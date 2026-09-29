from selenium.webdriver.common.by import By

from pages.base_page import BasePage


class ReclamoDatosPage(BasePage):
    """
    /reclamo/datos (Paso 1). CORRECCIÓN: el formulario usa
    [disabled]="!datosForm.form.valid" -- igual que el Paso 2 -- con un
    <small class="text-danger"> por campo. El alert() que existe en
    reclamodatos.component.ts (siguientePaso()) es código MUERTO: el botón
    ya está deshabilitado antes de que ese código pueda ejecutarse con
    datos inválidos, así que nunca se dispara en la práctica.
    """
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

    MENSAJE_ERROR_DOCUMENTO = (By.XPATH, "//input[@name='numeroDocumento']/parent::div//small")
    MENSAJE_ERROR_NOMBRES = (By.XPATH, "//input[@name='nombres']/parent::div//small")
    MENSAJE_ERROR_APELLIDOS = (By.XPATH, "//input[@name='apellidos']/parent::div//small")
    MENSAJE_ERROR_CORREO = (By.XPATH, "//input[@name='correo']/parent::div//small")
    MENSAJE_ERROR_CELULAR = (By.XPATH, "//input[@name='celular']/parent::div//small")

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

    def llenar_datos_personales_con_override(self, datos: dict, **overrides):
        """
        Igual que llenar_datos_personales, pero permite forzar un valor
        inválido en un campo puntual (p. ej. celular='123') para probar
        HU-001-CA02 sin repetir todos los demás campos válidos en cada test.
        """
        datos_finales = {**datos, **overrides}
        return self.llenar_datos_personales(datos_finales)

    def continuar(self):
        self.click(self.BTN_CONTINUAR)
        return self

    def boton_continuar_deshabilitado(self) -> bool:
        """HU-001-CA02: con algún campo inválido, el botón 'Continuar' queda inactivo."""
        return not self.driver.find_element(*self.BTN_CONTINUAR).is_enabled()

    def _texto_mensaje(self, locator, timeout: int = 5) -> str:
        try:
            return self.esperar_visible(locator, timeout).text.strip()
        except Exception:
            return ""

    def mensaje_error_documento(self) -> str:
        return self._texto_mensaje(self.MENSAJE_ERROR_DOCUMENTO)

    def mensaje_error_nombres(self) -> str:
        return self._texto_mensaje(self.MENSAJE_ERROR_NOMBRES)

    def mensaje_error_correo(self) -> str:
        return self._texto_mensaje(self.MENSAJE_ERROR_CORREO)

    def mensaje_error_celular(self) -> str:
        return self._texto_mensaje(self.MENSAJE_ERROR_CELULAR)

    def datos_precargados(self) -> bool:
        """True si el mensaje de bienvenida (datos precargados de sesión) está visible."""
        try:
            self.esperar_visible(self.MENSAJE_BIENVENIDA, timeout=3)
            return True
        except Exception:
            return False

    def valor_campo_nombres(self) -> str:
        return self.esperar_visible(self.INPUT_NOMBRES).get_attribute("value")
