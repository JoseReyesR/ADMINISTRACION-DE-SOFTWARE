"""
Generador de datos de prueba, únicos por ejecución, y claramente marcados
con el prefijo TEST_ para poder identificarlos (y borrarlos a mano de la
BD real) después de correr la suite.
"""
import os
import random
import time
from datetime import datetime

_LOG_PATH = os.path.join(os.path.dirname(os.path.dirname(__file__)), "datos_de_prueba_creados.txt")


def registrar_dato_creado(tipo: str, valor: str, extra: str = ""):
    """
    Anota en datos_de_prueba_creados.txt (en la raíz del proyecto) cada dato
    que un test creó en la BD real, para que después puedas ubicarlos y
    borrarlos a mano. No falla el test si no puede escribir el archivo.
    """
    try:
        with open(_LOG_PATH, "a", encoding="utf-8") as f:
            marca = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
            linea = f"[{marca}] {tipo}: {valor}"
            if extra:
                linea += f" ({extra})"
            f.write(linea + "\n")
    except OSError:
        pass


def _sufijo_unico() -> str:
    """Timestamp en milisegundos: único incluso si dos tests corren en paralelo."""
    return str(int(time.time() * 1000))


def nombre_de_prueba() -> str:
    return f"TEST_Selenium_{_sufijo_unico()}"


def correo_de_prueba() -> str:
    return f"test_selenium_{_sufijo_unico()}@correo-pruebas.com"


def dni_de_prueba() -> str:
    """
    DNI válido (8 dígitos) que no colisiona con los usuarios semilla del
    backend (44444441, 77778888, 88889999). Siempre empieza en '9' y no se
    puede prefijar con TEST_ porque el formulario exige exactamente 8
    dígitos numéricos.
    """
    return "9" + "".join(str(random.randint(0, 9)) for _ in range(7))


def celular_de_prueba() -> str:
    return "9" + "".join(str(random.randint(0, 9)) for _ in range(8))


def datos_cliente_de_prueba() -> dict:
    """Un set completo de datos de cliente listo para llenar el formulario de reclamo."""
    return {
        "tipo_documento": "DNI",
        "numero_documento": dni_de_prueba(),
        "nombres": nombre_de_prueba(),
        "apellidos": "Apellido_Prueba",
        "correo": correo_de_prueba(),
        "celular": celular_de_prueba(),
    }
