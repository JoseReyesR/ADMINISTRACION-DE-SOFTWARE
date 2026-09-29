from selenium.webdriver.common.by import By

from pages.base_page import BasePage


class ConsultaInvitadoPage(BasePage):
    RUTA = "/consulta"

    INPUT_NUM_DOC = (By.CSS_SELECTOR, "[data-cy='input-num-doc']")
    INPUT_CODIGO = (By.CSS_SELECTOR, "[data-cy='input-codigo']")
    BTN_CONSULTAR = (By.CSS_SELECTOR, "[data-cy='btn-consultar']")
    ALERTA_ENCONTRADO = (By.CSS_SELECTOR, ".alert-success")
    ALERTA_NO_ENCONTRADO = (By.CSS_SELECTOR, ".alert-danger")

    def abrir(self):
        self.ir_a(self.RUTA)
        self.esperar_visible(self.INPUT_NUM_DOC)
        return self

    def consultar(self, numero_documento: str, codigo_seguimiento: str):
        self.escribir(self.INPUT_NUM_DOC, numero_documento)
        self.escribir(self.INPUT_CODIGO, codigo_seguimiento)
        self.click(self.BTN_CONSULTAR)
        return self

    def caso_fue_encontrado(self) -> bool:
        try:
            self.esperar_visible(self.ALERTA_ENCONTRADO, timeout=5)
            return True
        except Exception:
            return False

    def caso_no_encontrado(self) -> bool:
        try:
            self.esperar_visible(self.ALERTA_NO_ENCONTRADO, timeout=5)
            return True
        except Exception:
            return False
