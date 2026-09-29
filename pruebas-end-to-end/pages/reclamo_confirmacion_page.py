from selenium.webdriver.common.by import By

from pages.base_page import BasePage


class ReclamoConfirmacionPage(BasePage):
    """/reclamo/confirmacion (pantalla final: código de seguimiento generado)."""

    TITULO_EXITO = (By.CSS_SELECTOR, "[data-cy='titulo-exito']")
    CORREO_CONFIRMACION = (By.CSS_SELECTOR, "[data-cy='correo-confirmacion']")
    CODIGO_SEGUIMIENTO = (By.CSS_SELECTOR, "[data-cy='codigo-seguimiento']")
    BTN_VER_MIS_CASOS = (By.CSS_SELECTOR, "[data-cy='btn-ver-mis-casos']")
    BTN_SALIR = (By.CSS_SELECTOR, "[data-cy='btn-salir-flujo']")

    def esperar_cargada(self):
        self.esperar_visible(self.TITULO_EXITO)
        return self

    def obtener_codigo_seguimiento(self) -> str:
        return self.texto_de(self.CODIGO_SEGUIMIENTO).strip()

    def obtener_correo_confirmacion(self) -> str:
        return self.texto_de(self.CORREO_CONFIRMACION).strip()

    def ir_a_mis_casos(self):
        self.click(self.BTN_VER_MIS_CASOS)
        return self

    def salir(self):
        self.click(self.BTN_SALIR)
        return self
