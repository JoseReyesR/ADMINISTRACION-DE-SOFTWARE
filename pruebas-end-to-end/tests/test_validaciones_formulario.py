"""
HU-001-CA02: validación de campos obligatorios/inválidos en el formulario
de reclamo. CORRECCIÓN IMPORTANTE: tanto el Paso 1 (reclamodatos) como el
Paso 2 (reclamoevidencia) usan el MISMO patrón -- [disabled]="!form.valid"
en el botón "Continuar" más un <small class="text-danger"> por campo. El
alert() que aparece en reclamodatos.component.ts es código muerto: el
botón ya está deshabilitado antes de que ese código pudiera ejecutarse.
"""
import pytest

from pages.ingreso_cliente_page import IngresoClientePage
from pages.reclamo_datos_page import ReclamoDatosPage
from pages.reclamo_evidencia_page import ReclamoEvidenciaPage
from utils.test_data import datos_cliente_de_prueba


def _ir_al_paso_1(driver, frontend_url):
    IngresoClientePage(driver, frontend_url).abrir().continuar_como_invitado()
    datos_page = ReclamoDatosPage(driver, frontend_url)
    datos_page.esperar_cargada()
    return datos_page


@pytest.mark.invitado
def test_HU001_CA02a_paso1_documento_invalido_deja_boton_deshabilitado(driver, frontend_url):
    """HU-001-CA02 (variante a): DNI que no cumple el patrón de 8 dígitos."""
    datos_page = _ir_al_paso_1(driver, frontend_url)
    datos = datos_cliente_de_prueba()

    datos_page.llenar_datos_personales_con_override(datos, numero_documento="123")

    assert datos_page.boton_continuar_deshabilitado()
    assert "8 números" in datos_page.mensaje_error_documento()


@pytest.mark.invitado
def test_HU001_CA02b_paso1_nombres_vacios_deja_boton_deshabilitado(driver, frontend_url):
    """HU-001-CA02 (variante b): nombres/apellidos vacíos (aquí, solo espacios)."""
    datos_page = _ir_al_paso_1(driver, frontend_url)
    datos = datos_cliente_de_prueba()

    datos_page.llenar_datos_personales_con_override(datos, nombres="   ")

    assert datos_page.boton_continuar_deshabilitado()
    assert "no pueden estar vacíos" in datos_page.mensaje_error_nombres()


@pytest.mark.invitado
def test_HU001_CA02c_paso1_celular_invalido_deja_boton_deshabilitado(driver, frontend_url):
    """HU-001-CA02 (variante c): celular inválido."""
    datos_page = _ir_al_paso_1(driver, frontend_url)
    datos = datos_cliente_de_prueba()

    datos_page.llenar_datos_personales_con_override(datos, celular="123")

    assert datos_page.boton_continuar_deshabilitado()
    assert "9 dígitos" in datos_page.mensaje_error_celular()


@pytest.mark.invitado
def test_HU001_CA02d_paso1_correo_invalido_deja_boton_deshabilitado(driver, frontend_url):
    """HU-001-CA02 (variante d): correo inválido."""
    datos_page = _ir_al_paso_1(driver, frontend_url)
    datos = datos_cliente_de_prueba()

    datos_page.llenar_datos_personales_con_override(datos, correo="no-es-un-correo")

    assert datos_page.boton_continuar_deshabilitado()
    assert "correo válido" in datos_page.mensaje_error_correo()


@pytest.mark.invitado
def test_HU001_CA02e_paso1_boton_se_habilita_con_datos_completos(driver, frontend_url):
    """HU-001-CA02 (variante e, contraste positivo): con todos los campos válidos, el botón se habilita."""
    datos_page = _ir_al_paso_1(driver, frontend_url)
    datos_page.llenar_datos_personales(datos_cliente_de_prueba())

    assert not datos_page.boton_continuar_deshabilitado()


@pytest.mark.invitado
def test_HU001_CA02f_paso2_boton_deshabilitado_con_formulario_incompleto(driver, frontend_url):
    """HU-001-CA02 (variante f): Paso 2, sin llenar nada, el botón 'Continuar' debe estar inactivo."""
    datos_page = _ir_al_paso_1(driver, frontend_url)
    datos_page.llenar_datos_personales(datos_cliente_de_prueba())
    datos_page.continuar()

    evidencia_page = ReclamoEvidenciaPage(driver, frontend_url).esperar_cargada()

    assert evidencia_page.boton_continuar_deshabilitado()


@pytest.mark.invitado
def test_HU001_CA02g_paso2_boton_habilitado_con_formulario_completo(driver, frontend_url):
    """HU-001-CA02 (variante g, contraste positivo): Paso 2, con todos los campos, el botón se habilita."""
    datos_page = _ir_al_paso_1(driver, frontend_url)
    datos_page.llenar_datos_personales(datos_cliente_de_prueba())
    datos_page.continuar()

    evidencia_page = ReclamoEvidenciaPage(driver, frontend_url)
    evidencia_page.llenar_formulario_completo(
        boleta="TEST-BOLETA-VALIDACION",
        producto="Producto de prueba",
        descripcion="Verifica que el botón se habilite con datos completos.",
    )

    assert not evidencia_page.boton_continuar_deshabilitado()
