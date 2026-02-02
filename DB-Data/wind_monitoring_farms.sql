-- MySQL dump 10.13  Distrib 8.0.44, for Linux (x86_64)
--
-- Host: localhost    Database: wind_monitoring
-- ------------------------------------------------------
-- Server version	8.0.44-0ubuntu0.24.04.1

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
-- Table structure for table `farms`
--

DROP TABLE IF EXISTS `farms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `farms` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `region` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK3214ianrcn3ndqcuta6d7nq2f` (`code`),
  KEY `idx_farms_region` (`region`),
  KEY `idx_farms_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `farms`
--

LOCK TABLES `farms` WRITE;
/*!40000 ALTER TABLE `farms` DISABLE KEYS */;
INSERT INTO `farms` VALUES (1,'FARM-001','Alpha Wind Farm','NORTH'),(2,'FARM-002','Beta Wind Farm','SOUTH'),(3,'FARM-003','Gamma Wind Farm','EAST'),(4,'FARM-004','Delta Wind Farm','WEST'),(5,'FARM-005','Epsilon Wind Farm','WEST'),(8,'FARM-N01','North Ridge Wind Farm','NORTH'),(9,'FARM-N02','Highland North Wind Farm','NORTH'),(10,'FARM-N03','Aurora North Wind Park','NORTH'),(11,'FARM-N04','Polar Breeze North','NORTH'),(12,'FARM-N05','North Valley Wind Site','NORTH'),(13,'FARM-S01','South Plains Wind Farm','SOUTH'),(14,'FARM-S02','Coastal South Wind Park','SOUTH'),(15,'FARM-S03','Delta South Wind Farm','SOUTH'),(16,'FARM-S04','Sunridge South Turbines','SOUTH'),(17,'FARM-S05','South Horizon Wind Project','SOUTH'),(18,'FARM-E01','East Ridge Wind Farm','EAST'),(19,'FARM-E02','Sunrise East Wind Park','EAST'),(20,'FARM-E03','Frontier East Turbines','EAST'),(21,'FARM-E04','Harbor East Wind Site','EAST'),(22,'FARM-E05','East Valley Wind Clusters','EAST'),(23,'FARM-W01','West Coast Wind Farm','WEST'),(24,'FARM-W02','Highland West Wind Park','WEST'),(25,'FARM-W03','Sunset West Turbines','WEST'),(26,'FARM-W04','Canyon West Wind Site','WEST'),(27,'FARM-W05','West Delta Wind Project','WEST');
/*!40000 ALTER TABLE `farms` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-02 18:07:02
