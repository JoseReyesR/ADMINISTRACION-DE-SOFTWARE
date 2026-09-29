from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC

from pages.base_page import BasePage


class DetalleCasoPage(BasePage):
    """
    /dashboard/caso/:id. Sin data-cy propio: los <select> de estado y
    prioridad se ubican por XPath relativo al <label> que los precede,
    porque no tienen name ni id.

    IMPORTANTE: cada acción (cambiar estado, cambiar prioridad, guardar
    nota) dispara un alert() nativo del navegador tanto en éxito como en
    error (ver detallecaso.component.ts). Cada método de esta página
    acepta ese alert y devuelve su texto, para no dejar el diálogo abierto
    bloqueando el resto de la prueba.
    """

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

    # HU-006-AC1: datos del cliente y del incidente mostrados en el detalle
    DATOS_CLIENTE_NOMBRES = (By.XPATH, "//label[contains(text(),'Nombres Completos')]/following-sibling::p")
    DATOS_CLIENTE_CORREO = (By.XPATH, "//label[contains(text(),'Correo Electrónico')]/following-sibling::p")
    DATOS_INCIDENTE_TIPO = (By.XPATH, "//label[contains(text(),'Tipo Solicitud')]/following-sibling::p")
    DATOS_INCIDENTE_DESCRIPCION = (By.CSS_SELECTOR, ".bg-light.rounded.mt-1.border")

    # HU-006-AC2: evidencia adjunta, botón "Ver" -> modal con la imagen ampliada
    BOTON_VER_EVIDENCIA = (By.XPATH, "//button[contains(., 'Ver')][.//i[contains(@class,'bi-eye')]]")
    MODAL_IMAGEN = (By.CSS_SELECTOR, "img[alt='Evidencia del Reclamo']")
    MENSAJE_SIN_EVIDENCIA = (By.XPATH, "//*[contains(text(),'El cliente no adjuntó archivos para este caso.')]")

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
        # Tras aceptar el alert, el componente vuelve a pedir el caso (cargarDetalleCaso());
        # esperamos a que el bloque "Estado Actual" quede visible otra vez.
        self.esperar_visible(self.ESTADO_ACTUAL)
        return texto_alerta

    def cambiar_prioridad(self, id_prioridad: str) -> str:
        self.seleccionar_por_valor(self.SELECT_NUEVA_PRIORIDAD, id_prioridad)
        self.click(self.BTN_GUARDAR_PRIORIDAD)
        texto_alerta = self.aceptar_alerta_nativa()
        self.esperar_visible(self.PRIORIDAD_ACTUAL)
        return texto_alerta

    def _elegir_opcion_distinta_a_la_actual(self, select_locator):
        """
        Lee las opciones reales del <select> (tu catálogo de verdad en MySQL,
        cuyos nombres/ids no controlo desde aquí) y elige una distinta a la
        que está seleccionada, para no depender de un id o nombre fijo.
        """
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
        # OJO: el interruptor "Nota Privada" viene ACTIVADO por defecto (esInterno: true
        # en detallecaso.component.ts), así que una nota pública requiere apagarlo.
        # Se usa el click robusto (scroll + fallback JS) porque está cerca del borde
        # inferior de la vista, donde otros elementos flotantes lo pueden tapar.
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
        """Caso de error: el propio Angular valida esto en el cliente antes de llamar al backend."""
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

    def datos_del_cliente_y_del_caso(self) -> dict:
        """HU-006-AC1."""
        return {
            "nombres": self.texto_de(self.DATOS_CLIENTE_NOMBRES).strip(),
            "correo": self.texto_de(self.DATOS_CLIENTE_CORREO).strip(),
            "tipo_solicitud": self.texto_de(self.DATOS_INCIDENTE_TIPO).strip(),
            "descripcion": self.texto_de(self.DATOS_INCIDENTE_DESCRIPCION).strip(),
        }

    def tiene_evidencia_adjunta(self) -> bool:
        return len(self.driver.find_elements(*self.BOTON_VER_EVIDENCIA)) > 0

    def sin_evidencia_adjunta(self) -> bool:
        return len(self.driver.find_elements(*self.MENSAJE_SIN_EVIDENCIA)) > 0

    def ver_evidencia_ampliada(self):
        """HU-006-AC2: abre el modal con la imagen ampliada."""
        self.click(self.BOTON_VER_EVIDENCIA)
        self.esperar_visible(self.MODAL_IMAGEN)
        return self

    def modal_de_imagen_visible(self) -> bool:
        try:
            self.esperar_visible(self.MODAL_IMAGEN, timeout=5)
            return True
        except Exception:
            return False
