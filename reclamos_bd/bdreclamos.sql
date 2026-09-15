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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evidencias`
--

LOCK TABLES `evidencias` WRITE;
/*!40000 ALTER TABLE `evidencias` DISABLE KEYS */;
INSERT INTO `evidencias` VALUES (1,2,'Captura de pantalla 2025-11-10 190445.png','/uploads/Captura de pantalla 2025-11-10 190445.png','image/png',0.01,'2026-09-12 13:13:19'),(2,3,'Captura de pantalla 2026-09-10 144402.png','/uploads/Captura de pantalla 2026-09-10 144402.png','image/png',0.03,'2026-09-12 16:20:10'),(3,4,'Captura de pantalla 2026-09-12 020948.png','/uploads/Captura de pantalla 2026-09-12 020948.png','image/png',0.04,'2026-09-12 16:31:57'),(4,5,'Captura de pantalla 2026-09-10 144352.png','/uploads/Captura de pantalla 2026-09-10 144352.png','image/png',0.06,'2026-09-12 16:34:51'),(5,6,'Captura de pantalla 2025-11-13 220656.png','/uploads/Captura de pantalla 2025-11-13 220656.png','image/png',0.14,'2026-09-12 18:53:30'),(6,7,'Captura de pantalla 2026-09-12 141642.png','/uploads/Captura de pantalla 2026-09-12 141642.png','image/png',0.08,'2026-09-12 19:19:10'),(7,8,'Captura de pantalla 2026-09-12 142433.png','/uploads/Captura de pantalla 2026-09-12 142433.png','image/png',0.07,'2026-09-12 19:26:55'),(8,9,'Captura de pantalla 2026-09-12 130654.png','/uploads/Captura de pantalla 2026-09-12 130654.png','image/png',0.05,'2026-09-12 20:32:32'),(9,10,'Captura de pantalla 2025-11-10 190449.png','/uploads/Captura de pantalla 2025-11-10 190449.png','image/png',0.01,'2026-09-13 17:12:41'),(10,11,'Captura de pantalla 2025-11-10 190445.png','/uploads/Captura de pantalla 2025-11-10 190445.png','image/png',0.01,'2026-09-15 04:43:12');
/*!40000 ALTER TABLE `evidencias` ENABLE KEYS */;
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
  `tienda` varchar(100) DEFAULT NULL,
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
  CONSTRAINT `reclamos_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `reclamos_ibfk_2` FOREIGN KEY (`categoria_id`) REFERENCES `categorias` (`id`),
  CONSTRAINT `reclamos_ibfk_3` FOREIGN KEY (`prioridad_id`) REFERENCES `prioridades` (`id`),
  CONSTRAINT `reclamos_ibfk_4` FOREIGN KEY (`estado_id`) REFERENCES `estados_reclamo` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reclamos`
--

LOCK TABLES `reclamos` WRITE;
/*!40000 ALTER TABLE `reclamos` DISABLE KEYS */;
INSERT INTO `reclamos` VALUES (1,'REQ-2026-2973',2,1,2,4,'Reclamo','Tienda Física','Trujillo Centro','asd','2026-09-05','asd','eq','2026-09-12 06:57:45','2026-10-03 11:57:46'),(2,'REQ-2026-9485',8,1,2,4,'Reclamo','Tottus.com','Trujillo Centro','gggg','2026-09-10','g1','g2','2026-09-12 13:13:19','2026-10-03 18:13:20'),(3,'REQ-2026-4464',18,1,2,3,'Reclamo','Tottus.com','Trujillo Centro','r1','2026-09-02','r11','r111','2026-09-12 16:20:10','2026-10-03 21:20:11'),(4,'REQ-2026-2225',18,1,2,3,'Reclamo','Tienda Física','Mall Aventura','asdad','2026-08-30','prueba2','yuiyuiyui','2026-09-12 16:31:57','2026-10-03 21:31:58'),(5,'REQ-2026-8999',19,1,2,2,'Reclamo','Tienda Física','Trujillo Centro','andrea23','2026-09-04','andrea1','andrea12','2026-09-12 16:34:51','2026-10-03 21:34:51'),(6,'REQ-2026-1463',18,1,2,1,'Reclamo','Tienda Física','Trujillo Centro','123','2026-09-11','123','123','2026-09-12 18:53:30','2026-10-03 23:53:30'),(7,'REQ-2026-6277',18,1,2,5,'Queja','Tottus.com','Trujillo Centro','nuevaboleta','2026-08-30','nuevo','nuevo','2026-09-12 19:19:10','2026-10-04 00:19:10'),(8,'REQ-2026-6935',18,1,2,1,'Reclamo','Tottus.com','Trujillo Centro','nuevo2','2026-08-31','nuevo2','nuevo2','2026-09-12 19:26:55','2026-10-04 00:26:56'),(9,'REQ-2026-9870',20,1,2,2,'Reclamo','Tottus.com','Trujillo Centro','hola','2026-09-02','prodhola','holadetal','2026-09-12 20:32:32','2026-10-04 01:32:33'),(10,'REQ-2026-4385',21,1,2,1,'Reclamo','Tottus.com','Mall Aventura','bb41','2026-08-31','qef','adas','2026-09-13 17:12:41','2026-10-04 22:12:41'),(11,'REQ-2026-8418',22,1,2,1,'Reclamo','Tottus.com','Trujillo Centro','asd','2026-09-01','prueba2','adasasdad','2026-09-15 04:43:12','2026-10-06 09:43:13');
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
  `password` varchar(255) NOT NULL,
  `rol_id` int NOT NULL,
  `fecha_registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `numero_documento` (`numero_documento`),
  KEY `rol_id` (`rol_id`),
  CONSTRAINT `usuarios_ibfk_1` FOREIGN KEY (`rol_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (2,'DNI','72672658','abc','123','registrador@registrador','123',NULL,'$2a$10$GGewNiQAKHNhh8ICGgAubOC.GarKO9TfBuP7YXHZ7Pzu45Qz3IWyS',3,'2026-09-12 06:57:45',1),(3,'DNI','88888888','Soporte','Tottus','soporte@tottus.com','999888777',NULL,'$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjQs840FCS',2,'2026-09-12 07:01:44',1),(5,'DNI','20202020','Luis','Soporte','tecnico@tottus.com','999333444',NULL,'$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjQs840FCS',2,'2026-09-12 12:30:46',1),(6,'DNI','30303030','Ana','Cliente','cliente@gmail.com','999555666',NULL,'$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjQs840FCS',3,'2026-09-12 12:30:46',1),(7,'DNI','99999999','Admin','Tottus','admin@tottus.com','999999999',NULL,'123456',2,'2026-09-12 13:07:09',1),(8,'DNI','14785236','jose','rubio','j@j','987654322',NULL,'$2a$10$LgXgJPjDlh1tsQZ7cm4EGOk2x77Nqa7gAyI5uyok1jcS91Ym2Gt5O',3,'2026-09-12 13:13:19',1),(9,'DNI','10203040','Administrador','Seguro','admi@admin','999000111',NULL,'$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjQs840FCS',2,'2026-09-12 13:17:23',1),(10,'DNI','77777777','Desarrollador','Tottus','dev@dev.com','999999999',NULL,'$2a$10$qarvGQJTJutDJINtg7dxr.Q.AWajDD5sJyjAwkZ3j8ZssvA2nc56.',2,'2026-09-12 13:18:21',1),(13,'DNI','77777771','Admin','Seguro','admin@admin','999999999',NULL,'$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjQs840FCS',2,'2026-09-12 13:21:29',1),(14,'DNI','12312312','Usuario','Prueba','prueba@admin','999111222',NULL,'$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjQs840FCS',2,'2026-09-12 13:42:35',1),(15,'DNI','44444444','Usuario','Final','final@admin','999999999',NULL,'123456',2,'2026-09-12 14:18:09',1),(16,'DNI','44444441','Usuario1','Final1','final@final','999999949',NULL,'$2a$10$NCg4iB4Ox5nLwoDzko72E.KrcsvpwmClFEX4QQeW37MPCd5SGyRHy',2,'2026-09-12 14:25:07',1),(17,'DNI','77778888','Usuario','Test','test@admin','999000111',NULL,'$2a$10$/anDO8WUxBJ5fBix4Re.8eRjVOdyEc2hjYFCdA9nH1lFS574mM48W',2,'2026-09-12 14:28:18',1),(18,'DNI','88889999','María','Torres','cliente@tottus.com','987654321',NULL,'$2a$10$s1u8AVK2h1RzYU6kIGBfweFRC/Z93Gc.jtHMnxHYqibdrUtWpSGza',1,'2026-09-12 16:05:10',1),(19,'DNI','09991111','andrea','andrea1','andrea@andrea','09876543',NULL,'$2a$10$hAjFWBbBF3JXtJPa4u8rAefKYW4MGShwp.oABOVg2gn2cuMBa38D.',3,'2026-09-12 16:34:51',1),(20,'DNI','77771234','hola','chay','hola@hola','987667891',NULL,'$2a$10$RZejufcdUprj65QLj0xXneVeYwWrkK5NHV2SzdNZxFx.fVmExnfp6',3,'2026-09-12 20:32:32',1),(21,'DNI','23123121','sdsdas','asdasd','soporte@tottuss.com','931980059',NULL,'$2a$10$MQzvaaMxCgB9wYlISX1IB.BDk6thlqnuhXZKS6q2pe2XyeD9r78aK',3,'2026-09-13 17:12:41',1),(22,'DNI','72672611','j','jj','jj','jj',NULL,'$2a$10$LXvZBrXTDkHNHy/N6ymEq.1QGT5ZTKXjcKVnAqVmLXm4EH/N66/qC',3,'2026-09-15 04:43:12',1);
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

-- Dump completed on 2026-09-15  0:33:03
