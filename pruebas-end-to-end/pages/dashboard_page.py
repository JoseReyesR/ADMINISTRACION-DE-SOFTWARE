from selenium.webdriver.common.by import By

from pages.base_page import BasePage


class DashboardPage(BasePage):
    RUTA = "/dashboard"
    URL_DASHBOARD_REGEX = r".*/dashboard/?$"
    URL_DETALLE_REGEX = r".*/dashboard/caso/\d+"

    ENCABEZADO = (By.XPATH, "//h5[normalize-space()='Resumen de Operaciones']")
    TOTAL_CASOS = (By.XPATH, "//p[contains(text(),'TOTAL CASOS')]/following-sibling::h3")
    ENLACE_CLIENTES = (By.XPATH, "//a[contains(., 'Clientes')]")

    @staticmethod
    def _fila(codigo: str):
        return (By.XPATH, f"//tbody/tr[td[1][normalize-space(text())='{codigo}']]")

    @staticmethod
    def _boton_ver_detalles(codigo: str):
        return (By.XPATH, f"//tbody/tr[td[1][normalize-space(text())='{codigo}']]//button[contains(., 'Ver Detalles')]")

    def abrir(self):
        self.ir_a(self.RUTA)
        return self.esperar_cargada()

    def esperar_cargada(self, timeout: int = 20):
        """Estamos EXACTAMENTE en /dashboard (no en /dashboard/caso/..) y la página ya se dibujó."""
        self.esperar_url_coincide(self.URL_DASHBOARD_REGEX, timeout)
        self.esperar_visible(self.ENCABEZADO, timeout)
        return self

    def esperar_caso_en_la_tabla(self, codigo: str, timeout: int = 20):
        """Espera a que termine la petición HTTP y el caso aparezca en la tabla."""
        locator = self._fila(codigo)
        self._esperar(timeout).until(
            lambda d: len(d.find_elements(*locator)) > 0,
            message=f"El caso {codigo} no apareció en la tabla del dashboard en {timeout}s",
        )
        return self

    def total_casos(self) -> int:
        return int(self.texto_de(self.TOTAL_CASOS).strip())

    def ver_detalle_del_caso(self, codigo: str):
        self.esperar_cargada()
        self.esperar_caso_en_la_tabla(codigo)
        self.click(self._boton_ver_detalles(codigo))
        self.esperar_url_coincide(self.URL_DETALLE_REGEX)
        return self

    def ir_a_clientes(self):
        self.click(self.ENLACE_CLIENTES)
        return self
