"""
Gestión de un caso desde el BackOffice: el administrador ve el caso en el
dashboard, entra al detalle, cambia estado y prioridad, y agrega notas al
historial (pública e interna). Incluye también casos de error.
"""
import pytest

from pages.login_page import LoginPage
from pages.dashboard_page import DashboardPage
from pages.detalle_caso_page import DetalleCasoPage

CORREO_ADMIN_SEMILLA = "test@admin"
PASSWORD_ADMIN_SEMILLA = "1234"


def _loguearse_como_admin_y_abrir_dashboard(driver, frontend_url):
    LoginPage(driver, frontend_url).abrir().iniciar_sesion(CORREO_ADMIN_SEMILLA, PASSWORD_ADMIN_SEMILLA)
    dashboard_page = DashboardPage(driver, frontend_url)
    dashboard_page.esperar_cargada()
    return dashboard_page


@pytest.mark.backoffice
def test_admin_encuentra_el_caso_en_el_dashboard_y_abre_su_detalle(driver, frontend_url, reclamo_de_prueba):
    codigo = reclamo_de_prueba["codigo"]

    dashboard_page = _loguearse_como_admin_y_abrir_dashboard(driver, frontend_url)
    dashboard_page.ver_detalle_del_caso(codigo)

    detalle_page = DetalleCasoPage(driver, frontend_url)
    detalle_page.esperar_cargada()

    assert detalle_page.estado_actual() != ""


@pytest.mark.backoffice
def test_admin_cambia_el_estado_del_caso(driver, frontend_url, reclamo_de_prueba):
    codigo = reclamo_de_prueba["codigo"]

    dashboard_page = _loguearse_como_admin_y_abrir_dashboard(driver, frontend_url)
    dashboard_page.ver_detalle_del_caso(codigo)

    detalle_page = DetalleCasoPage(driver, frontend_url).esperar_cargada()
    estado_anterior, estado_nuevo, alerta = detalle_page.cambiar_a_un_estado_diferente()

    assert "actualizado" in alerta.lower()
    assert detalle_page.estado_actual() == estado_nuevo
    assert detalle_page.estado_actual() != estado_anterior


@pytest.mark.backoffice
def test_admin_cambia_la_prioridad_del_caso(driver, frontend_url, reclamo_de_prueba):
    codigo = reclamo_de_prueba["codigo"]

    dashboard_page = _loguearse_como_admin_y_abrir_dashboard(driver, frontend_url)
    dashboard_page.ver_detalle_del_caso(codigo)

    detalle_page = DetalleCasoPage(driver, frontend_url).esperar_cargada()
    prioridad_anterior, prioridad_nueva, alerta = detalle_page.cambiar_a_una_prioridad_diferente()

    assert "actualizada" in alerta.lower()
    assert detalle_page.prioridad_actual() == prioridad_nueva
    assert detalle_page.prioridad_actual() != prioridad_anterior


@pytest.mark.backoffice
def test_admin_agrega_una_nota_publica_al_historial(driver, frontend_url, reclamo_de_prueba):
    codigo = reclamo_de_prueba["codigo"]
    comentario = f"TEST_Nota_publica_{codigo}"

    dashboard_page = _loguearse_como_admin_y_abrir_dashboard(driver, frontend_url)
    dashboard_page.ver_detalle_del_caso(codigo)

    detalle_page = DetalleCasoPage(driver, frontend_url).esperar_cargada()
    alerta = detalle_page.agregar_nota(comentario, es_interna=False)

    assert "guardada" in alerta.lower()
    assert detalle_page.historial_contiene_texto(comentario)


@pytest.mark.backoffice
def test_admin_agrega_una_nota_interna_al_historial(driver, frontend_url, reclamo_de_prueba):
    codigo = reclamo_de_prueba["codigo"]
    comentario = f"TEST_Nota_interna_{codigo}"

    dashboard_page = _loguearse_como_admin_y_abrir_dashboard(driver, frontend_url)
    dashboard_page.ver_detalle_del_caso(codigo)

    detalle_page = DetalleCasoPage(driver, frontend_url).esperar_cargada()
    alerta = detalle_page.agregar_nota(comentario, es_interna=True)

    assert "guardada" in alerta.lower()
    assert detalle_page.historial_contiene_texto(comentario)


# ==================== Casos de error ====================

@pytest.mark.backoffice
def test_admin_intenta_guardar_una_nota_vacia_muestra_alerta_de_validacion(driver, frontend_url, reclamo_de_prueba):
    codigo = reclamo_de_prueba["codigo"]

    dashboard_page = _loguearse_como_admin_y_abrir_dashboard(driver, frontend_url)
    dashboard_page.ver_detalle_del_caso(codigo)

    detalle_page = DetalleCasoPage(driver, frontend_url).esperar_cargada()
    alerta = detalle_page.intentar_guardar_nota_vacia()

    assert "comentario" in alerta.lower()


@pytest.mark.backoffice
def test_admin_abre_un_caso_inexistente_muestra_alerta_de_error(driver, frontend_url):
    _loguearse_como_admin_y_abrir_dashboard(driver, frontend_url)

    driver.get(f"{frontend_url}/dashboard/caso/999999999")

    detalle_page = DetalleCasoPage(driver, frontend_url)
    alerta = detalle_page.esperar_alerta_de_caso_no_encontrado()

    assert "no se pudo cargar" in alerta.lower()


@pytest.mark.backoffice
def test_acceder_al_dashboard_sin_haber_iniciado_sesion_redirige_al_login(driver, frontend_url):
    driver.get(f"{frontend_url}/dashboard")

    dashboard_page = DashboardPage(driver, frontend_url)
    dashboard_page.esperar_url_contiene("/login")
