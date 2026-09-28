"""
Login del BackOffice (/login), usado por el personal interno
(Administrador). Cuenta real sembrada por SetupDataLoader: test@admin / 1234.
"""
import pytest

from pages.login_page import LoginPage
from pages.dashboard_page import DashboardPage

CORREO_ADMIN_SEMILLA = "test@admin"
PASSWORD_ADMIN_SEMILLA = "1234"


@pytest.mark.backoffice
def test_login_admin_con_credenciales_correctas_entra_al_dashboard(driver, frontend_url):
    LoginPage(driver, frontend_url).abrir().iniciar_sesion(CORREO_ADMIN_SEMILLA, PASSWORD_ADMIN_SEMILLA)

    dashboard_page = DashboardPage(driver, frontend_url)
    dashboard_page.esperar_cargada()

    assert driver.current_url.rstrip("/").endswith("/dashboard")
    assert dashboard_page.obtener_token_local_storage("token"), "El login debería guardar el token JWT"


@pytest.mark.backoffice
def test_login_admin_con_password_incorrecta_muestra_error(driver, frontend_url):
    login_page = LoginPage(driver, frontend_url).abrir()
    login_page.iniciar_sesion(CORREO_ADMIN_SEMILLA, "clave-incorrecta")

    assert "incorrectas" in login_page.mensaje_error().lower()


@pytest.mark.backoffice
def test_login_admin_con_correo_inexistente_muestra_error(driver, frontend_url):
    login_page = LoginPage(driver, frontend_url).abrir()
    login_page.iniciar_sesion("no-existe@tottus.com", "cualquiera")

    assert login_page.mensaje_error() != ""
