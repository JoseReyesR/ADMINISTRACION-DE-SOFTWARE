"""
Flujo de INVITADO (sin cuenta). Pruebas ordenadas por HU-CA:
  HU-001-CA01a, HU-003-CA02, HU-003-CA03
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
def test_HU001_CA01a_invitado_registra_reclamo_con_evidencia_y_lo_consulta(driver, frontend_url):
    """
    HU-001-CA01a: registro exitoso como invitado, con datos obligatorios,
    detalle del incidente y evidencia adjunta (opcional, aquí presente).
    También ejercita HU-002-CA01 (código generado) y HU-003-CA01 (consulta
    con datos correctos muestra el caso).
    """
    datos = datos_cliente_de_prueba()

    IngresoClientePage(driver, frontend_url).abrir().continuar_como_invitado()

    datos_page = ReclamoDatosPage(driver, frontend_url)
    datos_page.esperar_cargada()
    datos_page.llenar_datos_personales(datos)
    datos_page.continuar()

    evidencia_page = ReclamoEvidenciaPage(driver, frontend_url)
    evidencia_page.llenar_formulario_completo(
        boleta="TEST-BOLETA-0001",
        producto="Producto de prueba Selenium",
        descripcion="Descripción generada automáticamente por una prueba E2E de Selenium.",
    )
    evidencia_page.adjuntar_archivo(ARCHIVO_EVIDENCIA)
    evidencia_page.continuar()

    confirmacion_page = ReclamoConfirmacionPage(driver, frontend_url)
    confirmacion_page.esperar_cargada()
    codigo = confirmacion_page.obtener_codigo_seguimiento()

    assert codigo and codigo != "Generando..."
    registrar_dato_creado("Reclamo (invitado)", codigo, extra=f"dni={datos['numero_documento']}, correo={datos['correo']}")

    consulta_page = ConsultaInvitadoPage(driver, frontend_url).abrir()
    consulta_page.consultar(datos["numero_documento"], codigo)

    assert consulta_page.caso_fue_encontrado(), (
        f"El caso {codigo} debería encontrarse con el DNI {datos['numero_documento']}"
    )


@pytest.mark.invitado
def test_HU003_CA02_consulta_con_dni_incorrecto_no_encuentra_el_caso(driver, frontend_url):
    """HU-003-CA02: documento o código incorrecto -> el sistema informa que no encontró el caso."""
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


@pytest.mark.invitado
def test_HU003_CA03_consulta_muestra_codigo_motivo_fecha_y_estado(driver, frontend_url):
    """HU-003-CA03: el resultado debe mostrar como mínimo código, motivo, fecha y estado."""
    datos = datos_cliente_de_prueba()

    IngresoClientePage(driver, frontend_url).abrir().continuar_como_invitado()

    datos_page = ReclamoDatosPage(driver, frontend_url)
    datos_page.esperar_cargada()
    datos_page.llenar_datos_personales(datos)
    datos_page.continuar()

    evidencia_page = ReclamoEvidenciaPage(driver, frontend_url)
    evidencia_page.llenar_formulario_completo(
        boleta="TEST-BOLETA-0003",
        producto="Producto de prueba Selenium",
        descripcion="Caso para verificar los campos mostrados en la consulta pública.",
    )
    evidencia_page.continuar()

    confirmacion_page = ReclamoConfirmacionPage(driver, frontend_url)
    confirmacion_page.esperar_cargada()
    codigo = confirmacion_page.obtener_codigo_seguimiento()
    registrar_dato_creado("Reclamo (invitado)", codigo, extra="usado para verificar campos de la consulta")

    consulta_page = ConsultaInvitadoPage(driver, frontend_url).abrir()
    consulta_page.consultar(datos["numero_documento"], codigo)
    assert consulta_page.caso_fue_encontrado()

    resultado = consulta_page.datos_del_resultado()
    assert codigo in resultado["codigo"]
    assert "Reclamo" in resultado["motivo"]
    assert resultado["fecha"] != ""
    assert resultado["estado"] != ""
