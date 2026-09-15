DROP DATABASE IF EXISTS `reclamos_db`;
CREATE DATABASE `reclamos_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `reclamos_db`;

-- ==========================================
-- 1. TABLAS CATÁLOGO (CON DATOS)
-- ==========================================

CREATE TABLE `roles` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `nombre` VARCHAR(50) NOT NULL UNIQUE
);
INSERT INTO `roles` (`nombre`) VALUES 
('ROLE_CLIENTE'), 
('ROLE_ADMIN'), 
('ROLE_INVITADO');

CREATE TABLE `categorias` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `nombre` VARCHAR(100) NOT NULL
);
INSERT INTO `categorias` (`nombre`) VALUES 
('Precio Y Cobro'), 
('Producto'), 
('Servicio'), 
('Devolución Y Cambio');

CREATE TABLE `prioridades` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `nombre` VARCHAR(50) NOT NULL
);
INSERT INTO `prioridades` (`nombre`) VALUES 
('Alta'), 
('Media'), 
('Baja'), 
('Crítica'), 
('Informativa');

CREATE TABLE `estados_reclamo` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `nombre` VARCHAR(50) NOT NULL
);
INSERT INTO `estados_reclamo` (`nombre`) VALUES 
('Ingresado'), 
('En Análisis'), 
('Resuelto'), 
('Cerrado'), 
('Vencido');

CREATE TABLE `tiendas` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `nombre` VARCHAR(100) NOT NULL
);
INSERT INTO `tiendas` (`nombre`) VALUES 
('Trujillo Centro'), 
('Mall Aventura'), 
('Real Plaza Trujillo');

CREATE TABLE `catalogo_motivos` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `nombre` VARCHAR(150) NOT NULL,
  `tipo_solicitud` VARCHAR(50) NOT NULL
);
INSERT INTO `catalogo_motivos` (`nombre`, `tipo_solicitud`) VALUES
('Cobro equivocado en caja', 'Reclamo'),
('Producto vencido o en mal estado', 'Reclamo'),
('No aplicaron promoción o descuento', 'Reclamo'),
('Mala atención del personal', 'Queja'),
('Demora excesiva en caja o atención', 'Queja'),
('Instalaciones sucias o con fallas', 'Queja');


-- ==========================================
-- 2. TABLAS TRANSACCIONALES (VACÍAS)
-- ==========================================

CREATE TABLE `usuarios` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `tipo_documento` VARCHAR(20) NOT NULL,
  `numero_documento` VARCHAR(20) NOT NULL UNIQUE,
  `nombres` VARCHAR(100) NOT NULL,
  `apellidos` VARCHAR(100) NOT NULL,
  `correo` VARCHAR(150) NOT NULL,
  `telefono` VARCHAR(20),
  `direccion` VARCHAR(255),
  `password` VARCHAR(255),
  `rol_id` INT NOT NULL,
  `fecha_registro` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `is_active` BOOLEAN DEFAULT TRUE,
  FOREIGN KEY (`rol_id`) REFERENCES `roles`(`id`)
);

CREATE TABLE `reclamos` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `codigo_seguimiento` VARCHAR(20) NOT NULL UNIQUE,
  `usuario_id` INT NOT NULL,
  `categoria_id` INT NOT NULL,
  `prioridad_id` INT NOT NULL,
  `estado_id` INT NOT NULL,
  `tipo_solicitud` VARCHAR(50) NOT NULL,
  `canal_compra` VARCHAR(50) DEFAULT NULL,
  `tienda_id` INT NOT NULL,
  `motivo_id` INT NOT NULL,
  `numero_boleta_pedido` VARCHAR(100) DEFAULT NULL,
  `fecha_compra` DATE DEFAULT NULL,
  `producto_implicado` VARCHAR(255) DEFAULT NULL,
  `descripcion_caso` TEXT NOT NULL,
  `fecha_registro` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `fecha_vencimiento` TIMESTAMP NOT NULL,
  FOREIGN KEY (`usuario_id`) REFERENCES `usuarios`(`id`),
  FOREIGN KEY (`categoria_id`) REFERENCES `categorias`(`id`),
  FOREIGN KEY (`prioridad_id`) REFERENCES `prioridades`(`id`),
  FOREIGN KEY (`estado_id`) REFERENCES `estados_reclamo`(`id`),
  FOREIGN KEY (`tienda_id`) REFERENCES `tiendas`(`id`),
  FOREIGN KEY (`motivo_id`) REFERENCES `catalogo_motivos`(`id`)
);

CREATE TABLE `evidencias` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `reclamo_id` INT NOT NULL,
  `nombre_archivo` VARCHAR(255) NOT NULL,
  `ruta_archivo` VARCHAR(500) NOT NULL,
  `tipo_archivo` VARCHAR(50),
  `tamanio_mb` DECIMAL(5,2),
  `fecha_subida` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`reclamo_id`) REFERENCES `reclamos`(`id`) ON DELETE CASCADE
);