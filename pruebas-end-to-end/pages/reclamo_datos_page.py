from selenium.webdriver.common.by import By

from pages.base_page import BasePage


class ReclamoDatosPage(BasePage):
    RUTA = "/reclamo/datos"

    SELECT_TIPO_DOCUMENTO = (By.CSS_SELECTOR, "select[name='tipoDocumento']")
    INPUT_NUMERO_DOCUMENTO = (By.CSS_SELECTOR, "input[name='numeroDocumento']")
    INPUT_NOMBRES = (By.CSS_SELECTOR, "input[name='nombres']")
    INPUT_APELLIDOS = (By.CSS_SELECTOR, "input[name='apellidos']")
    INPUT_CORREO = (By.CSS_SELECTOR, "input[name='correo']")
    INPUT_CELULAR = (By.CSS_SELECTOR, "input[name='celular']")
    BTN_CONTINUAR = (By.CSS_SELECTOR, "[data-cy='btn-continuar-paso1']")
    BTN_VOLVER = (By.CSS_SELECTOR, "[data-cy='btn-volver-login']")
    MENSAJE_BIENVENIDA = (By.CSS_SELECTOR, ".alert-success")

    def esperar_cargada(self):
        self.esperar_visible(self.INPUT_NUMERO_DOCUMENTO)
        return self

    def llenar_datos_personales(self, datos: dict):
        """`datos` con las llaves de utils.test_data.datos_cliente_de_prueba()."""
        self.escribir(self.INPUT_NUMERO_DOCUMENTO, datos["numero_documento"])
        self.escribir(self.INPUT_NOMBRES, datos["nombres"])
        self.escribir(self.INPUT_APELLIDOS, datos["apellidos"])
        self.escribir(self.INPUT_CORREO, datos["correo"])
        self.escribir(self.INPUT_CELULAR, datos["celular"])
        return self

    def llenar_datos_personales_predefinidos(self, datos: dict):
            """`datos` con las llaves de utils.test_data.datos_cliente_de_prueba()."""
            self.escribir(self.INPUT_NUMERO_DOCUMENTO, datos["numero_documento"])
            self.escribir(self.INPUT_NOMBRES, datos["nombres"])
            self.escribir(self.INPUT_APELLIDOS, datos["apellidos"])
            self.escribir(self.INPUT_CORREO, datos["correo"])
            self.escribir(self.INPUT_CELULAR, datos["celular"])
            return self

    def continuar(self):
        self.click(self.BTN_CONTINUAR)
        return self

    def datos_precargados(self) -> bool:
        """True si el mensaje de bienvenida (datos precargados de sesión) está visible."""
        try:
            self.esperar_visible(self.MENSAJE_BIENVENIDA, timeout=3)
            return True
        except Exception:
            return False

    def valor_campo_nombres(self) -> str:
        return self.esperar_visible(self.INPUT_NOMBRES).get_attribute("value")
