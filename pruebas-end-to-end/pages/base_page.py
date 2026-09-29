"""Page object base: helpers de espera explícita comunes a todas las páginas."""
from selenium.common.exceptions import ElementClickInterceptedException, StaleElementReferenceException
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

TIMEOUT_POR_DEFECTO = 10


class BasePage:
    def __init__(self, driver, base_url: str):
        self.driver = driver
        self.base_url = base_url

    def _esperar(self, timeout: int = TIMEOUT_POR_DEFECTO) -> WebDriverWait:
        return WebDriverWait(self.driver, timeout)

    def ir_a(self, ruta: str):
        """Navega a una ruta relativa del frontend, ej. '/ingresar'."""
        self.driver.get(f"{self.base_url}{ruta}")
        return self

    def esperar_visible(self, locator, timeout: int = TIMEOUT_POR_DEFECTO):
        return self._esperar(timeout).until(EC.visibility_of_element_located(locator))

    def esperar_clickeable(self, locator, timeout: int = TIMEOUT_POR_DEFECTO):
        return self._esperar(timeout).until(EC.element_to_be_clickable(locator))

    def esperar_url_contiene(self, fragmento: str, timeout: int = TIMEOUT_POR_DEFECTO):
        return self._esperar(timeout).until(EC.url_contains(fragmento))

    def esperar_url_coincide(self, patron_regex: str, timeout: int = TIMEOUT_POR_DEFECTO):
        """
        Espera a que la URL cumpla una expresión regular. Más estricto que
        esperar_url_contiene: '/dashboard' está "contenido" en
        '/dashboard/caso/5', pero r'.*/dashboard/?$' solo coincide con el
        dashboard en sí.
        """
        return self._esperar(timeout).until(EC.url_matches(patron_regex))

    def esperar_desaparece(self, locator, timeout: int = TIMEOUT_POR_DEFECTO):
        return self._esperar(timeout).until(EC.invisibility_of_element_located(locator))

    def click(self, locator, timeout: int = TIMEOUT_POR_DEFECTO):
        """
        Click robusto:
          1) scroll del elemento al centro de la vista (en tablas largas la fila
             puede estar fuera de pantalla);
          2) si el widget flotante de accesibilidad (position: fixed, z-index
             9999, esquina inferior derecha) lo tapa -> click por JavaScript;
          3) si Angular re-renderizó el DOM justo en medio (StaleElement) ->
             se vuelve a buscar el elemento y se reintenta (hasta 3 veces).
        """
        ultimo_error = None
        for _ in range(3):
            try:
                elemento = self.esperar_clickeable(locator, timeout)
                self.driver.execute_script("arguments[0].scrollIntoView({block: 'center'});", elemento)
                try:
                    elemento.click()
                except ElementClickInterceptedException:
                    self.driver.execute_script("arguments[0].click();", elemento)
                return
            except StaleElementReferenceException as error:
                ultimo_error = error
        raise ultimo_error

    def escribir(self, locator, texto: str, timeout: int = TIMEOUT_POR_DEFECTO):
        campo = self.esperar_visible(locator, timeout)
        campo.clear()
        campo.send_keys(texto)

    def texto_de(self, locator, timeout: int = TIMEOUT_POR_DEFECTO) -> str:
        return self.esperar_visible(locator, timeout).text

    def seleccionar_por_valor(self, locator, valor: str, timeout: int = TIMEOUT_POR_DEFECTO):
        from selenium.webdriver.support.select import Select
        Select(self.esperar_visible(locator, timeout)).select_by_value(valor)

    def aceptar_alerta_nativa(self, timeout: int = TIMEOUT_POR_DEFECTO) -> str:
        """Espera un alert() nativo del navegador, lee su texto y lo acepta."""
        alerta = self._esperar(timeout).until(EC.alert_is_present())
        texto = alerta.text
        alerta.accept()
        return texto

    def obtener_token_local_storage(self, clave: str):
        return self.driver.execute_script(f"return window.localStorage.getItem('{clave}');")

    def limpiar_local_storage(self):
        self.driver.execute_script("window.localStorage.clear();")
