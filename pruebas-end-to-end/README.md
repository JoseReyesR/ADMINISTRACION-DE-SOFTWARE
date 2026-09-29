# Pruebas End-to-End (Selenium + pytest)

Pruebas funcionales que controlan un navegador Chrome real, contra el
**frontend Angular** y el **backend Spring Boot** corriendo de verdad, con
tu **MySQL real** detrás. No hay mocks: es un end-to-end completo.

## Prerequisitos (los levantas tú)

1. Backend (`reclamos-backend`) corriendo en `http://localhost:8080`, contra
   tu MySQL real.
2. Frontend (`reclamos-frontend`) corriendo en `http://localhost:4200`
   (`ng serve`).
3. Google Chrome instalado. El chromedriver se gestiona solo (Selenium
   Manager, incluido desde Selenium 4.6+), no necesitas instalarlo a mano.
4. Python 3.10+.

## Instalación

```bash
cd pruebas-end-to-end
python -m venv venv
source venv/bin/activate        # En Windows: venv\Scripts\activate
pip install -r requirements.txt
```

## Ejecutar

```bash
pytest                          # corre toda la suite, con el navegador visible
pytest -m invitado              # solo el flujo de invitado
pytest -m cliente                # solo el flujo de cliente registrado
pytest -m backoffice             # solo login + gestión admin
pytest -v                        # con nombres de cada test
pytest --html=reporte.html       # genera un reporte HTML navegable
```

Para correr en headless (sin ventana visible, por ejemplo en un servidor):

```bash
HEADLESS=true pytest
```

Si tu frontend corre en otra URL/puerto:

```bash
FRONTEND_URL=http://localhost:4300 pytest
```

## Datos de prueba y limpieza

Cada dato que las pruebas crean en tu MySQL real (reclamos, con nombres y
correos marcados con el prefijo `TEST_`, más un DNI aleatorio que empieza en
`9`) se anota en **`datos_de_prueba_creados.txt`** (se genera automáticamente
en la raíz de este proyecto la primera vez que corres la suite). Usa ese
archivo para ubicar y borrar manualmente los datos de prueba de tu BD cuando
quieras.

Las cuentas de `test@admin` / `1234` y `cliente@tottus.com` / `123456` son
las sembradas por `SetupDataLoader` de tu backend — no las crean ni las
borran estas pruebas.

## Estructura

```
pruebas-end-to-end/
├── conftest.py              # fixtures: driver de Chrome, URL base, fixture de reclamo compartido
├── pytest.ini                # markers y configuración
├── requirements.txt
├── pages/                    # Page Objects (uno por pantalla real del Angular)
├── tests/                    # los casos de prueba (uno por función test_*)
│   └── fixtures/              # archivo de evidencia usado para el upload
└── utils/
    └── test_data.py          # generador de datos TEST_ + registro para limpieza manual
```