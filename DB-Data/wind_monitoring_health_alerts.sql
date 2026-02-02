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
-- Table structure for table `health_alerts`
--

DROP TABLE IF EXISTS `health_alerts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `health_alerts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `acknowledged` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `message` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `severity` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `turbine_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_alert_turbine_created` (`turbine_id`,`created_at`),
  CONSTRAINT `FKq7jkwmxpi7uxcso9cr395wyt6` FOREIGN KEY (`turbine_id`) REFERENCES `turbines` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `health_alerts`
--

LOCK TABLES `health_alerts` WRITE;
/*!40000 ALTER TABLE `health_alerts` DISABLE KEYS */;
INSERT INTO `health_alerts` VALUES (1,_binary '\0','2026-01-31 18:01:20.000000','Average power far below expected at good wind speed (possible drivetrain issue).','CRITICAL','LOW_PERFORMANCE',2),(2,_binary '\0','2026-01-31 18:01:20.000000','Output intermittently low compared to wind conditions.','WARNING','INTERMITTENT_OUTPUT',3),(3,_binary '\0','2026-01-31 18:02:08.000000','Average power far below expected at good wind speed (possible drivetrain issue).','CRITICAL','LOW_PERFORMANCE',2),(4,_binary '\0','2026-01-31 18:02:08.000000','Output intermittently low compared to wind conditions.','WARNING','INTERMITTENT_OUTPUT',3),(5,_binary '\0','2026-01-31 18:07:03.000000','Average power far below expected at good wind speed (possible drivetrain issue).','CRITICAL','LOW_PERFORMANCE',2),(6,_binary '\0','2026-01-31 18:07:03.000000','Output intermittently low compared to wind conditions.','WARNING','INTERMITTENT_OUTPUT',3),(7,_binary '\0','2026-02-02 11:24:14.000000','Wind conditions fluctuating at site.','WARNING','Site-Wide Wind Variability',903),(8,_binary '\0','2026-02-02 11:29:14.000000','Wind conditions fluctuating at site.','WARNING','Site-Wide Wind Variability',1721),(9,_binary '\0','2026-02-02 11:41:14.000000','Wind conditions fluctuating at site.','WARNING','Site-Wide Wind Variability',1517),(10,_binary '\0','2026-02-02 11:18:14.000000','Wind conditions fluctuating at site.','WARNING','Site-Wide Wind Variability',52),(11,_binary '\0','2026-02-02 12:16:14.000000','Wind conditions fluctuating at site.','WARNING','Site-Wide Wind Variability',1857),(12,_binary '\0','2026-02-02 12:32:14.000000','Wind conditions fluctuating at site.','WARNING','Site-Wide Wind Variability',1974),(13,_binary '\0','2026-02-02 11:19:14.000000','Wind conditions fluctuating at site.','WARNING','Site-Wide Wind Variability',816),(14,_binary '\0','2026-02-02 12:11:14.000000','Wind conditions fluctuating at site.','WARNING','Site-Wide Wind Variability',901),(15,_binary '\0','2026-02-02 12:45:14.000000','Wind conditions fluctuating at site.','WARNING','Site-Wide Wind Variability',1936),(16,_binary '\0','2026-02-02 11:13:14.000000','Wind conditions fluctuating at site.','WARNING','Site-Wide Wind Variability',837),(22,_binary '\0','2026-02-02 12:40:40.000000','Auto-generated alert for TURB-1957','WARNING','LOW_PERFORMANCE',2099),(23,_binary '\0','2026-02-02 12:17:40.000000','Auto-generated alert for TURB-0197','INFO','OVER_TEMPERATURE',327),(24,_binary '\0','2026-02-02 12:08:40.000000','Auto-generated alert for TURB-1651','CRITICAL','OVER_TEMPERATURE',1487),(25,_binary '\0','2026-02-02 12:27:40.000000','Auto-generated alert for TURB-0311','INFO','OVER_TEMPERATURE',968),(26,_binary '\0','2026-02-02 12:36:40.000000','Auto-generated alert for TURB-0505','INFO','OVER_TEMPERATURE',1396),(27,_binary '\0','2026-02-02 12:12:40.000000','Auto-generated alert for TURB-0146','WARNING','OVER_TEMPERATURE',474),(28,_binary '\0','2026-02-02 12:08:40.000000','Auto-generated alert for TURB-1893','CRITICAL','OVER_TEMPERATURE',1811),(29,_binary '\0','2026-02-02 12:51:40.000000','Auto-generated alert for TURB-0132','INFO','OVER_TEMPERATURE',492),(30,_binary '\0','2026-02-02 12:06:40.000000','Auto-generated alert for TURB-2110','WARNING','LOW_PERFORMANCE',604),(31,_binary '\0','2026-02-02 12:00:40.000000','Auto-generated alert for TURB-1490','INFO','OVER_TEMPERATURE',1045),(32,_binary '\0','2026-02-02 12:50:40.000000','Auto-generated alert for TURB-0895','WARNING','LOW_PERFORMANCE',1816),(33,_binary '\0','2026-02-02 12:58:40.000000','Auto-generated alert for TURB-2012','CRITICAL','OVER_TEMPERATURE',250),(34,_binary '\0','2026-02-02 12:30:40.000000','Auto-generated alert for TURB-0032','WARNING','OVER_TEMPERATURE',192),(35,_binary '\0','2026-02-02 12:02:40.000000','Auto-generated alert for TURB-0222','INFO','LOW_PERFORMANCE',750),(36,_binary '\0','2026-02-02 12:40:40.000000','Auto-generated alert for TURB-0092','INFO','OVER_TEMPERATURE',12),(37,_binary '\0','2026-02-02 12:41:40.000000','Auto-generated alert for TURB-2098','INFO','LOW_PERFORMANCE',28),(38,_binary '\0','2026-02-02 12:46:40.000000','Auto-generated alert for TURB-1901','CRITICAL','OVER_TEMPERATURE',2187),(39,_binary '\0','2026-02-02 12:18:40.000000','Auto-generated alert for TURB-1501','WARNING','OVER_TEMPERATURE',1387),(40,_binary '\0','2026-02-02 12:18:40.000000','Auto-generated alert for TURB-0008','INFO','OVER_TEMPERATURE',300),(41,_binary '\0','2026-02-02 12:13:40.000000','Auto-generated alert for TURB-0239','INFO','LOW_PERFORMANCE',744),(53,_binary '\0','2026-02-02 12:03:43.000000','Auto-generated alert for TURB-1381','WARNING','OVER_TEMPERATURE',827),(54,_binary '\0','2026-02-02 12:24:43.000000','Auto-generated alert for TURB-0712','WARNING','OVER_TEMPERATURE',1770),(55,_binary '\0','2026-02-02 12:36:43.000000','Auto-generated alert for TURB-2094','WARNING','OVER_TEMPERATURE',16),(56,_binary '\0','2026-02-02 12:48:43.000000','Auto-generated alert for TURB-1017','WARNING','LOW_PERFORMANCE',266),(57,_binary '\0','2026-02-02 12:32:43.000000','Auto-generated alert for TURB-1649','CRITICAL','OVER_TEMPERATURE',1523),(58,_binary '\0','2026-02-02 12:39:43.000000','Auto-generated alert for TURB-1344','WARNING','OVER_TEMPERATURE',913),(59,_binary '\0','2026-02-02 12:50:43.000000','Auto-generated alert for TURB-0704','WARNING','LOW_PERFORMANCE',1794),(60,_binary '\0','2026-02-02 12:25:43.000000','Auto-generated alert for TURB-1343','WARNING','OVER_TEMPERATURE',911),(61,_binary '\0','2026-02-02 12:42:43.000000','Auto-generated alert for TURB-0534','WARNING','OVER_TEMPERATURE',1334),(62,_binary '\0','2026-02-02 12:39:43.000000','Auto-generated alert for TURB-1161','WARNING','OVER_TEMPERATURE',398),(63,_binary '\0','2026-02-02 12:05:43.000000','Auto-generated alert for TURB-1099','INFO','LOW_PERFORMANCE',32),(64,_binary '\0','2026-02-02 12:20:43.000000','Auto-generated alert for TURB-0784','CRITICAL','LOW_PERFORMANCE',1634),(65,_binary '\0','2026-02-02 12:49:43.000000','Auto-generated alert for TURB-0434','INFO','OVER_TEMPERATURE',1134),(66,_binary '\0','2026-02-02 12:32:43.000000','Auto-generated alert for TURB-1847','CRITICAL','LOW_PERFORMANCE',1919),(67,_binary '\0','2026-02-02 12:18:43.000000','Auto-generated alert for TURB-0806','WARNING','OVER_TEMPERATURE',1998),(68,_binary '\0','2026-02-02 12:51:43.000000','Auto-generated alert for TURB-1783','INFO','OVER_TEMPERATURE',1631),(69,_binary '\0','2026-02-02 12:35:43.000000','Auto-generated alert for TURB-0711','CRITICAL','OVER_TEMPERATURE',1768),(70,_binary '\0','2026-02-02 12:53:43.000000','Auto-generated alert for TURB-0509','WARNING','OVER_TEMPERATURE',1404),(71,_binary '\0','2026-02-02 12:44:43.000000','Auto-generated alert for TURB-1400','CRITICAL','OVER_TEMPERATURE',825),(72,_binary '\0','2026-02-02 12:12:43.000000','Auto-generated alert for TURB-0233','WARNING','OVER_TEMPERATURE',732);
/*!40000 ALTER TABLE `health_alerts` ENABLE KEYS */;
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
