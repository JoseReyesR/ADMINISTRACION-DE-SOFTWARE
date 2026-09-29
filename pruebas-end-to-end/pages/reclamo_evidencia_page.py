from selenium.webdriver.common.by import By

from pages.base_page import BasePage


class ReclamoEvidenciaPage(BasePage):
    """/reclamo/evidencia (Paso 2 del flujo de reclamo)."""
    RUTA = "/reclamo/evidencia"

    RADIO_RECLAMO = (By.CSS_SELECTOR, "[data-cy='radio-reclamo']")
    RADIO_QUEJA = (By.CSS_SELECTOR, "[data-cy='radio-queja']")
    CARD_TIENDA_FISICA = (By.CSS_SELECTOR, "[data-cy='card-tienda']")
    CARD_WEB = (By.CSS_SELECTOR, "[data-cy='card-web']")
    SELECT_TIENDA = (By.CSS_SELECTOR, "[data-cy='select-tienda']")
    INPUT_FECHA_COMPRA = (By.CSS_SELECTOR, "input[name='fechaCompra']")
    INPUT_BOLETA = (By.CSS_SELECTOR, "[data-cy='input-boleta']")
    SELECT_CATEGORIA = (By.CSS_SELECTOR, "[data-cy='select-categoria']")
    SELECT_MOTIVO = (By.CSS_SELECTOR, "[data-cy='select-motivo']")
    INPUT_PRODUCTO = (By.CSS_SELECTOR, "[data-cy='input-producto']")
    INPUT_DESCRIPCION = (By.CSS_SELECTOR, "[data-cy='input-descripcion']")
    INPUT_FILE = (By.CSS_SELECTOR, "[data-cy='input-file']")
    BTN_CONTINUAR = (By.CSS_SELECTOR, "[data-cy='btn-continuar-paso2']")
    BTN_VOLVER = (By.CSS_SELECTOR, "[data-cy='btn-volver']")
    MENSAJE_ERROR = (By.CSS_SELECTOR, ".alert-danger")

    def esperar_cargada(self):
        # Espera a que el <select> de tienda tenga al menos una opción real
        # cargada desde /api/catalogos/tiendas (el <option disabled> inicial no cuenta).
        self._esperar().until(
            lambda d: len(self.driver.find_elements(By.CSS_SELECTOR, "[data-cy='select-tienda'] option")) > 1
        )
        return self

    def elegir_tipo_solicitud(self, tipo: str):
        """tipo: 'Reclamo' o 'Queja'."""
        locator = self.RADIO_RECLAMO if tipo == "Reclamo" else self.RADIO_QUEJA
        self.click(locator)
        return self

    def elegir_canal_compra(self, canal: str):
        """canal: 'Tienda Física' o 'Tottus.com'."""
        locator = self.CARD_TIENDA_FISICA if canal == "Tienda Física" else self.CARD_WEB
        self.click(locator)
        return self

    def seleccionar_primera_tienda_disponible(self):
        opciones = self.driver.find_elements(By.CSS_SELECTOR, "[data-cy='select-tienda'] option")
        valor = next(o.get_attribute("value") for o in opciones if o.get_attribute("value"))
        self.seleccionar_por_valor(self.SELECT_TIENDA, valor)
        return self

    def seleccionar_primera_categoria_disponible(self):
        opciones = self.driver.find_elements(By.CSS_SELECTOR, "[data-cy='select-categoria'] option")
        valor = next(o.get_attribute("value") for o in opciones if o.get_attribute("value"))
        self.seleccionar_por_valor(self.SELECT_CATEGORIA, valor)
        return self

    def seleccionar_primer_motivo_disponible(self):
        """Los <option> de motivo se filtran en el DOM según el tipo de solicitud elegido."""
        opciones = self.driver.find_elements(By.CSS_SELECTOR, "[data-cy='select-motivo'] option")
        valor = next(o.get_attribute("value") for o in opciones if o.get_attribute("value"))
        self.seleccionar_por_valor(self.SELECT_MOTIVO, valor)
        return self

    def llenar_fecha_compra(self, fecha_iso: str):
        """fecha_iso formato 'YYYY-MM-DD' (así lo pide <input type=date>)."""
        campo = self.esperar_visible(self.INPUT_FECHA_COMPRA)
        self.driver.execute_script(
            "arguments[0].value = arguments[1]; "
            "arguments[0].dispatchEvent(new Event('input', {bubbles:true}));"
            "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
            campo, fecha_iso,
        )
        return self

    def llenar_boleta(self, numero_boleta: str):
        self.escribir(self.INPUT_BOLETA, numero_boleta)
        return self

    def llenar_producto(self, producto: str):
        self.escribir(self.INPUT_PRODUCTO, producto)
        return self

    def llenar_descripcion(self, descripcion: str):
        self.escribir(self.INPUT_DESCRIPCION, descripcion)
        return self

    def adjuntar_archivo(self, ruta_absoluta_archivo: str):
        # El <input type=file> real está superpuesto (opacity:0) sobre la zona
        # de "arrastra o toca"; Selenium puede escribirle la ruta igual, sin
        # necesidad de que sea clickeable/visible para el ojo humano.
        campo = self.driver.find_element(*self.INPUT_FILE)
        campo.send_keys(ruta_absoluta_archivo)
        return self

    def llenar_formulario_completo(self, boleta: str, producto: str, descripcion: str,
                                    fecha_compra: str = "2026-01-15",
                                    tipo_solicitud: str = "Reclamo",
                                    canal_compra: str = "Tienda Física"):
        self.esperar_cargada()
        self.elegir_tipo_solicitud(tipo_solicitud)
        self.elegir_canal_compra(canal_compra)
        self.seleccionar_primera_tienda_disponible()
        self.llenar_fecha_compra(fecha_compra)
        self.llenar_boleta(boleta)
        self.seleccionar_primera_categoria_disponible()
        self.seleccionar_primer_motivo_disponible()
        self.llenar_producto(producto)
        self.llenar_descripcion(descripcion)
        return self

    def continuar(self):
        self.click(self.BTN_CONTINUAR)
        return self

    def boton_continuar_deshabilitado(self) -> bool:
        """
        HU-001-AC3 (Paso 2): a diferencia del Paso 1, aquí no hay alert() de
        validación -- el formulario usa [disabled]="!incidenteForm.form.valid",
        así que un formulario incompleto simplemente deja el botón inactivo.
        """
        return not self.driver.find_element(*self.BTN_CONTINUAR).is_enabled()
