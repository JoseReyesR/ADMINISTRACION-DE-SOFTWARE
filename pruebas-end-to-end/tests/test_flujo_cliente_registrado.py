"""
Flujo de CLIENTE REGISTRADO: login en /ingresar con una cuenta real
(cliente@tottus.com, sembrada por SetupDataLoader), verifica que sus datos
se precargan en el paso 1, completa un reclamo, y confirma que aparece en
/mis-casos.
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
def test_cliente_registrado_ve_sus_datos_precargados_al_loguearse(driver, frontend_url):
    ingreso_page = IngresoClientePage(driver, frontend_url).abrir()
    ingreso_page.iniciar_sesion_como_cliente(CORREO_CLIENTE_SEMILLA, PASSWORD_CLIENTE_SEMILLA)

    datos_page = ReclamoDatosPage(driver, frontend_url)
    datos_page.esperar_cargada()

    assert datos_page.datos_precargados(), "Debería mostrarse el mensaje de bienvenida con datos precargados"


@pytest.mark.cliente
def test_cliente_registrado_crea_reclamo_y_aparece_en_mis_casos(driver, frontend_url):
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
def test_login_cliente_con_password_incorrecta_muestra_error(driver, frontend_url):
    ingreso_page = IngresoClientePage(driver, frontend_url).abrir()
    ingreso_page.iniciar_sesion_como_cliente(CORREO_CLIENTE_SEMILLA, "clave-incorrecta")

    assert "incorrectas" in ingreso_page.mensaje_error().lower()
