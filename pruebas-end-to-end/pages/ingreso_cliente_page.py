from selenium.webdriver.common.by import By

from pages.base_page import BasePage


class IngresoClientePage(BasePage):
    RUTA = "/ingresar"

    INPUT_CORREO = (By.CSS_SELECTOR, "[data-cy='input-correo-cliente']")
    INPUT_PASSWORD = (By.CSS_SELECTOR, "[data-cy='input-password-cliente']")
    BTN_INGRESAR = (By.CSS_SELECTOR, "[data-cy='btn-ingresar-cliente']")
    BTN_INVITADO = (By.CSS_SELECTOR, "[data-cy='btn-invitado']")
    ALERTA_ERROR = (By.CSS_SELECTOR, "[data-cy='alerta-error']")

    def abrir(self):
        self.ir_a(self.RUTA)
        self.esperar_visible(self.INPUT_CORREO)
        return self

    def iniciar_sesion_como_cliente(self, correo: str, password: str):
        self.escribir(self.INPUT_CORREO, correo)
        self.escribir(self.INPUT_PASSWORD, password)
        self.click(self.BTN_INGRESAR)
        return self

    def continuar_como_invitado(self):
        self.click(self.BTN_INVITADO)
        return self

    def mensaje_error(self) -> str:
        return self.texto_de(self.ALERTA_ERROR)
