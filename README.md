# 🛒 Sistema de Gestión de Reclamos y Quejas - Tottus

Plataforma web integral para el registro, seguimiento y administración de reclamos y quejas de clientes. Este proyecto está dividido en una arquitectura de dos capas principales: un **Frontend** moderno construido con **Angular** y un **Backend** robusto desarrollado en **Java con Spring Boot**, respaldado por una base de datos **MySQL**.

---

## 🚀 Guía Rápida del Sistema (¿Cómo funciona?)

El sistema cuenta con dos flujos principales:

1. **Portal del Cliente (Público):** Permite a los clientes (invitados o registrados) registrar un nuevo reclamo o queja seleccionando el tipo de incidente, la tienda, subiendo evidencias (archivos hasta 10MB) y detallando el caso. También incluye un módulo de **"Seguimiento"** donde, ingresando su documento y código de seguimiento, pueden ver el estado y trazabilidad de su caso.
2. **BackOffice Administrativo (Privado):** Un panel exclusivo para los trabajadores/administradores del sistema. Aquí pueden visualizar un **Dashboard** con todos los casos entrantes, leer los detalles, ver la evidencia adjunta, y cambiar el estado (Ej: *En Revisión*, *Resuelto*) o la prioridad de cada reclamo.

---

## 🛠️ Tecnologías Utilizadas

- **Frontend:** Angular, HTML5, CSS3, Bootstrap.
- **Backend:** Java, Spring Boot, Spring Data JPA, Spring Security (JWT).
- **Base de Datos:** MySQL.

---

## 📋 Requisitos Previos

Antes de levantar el proyecto, asegúrate de tener instalado lo siguiente en tu equipo:

| Herramienta | Versión recomendada | Para qué se usa | Descarga |
|---|---|---|---|
| **Git** | Última estable | Clonar el repositorio | [git-scm.com](https://git-scm.com/downloads) |
| **JDK (Java)** | 17 o superior | Ejecutar el Backend (Spring Boot) | [adoptium.net](https://adoptium.net/) |
| **Maven** | 3.8 o superior | Compilar y levantar el Backend | [maven.apache.org](https://maven.apache.org/download.cgi) |
| **MySQL Server** | 8.0 o superior | Base de datos | [dev.mysql.com](https://dev.mysql.com/downloads/mysql/) |
| **Node.js** | 18 LTS o superior (incluye `npm`) | Ejecutar el Frontend (Angular) | [nodejs.org](https://nodejs.org/) |
| **Angular CLI** | Compatible con la versión del proyecto | Comando `ng serve` | Ver instalación abajo |

> 💡 **Nota:** La versión exacta de Java está en el `pom.xml` del backend y la de Angular en el `package.json` del frontend. Si tienes dudas, usa esas versiones.

### Herramientas opcionales (recomendadas)

- **Editor / IDE:** VS Code, IntelliJ IDEA o Spring Tool Suite.
- **Gestor de base de datos:** MySQL Workbench o phpMyAdmin.

### Instalar Angular CLI

Una vez instalado Node.js, ejecuta en una terminal:

```bash
npm install -g @angular/cli
```

### Verificar que todo esté instalado

Ejecuta estos comandos en tu terminal; cada uno debe mostrar una versión:

```bash
git --version
java -version
mvn -version
mysql --version
node -v
npm -v
ng version
```

### Puertos que deben estar libres

| Puerto | Servicio |
|---|---|
| `3306` | MySQL |
| `8080` | Backend (Spring Boot) |
| `4200` | Frontend (Angular) |

> ⚠️ Asegúrate de que el servicio de **MySQL esté iniciado** antes de levantar el Backend; de lo contrario, Spring Boot no podrá conectarse y fallará al arrancar.

---

## ⚙️ Configuración e Instalación

Para ejecutar este proyecto de manera local, sigue estos pasos cuidadosamente.

### 1. Preparar la Base de Datos (MySQL)

Dentro del repositorio, en la carpeta `reclamos_bd`, encontrarás dos archivos `.sql`. Elige el que mejor se adapte a lo que necesitas:

- **`bdreclamos.sql`:** Base de datos **CON DATOS**. Ideal si quieres probar el sistema rápidamente con reclamos, usuarios y catálogos ya creados. Crea la BD `reclamos_db`.
- **`reclamos_tottus.sql`:** Base de datos **VACÍA**. Ideal si quieres empezar desde cero. Solo contiene los catálogos básicos (Tiendas, Categorías, Motivos, Roles) y un usuario administrador. Crea la BD `reclamos_tottus`.

**Pasos:**

1. Abre tu gestor de base de datos (Ej: MySQL Workbench, phpMyAdmin).
2. Importa y ejecuta el script `.sql` que hayas elegido.

### 2. Configurar y Levantar el Backend (Spring Boot)

1. Abre la carpeta `reclamos-backend` en tu editor de código (como VS Code o IntelliJ).
2. Dirígete al archivo de configuración: `src/main/resources/application.properties`.
3. Configura la conexión a tu base de datos comentando/descomentando la línea correspondiente según el script que ejecutaste en el Paso 1:

```properties
# ========================================================
# CONFIGURACIÓN DE BASE DE DATOS
# ========================================================

# OPCIÓN A: Base de datos CON datos (bdreclamos.sql) -> BD: reclamos_db
spring.datasource.url=jdbc:mysql://localhost:3306/reclamos_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true

# OPCIÓN B: Base de datos VACÍA desde cero (reclamos_tottus.sql) -> BD: reclamos_tottus
# Descomenta la línea de abajo y comenta la de arriba si usas esta opción:
# spring.datasource.url=jdbc:mysql://localhost:3306/reclamos_tottus?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true

# ATENCIÓN: Cambia 'root' y '1234' por tu usuario y contraseña local de MySQL
spring.datasource.username=root
spring.datasource.password=1234
```

4. Abre una terminal dentro de la carpeta `reclamos-backend` y ejecuta el servidor con Maven:

```bash
mvn spring-boot:run
```

El backend iniciará en **http://localhost:8080**.

### 3. Configurar y Levantar el Frontend (Angular)

1. Abre la carpeta `reclamos-frontend` en tu terminal o editor de código.
2. Instala las dependencias necesarias de Node.js ejecutando:

```bash
npm install
```

3. Levanta el servidor de desarrollo de Angular (el parámetro `-o` abrirá tu navegador automáticamente):

```bash
ng serve -o
```

El frontend iniciará en **http://localhost:4200**.

---

## 🔐 Usuarios por Defecto para Pruebas

Si levantaste el sistema utilizando la base de datos pre-cargada (`bdreclamos.sql`), puedes iniciar sesión con cualquiera de los siguientes usuarios creados por defecto para probar los diferentes roles y accesos:

| Rol | Correo / Usuario | Contraseña | Descripción |
|---|---|---|---|
| 👑 Administrador | `test@admin` | `1234` | Acceso total al BackOffice y gestión de casos. |
| 🧪 Usuario Prueba | `final@final` | `12345` | Usuario genérico de pruebas. |
| 👤 Cliente | `cliente@tottus.com` | `123456` | Vista de cliente para crear reclamos y ver su bandeja personal de casos. |
