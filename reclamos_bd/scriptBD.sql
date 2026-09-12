-- 1. Crear y usar la base de datos
DROP DATABASE IF EXISTS reclamos_db;
CREATE DATABASE reclamos_db;
USE reclamos_db;

-- 2. Tabla de Roles 
CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE 
);

-- 3. Tabla de Usuarios 
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_documento VARCHAR(20) NOT NULL, 
    numero_documento VARCHAR(20) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL,
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    password VARCHAR(255), 
    rol_id INT NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (rol_id) REFERENCES roles(id)
);

-- 4. Tablas Catálogos para Clasificación 
CREATE TABLE categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL 
);

CREATE TABLE prioridades (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL 
);

CREATE TABLE estados_reclamo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL 
);

-- 5. Tabla Principal de Reclamos (AHORA COINCIDE CON ANGULAR)
CREATE TABLE reclamos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo_seguimiento VARCHAR(20) NOT NULL UNIQUE, 
    usuario_id INT NOT NULL, 
    categoria_id INT NOT NULL, 
    prioridad_id INT NOT NULL,
    estado_id INT NOT NULL,
    
    -- NUEVOS CAMPOS DEL FRONTEND
    tipo_solicitud VARCHAR(50) NOT NULL, -- 'Reclamo' o 'Queja'
    canal_compra VARCHAR(50),            -- 'Tienda Física' o 'Tottus.com'
    tienda VARCHAR(100),                 -- 'Trujillo Centro', etc.
    numero_boleta_pedido VARCHAR(100),
    fecha_compra DATE,
    producto_implicado VARCHAR(255),
    descripcion_caso TEXT NOT NULL,
    
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_vencimiento TIMESTAMP NOT NULL, 
    
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (categoria_id) REFERENCES categorias(id),
    FOREIGN KEY (prioridad_id) REFERENCES prioridades(id),
    FOREIGN KEY (estado_id) REFERENCES estados_reclamo(id)
);

-- 6. Tabla de Evidencias
CREATE TABLE evidencias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reclamo_id INT NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    ruta_archivo VARCHAR(500) NOT NULL,
    tipo_archivo VARCHAR(50),
    tamanio_mb DECIMAL(5,2), 
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reclamo_id) REFERENCES reclamos(id) ON DELETE CASCADE
);
-- 7. insercion de datos
USE reclamos_db;

-- Insertar Roles
INSERT INTO roles (nombre) VALUES ('ROLE_CLIENTE'), ('ROLE_ADMIN'), ('ROLE_INVITADO');

-- Insertar Categorías (RN-05)[cite: 2]
INSERT INTO categorias (nombre) VALUES 
('Precio Y Cobro'), 
('Producto'), 
('Servicio'), 
('Devolución Y Cambio');

-- Insertar Prioridades (RN-07)[cite: 2]
INSERT INTO prioridades (nombre) VALUES 
('Alta'), 
('Media'), 
('Baja');

-- Insertar Estados (RN-11)[cite: 2]
INSERT INTO estados_reclamo (nombre) VALUES 
('Ingresado'), 
('En Análisis'), 
('Resuelto'), 
('Cerrado'), 
('Vencido');

-- Insertar un Usuario genérico "Invitado" (ID 1) para que los reclamos anónimos no fallen
INSERT INTO usuarios (tipo_documento, numero_documento, nombres, apellidos, correo, rol_id)
VALUES ('DNI', '00000000', 'Usuario', 'Invitado', 'invitado@tottus.com', 3);

-- 8. Agregando booleano para el usuario invitado

ALTER TABLE usuarios ADD COLUMN is_active BOOLEAN DEFAULT TRUE;

-- 9. Agregando usuario administrador

INSERT INTO usuarios (tipo_documento, numero_documento, nombres, apellidos, correo, telefono, password, rol_id, is_active) 
VALUES ('DNI', '99999999', 'Admin', 'Tottus', 'admin@tottus.com', '999999999', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjQs840FCS', 2, true);