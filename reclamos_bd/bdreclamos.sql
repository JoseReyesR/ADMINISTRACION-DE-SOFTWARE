CREATE DATABASE  IF NOT EXISTS `reclamos_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `reclamos_db`;
-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: reclamos_db
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `catalogo_motivos`
--

DROP TABLE IF EXISTS `catalogo_motivos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `catalogo_motivos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) NOT NULL,
  `tipo_solicitud` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `catalogo_motivos`
--

LOCK TABLES `catalogo_motivos` WRITE;
/*!40000 ALTER TABLE `catalogo_motivos` DISABLE KEYS */;
INSERT INTO `catalogo_motivos` VALUES (1,'Cobro equivocado en caja','Reclamo'),(2,'Producto vencido o en mal estado','Reclamo'),(3,'No aplicaron promoción o descuento','Reclamo'),(4,'Mala atención del personal','Queja'),(5,'Demora excesiva en caja o atención','Queja'),(6,'Instalaciones sucias o con fallas','Queja');
/*!40000 ALTER TABLE `catalogo_motivos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categorias`
--

DROP TABLE IF EXISTS `categorias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categorias` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categorias`
--

LOCK TABLES `categorias` WRITE;
/*!40000 ALTER TABLE `categorias` DISABLE KEYS */;
INSERT INTO `categorias` VALUES (1,'Precio Y Cobro'),(2,'Producto'),(3,'Servicio'),(4,'Devolución Y Cambio');
/*!40000 ALTER TABLE `categorias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `estados_reclamo`
--

DROP TABLE IF EXISTS `estados_reclamo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `estados_reclamo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `estados_reclamo`
--

LOCK TABLES `estados_reclamo` WRITE;
/*!40000 ALTER TABLE `estados_reclamo` DISABLE KEYS */;
INSERT INTO `estados_reclamo` VALUES (1,'Ingresado'),(2,'En Análisis'),(3,'Resuelto'),(4,'Cerrado'),(5,'Vencido');
/*!40000 ALTER TABLE `estados_reclamo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evidencias`
--

DROP TABLE IF EXISTS `evidencias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evidencias`
--

LOCK TABLES `evidencias` WRITE;
/*!40000 ALTER TABLE `evidencias` DISABLE KEYS */;
INSERT INTO `evidencias` VALUES (1,1,'Captura de pantalla 2025-11-10 190445.png','/uploads/Captura de pantalla 2025-11-10 190445.png','image/png',0.01,'2026-09-15 21:59:15'),(2,2,'Captura de pantalla 2026-09-15 170027.png','/uploads/Captura de pantalla 2026-09-15 170027.png','image/png',0.05,'2026-09-15 23:34:00'),(3,3,'Captura de pantalla 2026-09-15 000348.png','/uploads/Captura de pantalla 2026-09-15 000348.png','image/png',0.01,'2026-09-16 00:39:12'),(4,4,'Captura de pantalla 2025-11-10 190445.png','/uploads/Captura de pantalla 2025-11-10 190445.png','image/png',0.01,'2026-09-18 02:08:03'),(5,5,'Captura de pantalla 2026-09-17 205046.png','/uploads/Captura de pantalla 2026-09-17 205046.png','image/png',0.06,'2026-09-18 02:09:45'),(6,6,'20250414_195517.jpg','/uploads/20250414_195517.jpg','image/jpeg',8.19,'2026-09-18 06:03:01'),(7,7,'Captura de pantalla 2026-09-18 193939.png','/uploads/Captura de pantalla 2026-09-18 193939.png','image/png',0.04,'2026-09-19 01:40:22'),(8,8,'Captura de pantalla 2025-11-10 171519.png','/uploads/Captura de pantalla 2025-11-10 171519.png','image/png',0.01,'2026-09-19 13:32:18'),(9,9,'Captura de pantalla 2026-09-19 081127.png','/uploads/Captura de pantalla 2026-09-19 081127.png','image/png',0.04,'2026-09-19 15:07:29'),(10,10,'Captura de pantalla 2026-09-18 222320.png','/uploads/Captura de pantalla 2026-09-18 222320.png','image/png',0.06,'2026-09-19 15:19:02'),(11,11,'Captura de pantalla 2026-09-18 221830.png','/uploads/Captura de pantalla 2026-09-18 221830.png','image/png',0.01,'2026-09-19 15:22:30'),(12,12,'Captura de pantalla 2026-09-19 120339.png','/uploads/Captura de pantalla 2026-09-19 120339.png','image/png',0.04,'2026-09-19 17:09:42'),(13,13,'Captura de pantalla 2026-09-19 081127.png','/uploads/Captura de pantalla 2026-09-19 081127.png','image/png',0.04,'2026-09-19 17:12:56');
/*!40000 ALTER TABLE `evidencias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `historial_seguimientos`
--

DROP TABLE IF EXISTS `historial_seguimientos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historial_seguimientos`
--

LOCK TABLES `historial_seguimientos` WRITE;
/*!40000 ALTER TABLE `historial_seguimientos` DISABLE KEYS */;
INSERT INTO `historial_seguimientos` VALUES (1,3,2,NULL,NULL,'Reclamo recibido - gracias',0,'2026-09-18 05:56:12'),(2,3,2,NULL,NULL,'reclamo asignado al user 1',1,'2026-09-18 05:56:32'),(3,2,2,NULL,NULL,'asignandose',0,'2026-09-19 00:09:33'),(4,2,2,NULL,NULL,'asignado a backoffice',0,'2026-09-19 00:09:44'),(5,4,2,NULL,NULL,'Cerrado',0,'2026-09-19 00:10:06'),(6,9,2,NULL,NULL,'Agregando nota',0,'2026-09-19 15:24:19'),(7,10,2,NULL,NULL,'nota usuario',0,'2026-09-19 15:38:35'),(8,10,2,NULL,NULL,'nota privada',1,'2026-09-19 15:38:42');
/*!40000 ALTER TABLE `historial_seguimientos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `motivos`
--

DROP TABLE IF EXISTS `motivos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `motivos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `tipo_solicitud` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `motivos`
--

LOCK TABLES `motivos` WRITE;
/*!40000 ALTER TABLE `motivos` DISABLE KEYS */;
/*!40000 ALTER TABLE `motivos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prioridades`
--

DROP TABLE IF EXISTS `prioridades`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prioridades` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prioridades`
--

LOCK TABLES `prioridades` WRITE;
/*!40000 ALTER TABLE `prioridades` DISABLE KEYS */;
INSERT INTO `prioridades` VALUES (1,'Alta'),(2,'Media'),(3,'Baja'),(4,'Crítica'),(5,'Informativa');
/*!40000 ALTER TABLE `prioridades` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reclamos`
--

DROP TABLE IF EXISTS `reclamos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reclamos`
--

LOCK TABLES `reclamos` WRITE;
/*!40000 ALTER TABLE `reclamos` DISABLE KEYS */;
INSERT INTO `reclamos` VALUES (1,'REQ-2026-8242',4,1,2,3,'Queja','Tottus.com',1,1,'boleta02222','2026-09-01','','mala atencion en tottus ','2026-09-15 21:59:15','2026-10-07 02:59:15'),(2,'REQ-2026-3901',3,1,2,2,'Queja','Tienda Física',1,1,'bb41','2026-09-01','w','1','2026-09-15 23:34:00','2026-10-07 04:34:00'),(3,'REQ-2026-1659',5,1,4,1,'Reclamo','Tottus.com',1,1,'b123132','2026-09-08','12312','prueba 2','2026-09-16 00:39:12','2026-10-07 05:39:12'),(4,'REQ-2026-7283',3,1,4,4,'Reclamo','Tottus.com',1,2,'12aa','2026-09-05','t67','97t','2026-09-18 02:08:03','2026-10-09 07:08:04'),(5,'REQ-2026-5473',7,1,5,2,'Queja','Tottus.com',3,6,'bb33','2026-09-02','a','asd','2026-09-18 02:09:45','2026-10-09 07:09:45'),(6,'REQ-2026-1894',7,1,4,1,'Reclamo','Tienda Física',1,2,'12311','2026-09-17','arroz','quiero reclamar','2026-09-18 06:03:01','2026-10-09 11:03:02'),(7,'REQ-2026-6089',3,1,2,1,'Queja','Tottus.com',2,4,'prueba imagen','2026-09-05','imagen','foto agregada','2026-09-19 01:40:22','2026-10-10 06:40:22'),(8,'REQ-2026-6718',8,1,2,1,'Reclamo','Tottus.com',1,1,'b12','2026-09-04','24','123','2026-09-19 13:32:18','2026-10-10 18:32:19'),(9,'REQ-2026-1793',3,1,2,1,'Reclamo','Tienda Física',2,2,'email','2026-09-18','email2','email3','2026-09-19 15:07:29','2026-10-10 20:07:29'),(10,'REQ-2026-6826',9,1,2,3,'Reclamo','Tienda Física',3,1,'12312','2026-09-03','123123','prueba email ','2026-09-19 15:19:02','2026-10-10 20:19:03'),(11,'REQ-2026-5615',9,1,2,1,'Reclamo','Tottus.com',2,1,'pruebadatos','2026-09-02','datos','datos','2026-09-19 15:22:30','2026-10-10 20:22:30'),(12,'REQ-2026-6820',10,1,2,1,'Reclamo','Tottus.com',3,2,'12aa','2026-09-03','producto1','asdasdsd','2026-09-19 17:09:42','2026-10-10 22:09:42'),(13,'REQ-2026-6131',11,1,2,1,'Queja','Tottus.com',2,5,'bol231','2026-09-05','111','111','2026-09-19 17:12:56','2026-10-10 22:12:57');
/*!40000 ALTER TABLE `reclamos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (2,'ROLE_ADMIN'),(1,'ROLE_CLIENTE'),(3,'ROLE_INVITADO');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tiendas`
--

DROP TABLE IF EXISTS `tiendas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tiendas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tiendas`
--

LOCK TABLES `tiendas` WRITE;
/*!40000 ALTER TABLE `tiendas` DISABLE KEYS */;
INSERT INTO `tiendas` VALUES (1,'Trujillo Centro'),(2,'Mall Aventura'),(3,'Real Plaza Trujillo');
/*!40000 ALTER TABLE `tiendas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'DNI','44444441','Usuario1','Final1','final@final','999999949',NULL,'$2a$10$2xcXN/YEnoPGJTD42C86Me4KYllswXNcnwbRZ9CubCJITD2ZOjloy',2,'2026-09-15 05:51:07',1),(2,'DNI','77778888','Usuario','Test','test@admin','999000111',NULL,'$2a$10$LDIpSCGN8rQLiI0CYwukpOMy8gKkQFLE0/56lIX0AB96OErQkSV16',2,'2026-09-15 05:51:07',1),(3,'DNI','88889999','María','Torres','cliente@tottus.com','987654321',NULL,'$2a$10$Rs2OvzFd7SjF7CABpPayUuBIdCZWWyb1T6YOd0kU45AF/aUmDkUcW',1,'2026-09-15 05:51:07',1),(4,'DNI','77667766','juan','perez','a@a','987654123',NULL,'$2a$10$ghsSY/txXIKkIMnIkgW2zOqGbWm5XQeavtVbh77JjoC/rnJwjSQK.',3,'2026-09-15 21:59:15',1),(5,'DNI','87655444','jesica','jess','jess@jess','9494949492',NULL,'$2a$10$zVUo3dSB5hk2P6hgc/V2Z.D7MYzL/p7PzCHSO.vzIsh9UNUtTB5yq',3,'2026-09-16 00:39:12',1),(7,'DNI','98988989','jessie','Ruiz','ruiz@ruiz','818178412',NULL,'$2a$10$8IyZEwrK1Uw4pyMIMkNTCebBaKYaDoopKlti93uqr70KQLhCdHq76',3,'2026-09-18 02:09:45',1),(8,'DNI','12311111','jesus','trujillo','jesus@jesus','987789654',NULL,'$2a$10$1LOdM3omRmcxtE.gs6X1MeVlktbfSIvaqx5rJhZ56NkxKRjAT1Z1m',3,'2026-09-19 13:32:18',1),(9,'DNI','77771234','Cliente','cliente2','cliente@cliente','987789555',NULL,'$2a$10$akv01NibkeNswXJH.0H5seh8hWFz5csQOeg4jgE38C/C1SK6xqY/m',3,'2026-09-19 15:19:02',1),(10,'DNI','78876543','Juancito','Perecito','q@q.com','900011111',NULL,'$2a$10$9F6oMIKP20WX1I27cn2kvuxoKkysRj5ZwmwWvgNvuBM346of8gMQK',3,'2026-09-19 17:09:42',1),(11,'DNI','72672643','lidia','lidias','lidia@lidia.com','983983983',NULL,'$2a$10$5mcE0I4PlUp11G5Y0uD1XOokIlcvGbT/xMHiQNX8EUUCGBQI5752G',3,'2026-09-19 17:12:56',1);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-19 14:43:50
