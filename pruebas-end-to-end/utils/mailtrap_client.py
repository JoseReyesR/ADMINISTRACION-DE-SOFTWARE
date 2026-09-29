"""
Cliente mínimo de la API de Mailtrap (Email Sandbox / Testing API) para
verificar, desde las pruebas E2E, que el backend realmente envió un correo
y qué contenido tiene -- sin depender de una bandeja de correo real.

Requiere 3 variables de entorno (ver README para cómo obtenerlas en
mailtrap.io -> tu Sandbox -> pestaña "Integration" / "Settings > API Tokens"):

  MAILTRAP_API_TOKEN    Token de API (Settings > API Tokens)
  MAILTRAP_ACCOUNT_ID    Id numérico de tu cuenta Mailtrap
  MAILTRAP_INBOX_ID      Id numérico del Sandbox/inbox de pruebas

El backend (application.properties o el perfil que uses para correr estas
pruebas) debe apuntar su SMTP a ese mismo Sandbox:
  spring.mail.host=sandbox.smtp.mailtrap.io
  spring.mail.port=2525
  spring.mail.username=<usuario del sandbox>
  spring.mail.password=<password del sandbox>

Mailtrap Sandbox captura TODO lo que se envía con esas credenciales,
sin importar la dirección "to" -- por eso estas pruebas funcionan
independientemente de a qué dirección esté (mal) configurado el envío
en el código (ver nota sobre CORREO_DESTINO hardcodeado).
"""
import os
import time

import requests

API_BASE = "https://mailtrap.io/api/accounts/{account_id}/inboxes/{inbox_id}/messages"


class MailtrapNoConfigurado(Exception):
    """Faltan variables de entorno para hablar con la API de Mailtrap."""


def _config():
    token = os.environ.get("MAILTRAP_API_TOKEN")
    account_id = os.environ.get("MAILTRAP_ACCOUNT_ID")
    inbox_id = os.environ.get("MAILTRAP_INBOX_ID")
    if not (token and account_id and inbox_id):
        raise MailtrapNoConfigurado(
            "Define MAILTRAP_API_TOKEN, MAILTRAP_ACCOUNT_ID y MAILTRAP_INBOX_ID "
            "para correr las pruebas que verifican el envío de correos."
        )
    return token, account_id, inbox_id


def _listar_mensajes():
    token, account_id, inbox_id = _config()
    url = API_BASE.format(account_id=account_id, inbox_id=inbox_id)
    respuesta = requests.get(url, headers={"Api-Token": token}, timeout=10)
    respuesta.raise_for_status()
    return respuesta.json()


def _cuerpo_texto(message_id) -> str:
    token, account_id, inbox_id = _config()
    url = API_BASE.format(account_id=account_id, inbox_id=inbox_id) + f"/{message_id}/body.txt"
    respuesta = requests.get(url, headers={"Api-Token": token}, timeout=10)
    respuesta.raise_for_status()
    return respuesta.text


def esperar_correo_con_asunto(fragmento_asunto: str, timeout: int = 20, intervalo: float = 1.5) -> dict:
    """
    Sondea el inbox hasta encontrar un mensaje cuyo 'subject' contenga
    `fragmento_asunto` (por ejemplo, el código de seguimiento del caso, que
    es único por prueba). Necesario porque el envío es @Async: el mensaje
    puede tardar un instante en aparecer después de que la petición HTTP ya
    respondió 200.

    Devuelve un dict con 'id', 'subject', 'to_email', y 'cuerpo' (texto
    plano). Lanza AssertionError si no aparece dentro del timeout.
    """
    limite = time.time() + timeout
    ultimo_intento_mensajes = []
    while time.time() < limite:
        mensajes = _listar_mensajes()
        ultimo_intento_mensajes = mensajes
        for mensaje in mensajes:
            if fragmento_asunto in (mensaje.get("subject") or ""):
                mensaje["cuerpo"] = _cuerpo_texto(mensaje["id"])
                return mensaje
        time.sleep(intervalo)

    asuntos_vistos = [m.get("subject") for m in ultimo_intento_mensajes[:10]]
    raise AssertionError(
        f"No llegó ningún correo con '{fragmento_asunto}' en el asunto dentro de {timeout}s. "
        f"Últimos asuntos vistos en el inbox: {asuntos_vistos}"
    )


def no_debe_llegar_correo_con_asunto(fragmento_asunto: str, espera: int = 8):
    """
    Verifica que NO llegue ningún correo con ese fragmento en el asunto
    durante `espera` segundos (para probar, por ejemplo, que una nota
    interna/privada no dispara notificación).
    """
    limite = time.time() + espera
    while time.time() < limite:
        mensajes = _listar_mensajes()
        for mensaje in mensajes:
            if fragmento_asunto in (mensaje.get("subject") or ""):
                raise AssertionError(
                    f"Llegó un correo con '{fragmento_asunto}' en el asunto y no debía enviarse "
                    f"(subject real: '{mensaje.get('subject')}')."
                )
        time.sleep(1.5)
