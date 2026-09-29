from selenium.webdriver.common.by import By

from pages.base_page import BasePage


class LoginPage(BasePage):
    """
    /login (acceso interno / backoffice). Este componente no tiene
    atributos data-cy, así que se localiza por el atributo `name` del
    formulario (name="correo" / name="password") y por el botón submit.
    """
    RUTA = "/login"

    INPUT_CORREO = (By.CSS_SELECTOR, "input[name='correo']")
    INPUT_PASSWORD = (By.CSS_SELECTOR, "input[name='password']")
    BTN_INGRESAR = (By.CSS_SELECTOR, "button[type='submit']")
    ALERTA_ERROR = (By.CSS_SELECTOR, ".alert-danger")

    def abrir(self):
        self.ir_a(self.RUTA)
        self.esperar_visible(self.INPUT_CORREO)
        return self

    def iniciar_sesion(self, correo: str, password: str):
        self.escribir(self.INPUT_CORREO, correo)
        self.escribir(self.INPUT_PASSWORD, password)
        self.click(self.BTN_INGRESAR)
        return self

    def mensaje_error(self) -> str:
        return self.texto_de(self.ALERTA_ERROR)
