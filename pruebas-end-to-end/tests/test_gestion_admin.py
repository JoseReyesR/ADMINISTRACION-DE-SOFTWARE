"""
Gestión de un caso desde el BackOffice. Pruebas ordenadas por HU-CA:
  HU-005-CA01, HU-005-CA02_CA03, HU-006-CA01, HU-001-CA03 (+HU-006-CA02),
  HU-006-CA03, HU-007-CA01, HU-007-CA02a, HU-007-CA02b, HU-007-CA03
"""
import os

import pytest

from pages.login_page import LoginPage
from pages.dashboard_page import DashboardPage
from pages.detalle_caso_page import DetalleCasoPage
from pages.ingreso_cliente_page import IngresoClientePage
from pages.reclamo_datos_page import ReclamoDatosPage
from pages.reclamo_evidencia_page import ReclamoEvidenciaPage
from pages.reclamo_confirmacion_page import ReclamoConfirmacionPage
from utils.test_data import datos_cliente_de_prueba, registrar_dato_creado

CORREO_ADMIN_SEMILLA = "test@admin"
PASSWORD_ADMIN_SEMILLA = "1234"
ARCHIVO_EVIDENCIA = os.path.join(os.path.dirname(__file__), "fixtures", "evidencia_prueba.jpg")


def _loguearse_como_admin_y_abrir_dashboard(driver, frontend_url):
    LoginPage(driver, frontend_url).abrir().iniciar_sesion(CORREO_ADMIN_SEMILLA, PASSWORD_ADMIN_SEMILLA)
    dashboard_page = DashboardPage(driver, frontend_url)
    dashboard_page.esperar_cargada()
    return dashboard_page


@pytest.mark.backoffice
def test_HU005_CA01_dashboard_muestra_los_cuatro_indicadores_por_estado(driver, frontend_url, reclamo_de_prueba):
    """HU-005-CA01: resumen con Total, Pendientes, Urgentes y Resueltos."""
    dashboard_page = _loguearse_como_admin_y_abrir_dashboard(driver, frontend_url)
    dashboard_page.esperar_caso_en_la_tabla(reclamo_de_prueba["codigo"])

    kpis = dashboard_page.kpis()

    # Fórmula real (dashboard.component.ts): pendientes = totalCasos - resueltos;
    # urgentes se calcula aparte por prioridad y puede solaparse con las otras dos.
    assert kpis["total"] >= 1
    assert kpis["urgentes"] >= 0
    assert kpis["total"] == kpis["pendientes"] + kpis["resueltos"]


@pytest.mark.backoffice
def test_HU005_CA02_CA03_admin_ve_la_tabla_de_casos_y_abre_el_detalle(driver, frontend_url, reclamo_de_prueba):
    """HU-005-CA02 (tabla con los casos) y HU-005-CA03 (seleccionar uno abre su información)."""
    codigo = reclamo_de_prueba["codigo"]

    dashboard_page = _loguearse_como_admin_y_abrir_dashboard(driver, frontend_url)
    dashboard_page.ver_detalle_del_caso(codigo)

    detalle_page = DetalleCasoPage(driver, frontend_url)
    detalle_page.esperar_cargada()

    assert detalle_page.estado_actual() != ""


@pytest.mark.backoffice
def test_HU006_CA01_detalle_muestra_los_datos_del_cliente_y_del_incidente(driver, frontend_url, reclamo_de_prueba):
    """HU-006-CA01: el detalle muestra los datos del cliente y del incidente."""
    codigo = reclamo_de_prueba["codigo"]

    dashboard_page = _loguearse_como_admin_y_abrir_dashboard(driver, frontend_url)
    dashboard_page.ver_detalle_del_caso(codigo)

    detalle_page = DetalleCasoPage(driver, frontend_url).esperar_cargada()
    datos = detalle_page.datos_del_cliente_y_del_caso()

    assert datos["nombres"] != ""
    assert reclamo_de_prueba["correo"] in datos["correo"]
    assert datos["tipo_solicitud"] in ("Reclamo", "Queja")
    assert datos["descripcion"] != ""


@pytest.mark.backoffice
def test_HU001_CA03_evidencia_se_adjunta_y_HU006_CA02_se_puede_ver_ampliada(driver, frontend_url):
    """
    HU-001-CA03: el sistema permite adjuntar una evidencia válida al reclamo.
    HU-006-CA02: el analista puede visualizar esa evidencia ampliada.
    Requiere un caso creado con archivo adjunto (por eso no usa el fixture
    reclamo_de_prueba, que no adjunta ninguno).
    """
    datos = datos_cliente_de_prueba()

    IngresoClientePage(driver, frontend_url).abrir().continuar_como_invitado()
    datos_page = ReclamoDatosPage(driver, frontend_url).esperar_cargada()
    datos_page.llenar_datos_personales(datos)
    datos_page.continuar()

    evidencia_page = ReclamoEvidenciaPage(driver, frontend_url)
    evidencia_page.llenar_formulario_completo(
        boleta="TEST-BOLETA-EVIDENCIA", producto="Producto con evidencia", descripcion="Caso con evidencia adjunta."
    )
    evidencia_page.adjuntar_archivo(ARCHIVO_EVIDENCIA)
    evidencia_page.continuar()

    confirmacion_page = ReclamoConfirmacionPage(driver, frontend_url).esperar_cargada()
    codigo = confirmacion_page.obtener_codigo_seguimiento()
    registrar_dato_creado("Reclamo (con evidencia)", codigo, extra="usado para probar el visor de imagen")

    dashboard_page = _loguearse_como_admin_y_abrir_dashboard(driver, frontend_url)
    dashboard_page.ver_detalle_del_caso(codigo)

    detalle_page = DetalleCasoPage(driver, frontend_url).esperar_cargada()
    assert detalle_page.tiene_evidencia_adjunta()  # HU-001-CA03: se adjuntó correctamente

    detalle_page.ver_evidencia_ampliada()
    assert detalle_page.modal_de_imagen_visible()  # HU-006-CA02: se puede ver ampliada


@pytest.mark.backoffice
def test_HU006_CA03_detalle_de_caso_inexistente_muestra_alerta(driver, frontend_url):
    """HU-006-CA03: caso inexistente -> alerta indicando que no se pudo cargar."""
    _loguearse_como_admin_y_abrir_dashboard(driver, frontend_url)

    driver.get(f"{frontend_url}/dashboard/caso/999999999")

    detalle_page = DetalleCasoPage(driver, frontend_url)
    alerta = detalle_page.esperar_alerta_de_caso_no_encontrado()

    assert "no se pudo cargar" in alerta.lower()


@pytest.mark.backoffice
def test_HU007_CA01_admin_actualiza_el_estado_de_forma_independiente(driver, frontend_url, reclamo_de_prueba):
    """HU-007-CA01: seleccionar un nuevo estado y confirmar actualiza el caso, de forma independiente a las notas."""
    codigo = reclamo_de_prueba["codigo"]

    dashboard_page = _loguearse_como_admin_y_abrir_dashboard(driver, frontend_url)
    dashboard_page.ver_detalle_del_caso(codigo)

    detalle_page = DetalleCasoPage(driver, frontend_url).esperar_cargada()
    estado_anterior, estado_nuevo, alerta = detalle_page.cambiar_a_un_estado_diferente()

    assert "actualizado" in alerta.lower()
    assert detalle_page.estado_actual() == estado_nuevo
    assert detalle_page.estado_actual() != estado_anterior


@pytest.mark.backoffice
def test_HU007_CA02a_admin_registra_una_nota_publica(driver, frontend_url, reclamo_de_prueba):
    """HU-007-CA02 (variante pública): la nota queda visible para el cliente."""
    codigo = reclamo_de_prueba["codigo"]
    comentario = f"TEST_Nota_publica_{codigo}"

    dashboard_page = _loguearse_como_admin_y_abrir_dashboard(driver, frontend_url)
    dashboard_page.ver_detalle_del_caso(codigo)

    detalle_page = DetalleCasoPage(driver, frontend_url).esperar_cargada()
    alerta = detalle_page.agregar_nota(comentario, es_interna=False)

    assert "guardada" in alerta.lower()
    assert detalle_page.historial_contiene_texto(comentario)


@pytest.mark.backoffice
def test_HU007_CA02b_admin_registra_una_nota_privada(driver, frontend_url, reclamo_de_prueba):
    """HU-007-CA02 (variante privada/interna): la nota solo es visible para el equipo interno."""
    codigo = reclamo_de_prueba["codigo"]
    comentario = f"TEST_Nota_interna_{codigo}"

    dashboard_page = _loguearse_como_admin_y_abrir_dashboard(driver, frontend_url)
    dashboard_page.ver_detalle_del_caso(codigo)

    detalle_page = DetalleCasoPage(driver, frontend_url).esperar_cargada()
    alerta = detalle_page.agregar_nota(comentario, es_interna=True)

    assert "guardada" in alerta.lower()
    assert detalle_page.historial_contiene_texto(comentario)


@pytest.mark.backoffice
def test_HU007_CA03_admin_intenta_guardar_nota_vacia(driver, frontend_url, reclamo_de_prueba):
    """HU-007-CA03: sin comentario, el sistema avisa que es obligatorio y no la registra."""
    codigo = reclamo_de_prueba["codigo"]

    dashboard_page = _loguearse_como_admin_y_abrir_dashboard(driver, frontend_url)
    dashboard_page.ver_detalle_del_caso(codigo)

    detalle_page = DetalleCasoPage(driver, frontend_url).esperar_cargada()
    alerta = detalle_page.intentar_guardar_nota_vacia()

    assert "comentario" in alerta.lower()
