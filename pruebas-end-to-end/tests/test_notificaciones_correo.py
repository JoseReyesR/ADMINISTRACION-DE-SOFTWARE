"""
HU-002 y HU-008: verifica que el backend REALMENTE envíe los correos, y con
qué contenido, usando la API de Mailtrap (ver utils/mailtrap_client.py).

Requiere que:
  1. Definas MAILTRAP_API_TOKEN, MAILTRAP_ACCOUNT_ID y MAILTRAP_INBOX_ID como
     variables de entorno (si faltan, estos tests se SALTAN automáticamente,
     no fallan -- ver el fixture requiere_mailtrap en conftest.py).
  2. El backend que estás corriendo tenga su spring.mail.* apuntando a ese
     mismo Sandbox de Mailtrap (sandbox.smtp.mailtrap.io), no a Gmail.

RECORDATORIO IMPORTANTE (pediste que te lo recuerde): EmailService.java
tiene hardcodeado CORREO_DESTINO = "pruebasdecorreoupn@gmail.com" y lo usa
para AMBOS correos (registro y actualización), ignorando por completo
reclamo.getUsuario().getCorreo(). Estos tests funcionan igual porque
Mailtrap Sandbox captura todo lo que se envía con esas credenciales sin
importar el "to" -- pero si arreglas ese hardcodeo para que use el correo
real del cliente, avísame para agregar una aserción extra aquí que
verifique el campo "to_email" del mensaje contra reclamo_de_prueba['correo'].
"""
import pytest

from pages.login_page import LoginPage
from pages.dashboard_page import DashboardPage
from pages.detalle_caso_page import DetalleCasoPage
from utils.mailtrap_client import esperar_correo_con_asunto, no_debe_llegar_correo_con_asunto

CORREO_ADMIN_SEMILLA = "test@admin"
PASSWORD_ADMIN_SEMILLA = "1234"


def _loguearse_como_admin_y_abrir_dashboard(driver, frontend_url):
    LoginPage(driver, frontend_url).abrir().iniciar_sesion(CORREO_ADMIN_SEMILLA, PASSWORD_ADMIN_SEMILLA)
    dashboard_page = DashboardPage(driver, frontend_url)
    dashboard_page.esperar_cargada()
    return dashboard_page


@pytest.mark.correo
def test_HU002_CA02_registrar_reclamo_envia_correo_de_confirmacion(driver, frontend_url, reclamo_de_prueba, requiere_mailtrap):
    """HU-002-CA02 (correo enviado) y HU-002-CA03 (contenido: código y resumen del caso)."""
    codigo = reclamo_de_prueba["codigo"]

    mensaje = esperar_correo_con_asunto(codigo)

    assert "Confirmación de Registro" in mensaje["subject"]
    assert codigo in mensaje["cuerpo"]
    assert "Reclamo" in mensaje["cuerpo"] or "Queja" in mensaje["cuerpo"]
    assert "Producto de prueba (fixture compartido)" in mensaje["cuerpo"]


@pytest.mark.correo
@pytest.mark.backoffice
def test_HU008_CA01_cambiar_estado_envia_correo_con_el_nuevo_estado(driver, frontend_url, reclamo_de_prueba, requiere_mailtrap):
    """HU-008-CA01: el correo debe identificar el caso y el nuevo estado."""
    codigo = reclamo_de_prueba["codigo"]

    dashboard_page = _loguearse_como_admin_y_abrir_dashboard(driver, frontend_url)
    dashboard_page.ver_detalle_del_caso(codigo)

    detalle_page = DetalleCasoPage(driver, frontend_url).esperar_cargada()
    _anterior, nuevo_estado, _alerta = detalle_page.cambiar_a_un_estado_diferente()

    # Búsqueda específica: "codigo" solo coincidiría también con el correo de
    # confirmación ya enviado al crear el caso (mismo código en el asunto).
    mensaje = esperar_correo_con_asunto(f"ACTUALIZACION DE TU CASO - {codigo}")

    assert "ACTUALIZACION DE TU CASO" in mensaje["subject"]
    assert nuevo_estado in mensaje["cuerpo"]


@pytest.mark.correo
@pytest.mark.backoffice
def test_HU008_CA02_nota_publica_envia_correo_con_el_comentario(driver, frontend_url, reclamo_de_prueba, requiere_mailtrap):
    """HU-008-CA02: el correo debe incluir el texto de la nota pública."""
    codigo = reclamo_de_prueba["codigo"]
    comentario = f"TEST_Correo_nota_publica_{codigo}"

    dashboard_page = _loguearse_como_admin_y_abrir_dashboard(driver, frontend_url)
    dashboard_page.ver_detalle_del_caso(codigo)

    detalle_page = DetalleCasoPage(driver, frontend_url).esperar_cargada()
    detalle_page.agregar_nota(comentario, es_interna=False)

    mensaje = esperar_correo_con_asunto(f"ACTUALIZACION DE TU CASO - {codigo}")

    assert "ACTUALIZACION DE TU CASO" in mensaje["subject"]
    assert comentario in mensaje["cuerpo"]
    assert "nota pública" in mensaje["cuerpo"].lower()


@pytest.mark.correo
@pytest.mark.backoffice
def test_HU008_CA03_nota_interna_no_envia_ningun_correo(driver, frontend_url, reclamo_de_prueba, requiere_mailtrap):
    """HU-008-CA03: una nota privada/interna NO debe notificar al cliente."""
    codigo = reclamo_de_prueba["codigo"]
    comentario = f"TEST_Correo_nota_interna_{codigo}"

    dashboard_page = _loguearse_como_admin_y_abrir_dashboard(driver, frontend_url)
    dashboard_page.ver_detalle_del_caso(codigo)

    detalle_page = DetalleCasoPage(driver, frontend_url).esperar_cargada()
    detalle_page.agregar_nota(comentario, es_interna=True)

    # Igual que arriba: si buscáramos solo "codigo", SIEMPRE encontraría el
    # correo de confirmación (que no tiene nada que ver con esta nota) y el
    # test fallaría siempre, sin importar si la nota interna notificó o no.
    no_debe_llegar_correo_con_asunto(f"ACTUALIZACION DE TU CASO - {codigo}")
