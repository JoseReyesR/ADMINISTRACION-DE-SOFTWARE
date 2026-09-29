from selenium.webdriver.common.by import By

from pages.base_page import BasePage


class ConsultaInvitadoPage(BasePage):
    """
    /consulta. El botón "Consultar Estado" se deshabilita nativamente
    mientras el formulario (documento + código, ambos "required", con
    patrón regex sobre el documento) sea inválido -- no hay alert().
    """
    RUTA = "/consulta"

    SELECT_TIPO_DOC = (By.CSS_SELECTOR, "[data-cy='select-tipo-doc']")
    INPUT_NUM_DOC = (By.CSS_SELECTOR, "[data-cy='input-num-doc']")
    INPUT_CODIGO = (By.CSS_SELECTOR, "[data-cy='input-codigo']")
    BTN_CONSULTAR = (By.CSS_SELECTOR, "[data-cy='btn-consultar']")

    ALERTA_ENCONTRADO = (By.CSS_SELECTOR, ".alert-success")
    ALERTA_NO_ENCONTRADO = (By.CSS_SELECTOR, ".alert-danger")

    # Campos del resultado (HU-003-AC3): código, motivo, fecha, estado
    RESULTADO_CODIGO = (By.XPATH, "//strong[text()='Código:']/parent::p")
    RESULTADO_MOTIVO = (By.XPATH, "//strong[text()='Motivo:']/parent::p")
    RESULTADO_FECHA = (By.XPATH, "//strong[text()='Fecha:']/parent::p")
    RESULTADO_ESTADO = (By.XPATH, "//strong[text()='Estado:']/following-sibling::span")

    BTN_VER_DETALLES = (By.XPATH, "//button[contains(., 'Ver Detalles')]")
    BTN_VER_HISTORIAL = (By.XPATH, "//button[contains(., 'Ver Historial')]")

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
            self.esperar_visible(self.ALERTA_ENCONTRADO, timeout=8)
            return True
        except Exception:
            return False

    def caso_no_encontrado(self) -> bool:
        try:
            self.esperar_visible(self.ALERTA_NO_ENCONTRADO, timeout=8)
            return True
        except Exception:
            return False

    def datos_del_resultado(self) -> dict:
        """HU-003-AC3: código, motivo, fecha y estado mostrados tras una consulta exitosa."""
        return {
            "codigo": self.texto_de(self.RESULTADO_CODIGO),
            "motivo": self.texto_de(self.RESULTADO_MOTIVO),
            "fecha": self.texto_de(self.RESULTADO_FECHA),
            "estado": self.texto_de(self.RESULTADO_ESTADO),
        }

    def boton_consultar_deshabilitado(self) -> bool:
        return not self.driver.find_element(*self.BTN_CONSULTAR).is_enabled()
