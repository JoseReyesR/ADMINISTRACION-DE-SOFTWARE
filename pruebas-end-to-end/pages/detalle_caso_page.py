from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC

from pages.base_page import BasePage


class DetalleCasoPage(BasePage):
    ESTADO_ACTUAL = (By.XPATH, "//label[contains(text(),'Estado Actual')]/following-sibling::div[1]")
    SELECT_NUEVO_ESTADO = (By.XPATH, "//label[contains(text(),'Actualizar Estado')]/following-sibling::select")
    BTN_GUARDAR_ESTADO = (By.XPATH, "//button[contains(., 'Guardar Cambios')]")

    PRIORIDAD_ACTUAL = (By.XPATH, "//label[contains(text(),'Prioridad Actual')]/following-sibling::div[1]")
    SELECT_NUEVA_PRIORIDAD = (By.XPATH, "//label[contains(text(),'Actualizar Prioridad')]/following-sibling::select")
    BTN_GUARDAR_PRIORIDAD = (By.XPATH, "//button[contains(., 'Actualizar Prioridad')]")

    TEXTAREA_NOTA = (By.CSS_SELECTOR, ".card-body textarea.form-control")
    CHECK_NOTA_INTERNA = (By.ID, "esInternoSwitch")
    BTN_GUARDAR_NOTA = (By.XPATH, "//button[contains(., 'Guardar Nota en Historial')]")
    NOTAS_HISTORIAL = (By.CSS_SELECTOR, ".border-start.border-4")

    def esperar_cargada(self):
        self.esperar_visible(self.ESTADO_ACTUAL)
        return self

    def esperar_alerta_de_caso_no_encontrado(self, timeout: int = 10) -> str:
        """Para el caso de error: navegar a un caso inexistente dispara alert()."""
        return self.aceptar_alerta_nativa(timeout)

    def estado_actual(self) -> str:
        return self.texto_de(self.ESTADO_ACTUAL).strip()

    def prioridad_actual(self) -> str:
        return self.texto_de(self.PRIORIDAD_ACTUAL).strip()

    def cambiar_estado(self, id_estado: str) -> str:
        self.seleccionar_por_valor(self.SELECT_NUEVO_ESTADO, id_estado)
        self.click(self.BTN_GUARDAR_ESTADO)
        texto_alerta = self.aceptar_alerta_nativa()
        self.esperar_visible(self.ESTADO_ACTUAL)
        return texto_alerta

    def cambiar_prioridad(self, id_prioridad: str) -> str:
        self.seleccionar_por_valor(self.SELECT_NUEVA_PRIORIDAD, id_prioridad)
        self.click(self.BTN_GUARDAR_PRIORIDAD)
        texto_alerta = self.aceptar_alerta_nativa()
        self.esperar_visible(self.PRIORIDAD_ACTUAL)
        return texto_alerta

    def _elegir_opcion_distinta_a_la_actual(self, select_locator):
        from selenium.webdriver.support.select import Select
        elemento = self.esperar_visible(select_locator)
        select = Select(elemento)
        actual = select.first_selected_option
        opciones = [
            o for o in select.options
            if o.get_attribute("value") and o.get_attribute("value") != actual.get_attribute("value")
        ]
        assert opciones, "El catálogo necesita al menos 2 opciones para probar un cambio."
        nueva = opciones[0]
        select.select_by_value(nueva.get_attribute("value"))
        return actual.text.strip(), nueva.text.strip()

    def cambiar_a_un_estado_diferente(self):
        """Devuelve (nombre_anterior, nombre_nuevo, texto_del_alert)."""
        anterior, nuevo = self._elegir_opcion_distinta_a_la_actual(self.SELECT_NUEVO_ESTADO)
        self.click(self.BTN_GUARDAR_ESTADO)
        alerta = self.aceptar_alerta_nativa()
        self.esperar_visible(self.ESTADO_ACTUAL)
        return anterior, nuevo, alerta

    def cambiar_a_una_prioridad_diferente(self):
        """Devuelve (nombre_anterior, nombre_nuevo, texto_del_alert)."""
        anterior, nuevo = self._elegir_opcion_distinta_a_la_actual(self.SELECT_NUEVA_PRIORIDAD)
        self.click(self.BTN_GUARDAR_PRIORIDAD)
        alerta = self.aceptar_alerta_nativa()
        self.esperar_visible(self.PRIORIDAD_ACTUAL)
        return anterior, nuevo, alerta

    def agregar_nota(self, comentario: str, es_interna: bool = False) -> str:
        self.escribir(self.TEXTAREA_NOTA, comentario)
        casilla = self.esperar_visible(self.CHECK_NOTA_INTERNA)
        if casilla.is_selected() != es_interna:
            self.click(self.CHECK_NOTA_INTERNA)
        self._esperar().until(
            lambda d: d.find_element(*self.CHECK_NOTA_INTERNA).is_selected() == es_interna,
            message=f"El interruptor de nota privada no quedó en {es_interna}",
        )
        self.click(self.BTN_GUARDAR_NOTA)
        texto_alerta = self.aceptar_alerta_nativa()
        return texto_alerta

    def intentar_guardar_nota_vacia(self) -> str:
        self.click(self.BTN_GUARDAR_NOTA)
        return self.aceptar_alerta_nativa()

    def cantidad_de_notas(self) -> int:
        return len(self.driver.find_elements(*self.NOTAS_HISTORIAL))

    def historial_contiene_texto(self, texto: str, timeout: int = 10) -> bool:
        locator = (By.XPATH, f"//div[contains(@class,'border-start')][contains(., \"{texto}\")]")
        try:
            self.esperar_visible(locator, timeout)
            return True
        except Exception:
            return False