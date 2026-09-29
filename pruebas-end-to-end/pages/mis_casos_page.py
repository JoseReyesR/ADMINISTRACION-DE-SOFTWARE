from selenium.webdriver.common.by import By

from pages.base_page import BasePage


class MisCasosPage(BasePage):
    RUTA = "/mis-casos"

    TABLA_FILAS = (By.CSS_SELECTOR, "tbody tr")

    def abrir(self):
        self.ir_a(self.RUTA)
        return self

    def fila_con_codigo(self, codigo: str, timeout: int = 10):
        locator = (By.XPATH, f"//tbody/tr[td[1][normalize-space(text())='{codigo}']]")
        return self.esperar_visible(locator, timeout)

    def contiene_el_codigo(self, codigo: str) -> bool:
        try:
            self.fila_con_codigo(codigo, timeout=5)
            return True
        except Exception:
            return False

    def cantidad_de_casos(self) -> int:
        return len(self.driver.find_elements(*self.TABLA_FILAS))
