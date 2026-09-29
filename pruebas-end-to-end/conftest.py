"""
Fixtures compartidos por toda la suite de pruebas E2E.

Estas pruebas corren contra el frontend Angular y el backend Spring Boot
REALES (ambos deben estar levantados por ti antes de ejecutar pytest), y
contra tu base de datos MySQL real. No hay mocks ni base de datos en
memoria: es un end-to-end de verdad.

Configuración por variables de entorno (todas opcionales, con defaults
razonables):
  FRONTEND_URL   -> por defecto http://localhost:4200 (ng serve)
  HEADLESS       -> "true"/"false", por defecto "false" (navegador visible)
  BROWSER_WIDTH  -> por defecto 1440
  BROWSER_HEIGHT -> por defecto 900

Ejemplo para correr en headless:
    HEADLESS=true pytest
"""
import os

import pytest
from selenium import webdriver
from selenium.webdriver.chrome.options import Options


def _leer_bool_env(nombre: str, default: bool) -> bool:
    valor = os.environ.get(nombre)
    if valor is None:
        return default
    return valor.strip().lower() in ("1", "true", "yes", "si", "sí")


@pytest.fixture(scope="session")
def frontend_url() -> str:
    """URL base del frontend Angular (ng serve)."""
    return os.environ.get("FRONTEND_URL", "http://localhost:4200")


@pytest.fixture
def driver():
    """
    WebDriver de Chrome, uno nuevo por cada test (aislamiento total: cada
    test arranca con localStorage/cookies vacíos, como un usuario nuevo).

    Usa el Selenium Manager incorporado en Selenium >= 4.6, que descarga y
    gestiona el chromedriver automáticamente: no necesitas instalarlo a mano.
    """
    opciones = Options()

    if _leer_bool_env("HEADLESS", False):
        opciones.add_argument("--headless=new")

    ancho = os.environ.get("BROWSER_WIDTH", "1440")
    alto = os.environ.get("BROWSER_HEIGHT", "900")
    opciones.add_argument(f"--window-size={ancho},{alto}")
    opciones.add_argument("--disable-notifications")

    prefs = {
        "credentials_enable_service": False,
        "profile.password_manager_enabled": False,
        "profile.password_manager_leak_detection": False,
    }
    opciones.add_experimental_option("prefs", prefs)
    opciones.add_argument("--disable-save-password-bubble")
    opciones.add_experimental_option("excludeSwitches", ["enable-automation"])
    opciones.add_experimental_option("useAutomationExtension", False)

    navegador = webdriver.Chrome(options=opciones)
    navegador.implicitly_wait(0)  # usamos esperas explícitas (WebDriverWait) en las page objects

    yield navegador

    navegador.quit()


@pytest.fixture
def reclamo_de_prueba(driver, frontend_url):
    """
    Crea un reclamo real vía la UI (como invitado) y devuelve sus datos, para
    que otras pruebas (ej. de backoffice) tengan un caso real con el que
    trabajar sin repetir todo el flujo de creación en cada archivo de test.
    """
    from pages.ingreso_cliente_page import IngresoClientePage
    from pages.reclamo_datos_page import ReclamoDatosPage
    from pages.reclamo_evidencia_page import ReclamoEvidenciaPage
    from pages.reclamo_confirmacion_page import ReclamoConfirmacionPage
    from utils.test_data import datos_cliente_de_prueba, registrar_dato_creado

    datos = datos_cliente_de_prueba()

    IngresoClientePage(driver, frontend_url).abrir().continuar_como_invitado()

    datos_page = ReclamoDatosPage(driver, frontend_url)
    datos_page.esperar_cargada()
    datos_page.llenar_datos_personales(datos)
    datos_page.continuar()

    evidencia_page = ReclamoEvidenciaPage(driver, frontend_url)
    evidencia_page.llenar_formulario_completo(
        boleta="TEST-BOLETA-FIXTURE",
        producto="Producto de prueba (fixture compartido)",
        descripcion="Reclamo creado automáticamente como fixture para otra prueba E2E.",
    )
    evidencia_page.continuar()

    confirmacion_page = ReclamoConfirmacionPage(driver, frontend_url)
    confirmacion_page.esperar_cargada()
    codigo = confirmacion_page.obtener_codigo_seguimiento()

    registrar_dato_creado("Reclamo (fixture compartido)", codigo, extra=f"dni={datos['numero_documento']}")

    return {"codigo": codigo, **datos}


@pytest.fixture
def requiere_mailtrap():
    """
    Los tests de notificación por correo (test_notificaciones_correo.py) lo piden
    como parámetro para saltarse limpiamente (en vez de fallar) si todavía no
    configuraste MAILTRAP_API_TOKEN / MAILTRAP_ACCOUNT_ID / MAILTRAP_INBOX_ID.
    """
    from utils.mailtrap_client import MailtrapNoConfigurado, _config
    try:
        _config()
    except MailtrapNoConfigurado as error:
        pytest.skip(str(error))
