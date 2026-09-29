from selenium.webdriver.common.by import By

from pages.base_page import BasePage


class ClientesPage(BasePage):
    """/clientes (directorio de clientes, derivado de /api/reclamos/admin/todos)."""
    RUTA = "/clientes"

    TABLA_FILAS = (By.CSS_SELECTOR, "tbody tr")
    LOADER = (By.CSS_SELECTOR, ".spinner-border")

    def abrir(self):
        self.ir_a(self.RUTA)
        self.esperar_desaparece(self.LOADER)
        return self

    def contiene_dni(self, dni: str) -> bool:
        locator = (By.XPATH, f"//tbody/tr[td[2][normalize-space(text())='{dni}']]")
        try:
            self.esperar_visible(locator, timeout=5)
            return True
        except Exception:
            return False
