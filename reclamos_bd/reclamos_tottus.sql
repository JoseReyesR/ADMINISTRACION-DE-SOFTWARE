-- =========================================================================
-- SCRIPT DE INICIALIZACIÓN DE BASE DE DATOS: RECLAMOS TOTTUS
-- =========================================================================

-- 1. CREACIÓN DE LA BASE DE DATOS
CREATE DATABASE IF NOT EXISTS `reclamos_tottus` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `reclamos_tottus`;

-- Desactivamos la validación de llaves foráneas temporalmente para poder crear/borrar sin errores de dependencia
SET FOREIGN_KEY_CHECKS=0;

-- =========================================================================
-- FASE 1: CREACIÓN DE ESTRUCTURAS (TABLAS)
-- =========================================================================

-- 1.1 Tablas Maestras / Catálogos Independientes
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `categorias`;
CREATE TABLE `categorias` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `estados_reclamo`;
CREATE TABLE `estados_reclamo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `prioridades`;
CREATE TABLE `prioridades` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `tiendas`;
CREATE TABLE `tiendas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `catalogo_motivos`;
CREATE TABLE `catalogo_motivos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) NOT NULL,
  `tipo_solicitud` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 1.2 Tabla de Usuarios (Se deja vacía intencionalmente para que Spring Boot haga el seeding al iniciar)
DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE `usuarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tipo_documento` varchar(20) NOT NULL,
  `numero_documento` varchar(20) NOT NULL,
  `nombres` varchar(100) NOT NULL,
  `apellidos` varchar(100) NOT NULL,
  `correo` varchar(150) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `rol_id` int NOT NULL,
  `fecha_registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `numero_documento` (`numero_documento`),
  KEY `rol_id` (`rol_id`),
  CONSTRAINT `usuarios_ibfk_1` FOREIGN KEY (`rol_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 1.3 Tablas Transaccionales Principales (Reclamos, Evidencias e Historial)
DROP TABLE IF EXISTS `reclamos`;
CREATE TABLE `reclamos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo_seguimiento` varchar(20) NOT NULL,
  `usuario_id` int NOT NULL,
  `categoria_id` int NOT NULL,
  `prioridad_id` int NOT NULL,
  `estado_id` int NOT NULL,
  `tipo_solicitud` varchar(50) NOT NULL,
  `canal_compra` varchar(50) DEFAULT NULL,
  `tienda_id` int NOT NULL,
  `motivo_id` int NOT NULL,
  `numero_boleta_pedido` varchar(100) DEFAULT NULL,
  `fecha_compra` date DEFAULT NULL,
  `producto_implicado` varchar(255) DEFAULT NULL,
  `descripcion_caso` text NOT NULL,
  `fecha_registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_vencimiento` timestamp NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo_seguimiento` (`codigo_seguimiento`),
  KEY `usuario_id` (`usuario_id`),
  KEY `categoria_id` (`categoria_id`),
  KEY `prioridad_id` (`prioridad_id`),
  KEY `estado_id` (`estado_id`),
  KEY `tienda_id` (`tienda_id`),
  KEY `motivo_id` (`motivo_id`),
  CONSTRAINT `reclamos_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `reclamos_ibfk_2` FOREIGN KEY (`categoria_id`) REFERENCES `categorias` (`id`),
  CONSTRAINT `reclamos_ibfk_3` FOREIGN KEY (`prioridad_id`) REFERENCES `prioridades` (`id`),
  CONSTRAINT `reclamos_ibfk_4` FOREIGN KEY (`estado_id`) REFERENCES `estados_reclamo` (`id`),
  CONSTRAINT `reclamos_ibfk_5` FOREIGN KEY (`tienda_id`) REFERENCES `tiendas` (`id`),
  CONSTRAINT `reclamos_ibfk_6` FOREIGN KEY (`motivo_id`) REFERENCES `catalogo_motivos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `evidencias`;
CREATE TABLE `evidencias` (
  `id` int NOT NULL AUTO_INCREMENT,
  `reclamo_id` int NOT NULL,
  `nombre_archivo` varchar(255) NOT NULL,
  `ruta_archivo` varchar(500) NOT NULL,
  `tipo_archivo` varchar(50) DEFAULT NULL,
  `tamanio_mb` decimal(5,2) DEFAULT NULL,
  `fecha_subida` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `reclamo_id` (`reclamo_id`),
  CONSTRAINT `evidencias_ibfk_1` FOREIGN KEY (`reclamo_id`) REFERENCES `reclamos` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `historial_seguimientos`;
CREATE TABLE `historial_seguimientos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `reclamo_id` int NOT NULL,
  `usuario_id_responsable` int NOT NULL,
  `estado_anterior_id` int DEFAULT NULL,
  `estado_nuevo_id` int DEFAULT NULL,
  `comentario` text NOT NULL,
  `es_interno` tinyint(1) DEFAULT '0',
  `fecha_registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `reclamo_id` (`reclamo_id`),
  KEY `usuario_id_responsable` (`usuario_id_responsable`),
  KEY `estado_anterior_id` (`estado_anterior_id`),
  KEY `estado_nuevo_id` (`estado_nuevo_id`),
  CONSTRAINT `historial_seguimientos_ibfk_1` FOREIGN KEY (`reclamo_id`) REFERENCES `reclamos` (`id`) ON DELETE CASCADE,
  CONSTRAINT `historial_seguimientos_ibfk_2` FOREIGN KEY (`usuario_id_responsable`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `historial_seguimientos_ibfk_3` FOREIGN KEY (`estado_anterior_id`) REFERENCES `estados_reclamo` (`id`),
  CONSTRAINT `historial_seguimientos_ibfk_4` FOREIGN KEY (`estado_nuevo_id`) REFERENCES `estados_reclamo` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =========================================================================
-- FASE 2: INSERCIÓN DE DATOS MAESTROS AMPLIADOS (CATÁLOGOS)
-- =========================================================================

-- Inserción de Roles del sistema
INSERT INTO `roles` (`id`, `nombre`) VALUES 
(1,'ROLE_CLIENTE'), 
(2,'ROLE_ADMIN'), 
(3,'ROLE_INVITADO');

-- Inserción de Categorías de atención (Se mantienen las 4 obligatorias para correcta clasificación)
INSERT INTO `categorias` (`id`, `nombre`) VALUES 
(1,'Precio Y Cobro'), 
(2,'Producto'), 
(3,'Servicio'), 
(4,'Devolución Y Cambio');

-- Inserción de Estados del Reclamo/Queja
INSERT INTO `estados_reclamo` (`id`, `nombre`) VALUES 
(1,'Ingresado'), 
(2,'En Análisis'), 
(3,'Resuelto'), 
(4,'Cerrado'), 
(5,'Vencido');

-- Inserción de Prioridades de atención
INSERT INTO `prioridades` (`id`, `nombre`) VALUES 
(1,'Alta'), 
(2,'Media'), 
(3,'Baja'), 
(4,'Crítica'), 
(5,'Informativa');

-- Inserción de Tiendas Físicas (AMPLIADO A 10 SUCURSALES)
INSERT INTO `tiendas` (`id`, `nombre`) VALUES 
(1,'Trujillo Centro'), 
(2,'Mall Aventura Trujillo'), 
(3,'Real Plaza Trujillo'),
(4,'Open Plaza Los Jardines'),
(5,'Chepén'),
(6,'Lima - Jockey Plaza'),
(7,'Lima - Megaplaza'),
(8,'Chiclayo - Mall Aventura'),
(9,'Piura - Open Plaza'),
(10,'Arequipa - Porongoche');

-- Inserción del Catálogo de Motivos reales (AMPLIADO A 15 CASUÍSTICAS CLARAS)
INSERT INTO `catalogo_motivos` (`id`, `nombre`, `tipo_solicitud`) VALUES 
(1,'Cobro doble o monto incorrecto en caja','Reclamo'),
(2,'Producto vencido o en mal estado','Reclamo'),
(3,'No aplicaron promoción o descuento publicado','Reclamo'),
(4,'Negativa a realizar cambio o devolución','Reclamo'),
(5,'Publicidad engañosa en tienda o web','Reclamo'),
(6,'Retraso en la entrega de compra online','Reclamo'),
(7,'Producto entregado incompleto o dañado','Reclamo'),
(8,'Problemas con la garantía del producto','Reclamo'),
(9,'Mala atención o trato inadecuado del personal','Queja'),
(10,'Demora excesiva en caja para pagar','Queja'),
(11,'Instalaciones sucias o baños en mal estado','Queja'),
(12,'Falta de stock de productos ofertados','Queja'),
(13,'Desorden o falta de precios en las góndolas','Queja'),
(14,'Demora en la zona de recojo (Retiro en Tienda)','Queja'),
(15,'Falta de carritos de compras o canastillas','Queja');

-- Reactivamos la validación de llaves foráneas para proteger la integridad de los datos
SET FOREIGN_KEY_CHECKS=1;