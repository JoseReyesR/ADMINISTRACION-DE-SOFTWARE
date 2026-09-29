from selenium.webdriver.common.by import By

from pages.base_page import BasePage


class MisCasosPage(BasePage):
    """/mis-casos. Sin data-cy propio: columnas reales = Código, Fecha, Motivo, Estado, Detalles, Trazabilidad."""
    RUTA = "/mis-casos"

    TABLA_FILAS = (By.CSS_SELECTOR, "tbody tr")
    MENSAJE_SIN_CASOS = (By.XPATH, "//p[contains(text(),'No se encontraron reclamos en el historial.')]")

    def abrir(self):
        self.ir_a(self.RUTA)
        return self

    @staticmethod
    def _fila(codigo: str):
        return (By.XPATH, f"//tbody/tr[td[1][normalize-space(text())='{codigo}']]")

    def fila_con_codigo(self, codigo: str, timeout: int = 10):
        return self.esperar_visible(self._fila(codigo), timeout)

    def contiene_el_codigo(self, codigo: str) -> bool:
        try:
            self.fila_con_codigo(codigo, timeout=8)
            return True
        except Exception:
            return False

    def cantidad_de_casos(self) -> int:
        return len(self.driver.find_elements(*self.TABLA_FILAS))

    def datos_de_la_fila(self, codigo: str) -> dict:
        """HU-004-AC2: columnas código, fecha, motivo y estado de un caso puntual."""
        fila = self.fila_con_codigo(codigo)
        celdas = fila.find_elements(By.TAG_NAME, "td")
        return {
            "codigo": celdas[0].text.strip(),
            "fecha": celdas[1].text.strip(),
            "motivo": celdas[2].text.strip(),
            "estado": celdas[3].text.strip(),
        }

    def ver_detalle_de_la_fila(self, codigo: str):
        fila = self.fila_con_codigo(codigo)
        fila.find_element(By.XPATH, ".//button[contains(., 'Ver')]").click()
        return self

    def ver_historial_de_la_fila(self, codigo: str):
        fila = self.fila_con_codigo(codigo)
        fila.find_element(By.XPATH, ".//button[contains(., 'Historial')]").click()
        return self

    def muestra_mensaje_sin_casos(self) -> bool:
        """HU-004-AC3."""
        try:
            self.esperar_visible(self.MENSAJE_SIN_CASOS, timeout=8)
            return True
        except Exception:
            return False
