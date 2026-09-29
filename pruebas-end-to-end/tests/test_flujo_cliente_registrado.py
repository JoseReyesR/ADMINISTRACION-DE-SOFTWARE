"""
Flujo de CLIENTE REGISTRADO. Pruebas ordenadas por HU-CA:
  HU-001-CA01b (+ HU-004-CA01), HU-004-CA02
"""
import pytest

from pages.ingreso_cliente_page import IngresoClientePage
from pages.reclamo_datos_page import ReclamoDatosPage
from pages.reclamo_evidencia_page import ReclamoEvidenciaPage
from pages.reclamo_confirmacion_page import ReclamoConfirmacionPage
from pages.mis_casos_page import MisCasosPage
from utils.test_data import registrar_dato_creado

CORREO_CLIENTE_SEMILLA = "cliente@tottus.com"
PASSWORD_CLIENTE_SEMILLA = "123456"


@pytest.mark.cliente
def test_HU001_CA01b_cliente_registrado_crea_reclamo_y_aparece_en_mis_casos(driver, frontend_url):
    """
    HU-001-CA01b: registro exitoso como cliente autenticado, sin evidencia
    (opcional, aquí ausente). También ejercita HU-004-CA01 (el cliente
    autenticado ve sus reclamos en Mis Casos).
    """
    ingreso_page = IngresoClientePage(driver, frontend_url).abrir()
    ingreso_page.iniciar_sesion_como_cliente(CORREO_CLIENTE_SEMILLA, PASSWORD_CLIENTE_SEMILLA)

    datos_page = ReclamoDatosPage(driver, frontend_url)
    datos_page.esperar_cargada()
    # Los datos ya vienen precargados por la sesión; solo avanzamos.
    datos_page.continuar()

    evidencia_page = ReclamoEvidenciaPage(driver, frontend_url)
    evidencia_page.llenar_formulario_completo(
        boleta="TEST-BOLETA-CLIENTE-0001",
        producto="Producto de prueba Selenium (cliente registrado)",
        descripcion="Reclamo creado por una prueba E2E como cliente registrado.",
        canal_compra="Tottus.com",
    )
    evidencia_page.continuar()

    confirmacion_page = ReclamoConfirmacionPage(driver, frontend_url)
    confirmacion_page.esperar_cargada()
    codigo = confirmacion_page.obtener_codigo_seguimiento()
    registrar_dato_creado("Reclamo (cliente registrado)", codigo, extra=f"correo={CORREO_CLIENTE_SEMILLA}")

    confirmacion_page.ir_a_mis_casos()

    mis_casos_page = MisCasosPage(driver, frontend_url)
    assert mis_casos_page.contiene_el_codigo(codigo), (
        f"El caso {codigo} debería aparecer en /mis-casos del cliente"
    )


@pytest.mark.cliente
def test_HU004_CA02_mis_casos_muestra_codigo_fecha_motivo_y_estado(driver, frontend_url):
    """HU-004-CA02: cada fila de Mis Casos debe mostrar código, fecha, motivo y estado."""
    ingreso_page = IngresoClientePage(driver, frontend_url).abrir()
    ingreso_page.iniciar_sesion_como_cliente(CORREO_CLIENTE_SEMILLA, PASSWORD_CLIENTE_SEMILLA)

    datos_page = ReclamoDatosPage(driver, frontend_url)
    datos_page.esperar_cargada()
    datos_page.continuar()

    evidencia_page = ReclamoEvidenciaPage(driver, frontend_url)
    evidencia_page.llenar_formulario_completo(
        boleta="TEST-BOLETA-CLIENTE-0002",
        producto="Producto de prueba Selenium (columnas Mis Casos)",
        descripcion="Reclamo creado para verificar las columnas de la tabla Mis Casos.",
    )
    evidencia_page.continuar()

    confirmacion_page = ReclamoConfirmacionPage(driver, frontend_url)
    confirmacion_page.esperar_cargada()
    codigo = confirmacion_page.obtener_codigo_seguimiento()
    registrar_dato_creado("Reclamo (cliente registrado)", codigo, extra="usado para verificar columnas de Mis Casos")
    confirmacion_page.ir_a_mis_casos()

    mis_casos_page = MisCasosPage(driver, frontend_url)
    fila = mis_casos_page.datos_de_la_fila(codigo)

    assert fila["codigo"] == codigo
    assert fila["fecha"] != ""
    assert fila["motivo"] != ""
    assert fila["estado"] != ""
