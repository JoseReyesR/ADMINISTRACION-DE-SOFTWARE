"""
Flujo de INVITADO (sin cuenta): entra a /ingresar, elige "Continuar sin
cuenta", llena el formulario de reclamo completo (con evidencia adjunta),
recibe un código de seguimiento, y lo usa en /consulta para verificar el
caso de forma pública.
"""
import os

import pytest

from pages.ingreso_cliente_page import IngresoClientePage
from pages.reclamo_datos_page import ReclamoDatosPage
from pages.reclamo_evidencia_page import ReclamoEvidenciaPage
from pages.reclamo_confirmacion_page import ReclamoConfirmacionPage
from pages.consulta_invitado_page import ConsultaInvitadoPage
from utils.test_data import datos_cliente_de_prueba, registrar_dato_creado

ARCHIVO_EVIDENCIA = os.path.join(os.path.dirname(__file__), "fixtures", "evidencia_prueba.jpg")


@pytest.mark.invitado
def test_invitado_crea_reclamo_y_lo_consulta_publicamente(driver, frontend_url):
    datos = datos_cliente_de_prueba()

    # --- Paso 0: entra como invitado ---
    IngresoClientePage(driver, frontend_url).abrir().continuar_como_invitado()

    # --- Paso 1: datos personales ---
    datos_page = ReclamoDatosPage(driver, frontend_url)
    datos_page.esperar_cargada()
    datos_page.llenar_datos_personales(datos)
    datos_page.continuar()

    # --- Paso 2: incidente + evidencia ---
    evidencia_page = ReclamoEvidenciaPage(driver, frontend_url)
    evidencia_page.llenar_formulario_completo(
        boleta="TEST-BOLETA-0001",
        producto="Producto de prueba Selenium",
        descripcion="Descripción generada automáticamente por una prueba E2E de Selenium.",
    )
    evidencia_page.adjuntar_archivo(ARCHIVO_EVIDENCIA)
    evidencia_page.continuar()

    # --- Paso 3: confirmación ---
    confirmacion_page = ReclamoConfirmacionPage(driver, frontend_url)
    confirmacion_page.esperar_cargada()
    codigo = confirmacion_page.obtener_codigo_seguimiento()

    assert codigo and codigo != "Generando..."
    registrar_dato_creado("Reclamo (invitado)", codigo, extra=f"dni={datos['numero_documento']}, correo={datos['correo']}")

    # --- Verificación cruzada: consulta pública con el código recién obtenido ---
    consulta_page = ConsultaInvitadoPage(driver, frontend_url).abrir()
    consulta_page.consultar(datos["numero_documento"], codigo)

    assert consulta_page.caso_fue_encontrado(), (
        f"El caso {codigo} debería encontrarse con el DNI {datos['numero_documento']}"
    )


@pytest.mark.invitado
def test_consulta_invitado_con_dni_incorrecto_no_encuentra_el_caso(driver, frontend_url):
    """Reutiliza el flujo de creación para luego consultar con un DNI que no es el del caso."""
    datos = datos_cliente_de_prueba()

    IngresoClientePage(driver, frontend_url).abrir().continuar_como_invitado()

    datos_page = ReclamoDatosPage(driver, frontend_url)
    datos_page.esperar_cargada()
    datos_page.llenar_datos_personales(datos)
    datos_page.continuar()

    evidencia_page = ReclamoEvidenciaPage(driver, frontend_url)
    evidencia_page.llenar_formulario_completo(
        boleta="TEST-BOLETA-0002",
        producto="Producto de prueba Selenium",
        descripcion="Caso para probar que un DNI incorrecto no da acceso al seguimiento.",
    )
    evidencia_page.continuar()

    confirmacion_page = ReclamoConfirmacionPage(driver, frontend_url)
    confirmacion_page.esperar_cargada()
    codigo = confirmacion_page.obtener_codigo_seguimiento()
    registrar_dato_creado("Reclamo (invitado)", codigo, extra="usado en prueba de DNI incorrecto")

    consulta_page = ConsultaInvitadoPage(driver, frontend_url).abrir()
    consulta_page.consultar("00000000", codigo)  # DNI que no coincide con el caso

    assert consulta_page.caso_no_encontrado()
