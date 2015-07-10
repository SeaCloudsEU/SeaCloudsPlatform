-- MySQL dump 10.13  Distrib 5.6.16, for Win64 (x86_64)
--
-- Host: localhost    Database: sc_sla
-- ------------------------------------------------------
-- Server version	5.6.16-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `agreement`
--

DROP TABLE IF EXISTS `agreement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `agreement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `agreement_id` varchar(255) DEFAULT NULL,
  `consumer` varchar(255) DEFAULT NULL,
  `expiration_time` datetime DEFAULT NULL,
  `metrics_eval_end` tinyint(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `service_id` varchar(255) DEFAULT NULL,
  `text` longtext,
  `provider_id` bigint(20) NOT NULL,
  `template_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_b3upn8jbq7ryrj26mpc5hccf3` (`agreement_id`),
  KEY `FK_hlyajkn6gfqd9vd9u2ne74dbf` (`provider_id`),
  KEY `FK_ob6aljvohdv1g1nckphj69974` (`template_id`),
  CONSTRAINT `FK_ob6aljvohdv1g1nckphj69974` FOREIGN KEY (`template_id`) REFERENCES `template` (`id`),
  CONSTRAINT `FK_hlyajkn6gfqd9vd9u2ne74dbf` FOREIGN KEY (`provider_id`) REFERENCES `provider` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `breach`
--

DROP TABLE IF EXISTS `breach`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `breach` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `contract_uuid` varchar(255) DEFAULT NULL,
  `datetime` datetime DEFAULT NULL,
  `kpi_name` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `violation_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_l215s831vj7ar4rpecpggg2xp` (`violation_id`),
  CONSTRAINT `FK_l215s831vj7ar4rpecpggg2xp` FOREIGN KEY (`violation_id`) REFERENCES `violation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `business_value_list`
--

DROP TABLE IF EXISTS `business_value_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `business_value_list` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `importance` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `enforcement_job`
--

DROP TABLE IF EXISTS `enforcement_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `enforcement_job` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `enabled` bit(1) DEFAULT NULL,
  `first_executed` datetime DEFAULT NULL,
  `last_executed` datetime DEFAULT NULL,
  `agreement_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_e63hbo5jhctm96xdwu93cls0v` (`agreement_id`),
  CONSTRAINT `FK_e63hbo5jhctm96xdwu93cls0v` FOREIGN KEY (`agreement_id`) REFERENCES `agreement` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `guarantee_term`
--

DROP TABLE IF EXISTS `guarantee_term`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `guarantee_term` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `kpi_name` varchar(255) DEFAULT NULL,
  `lastSampledDate` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `samplingPeriodFactor` int(11) DEFAULT NULL,
  `service_level` varchar(255) DEFAULT NULL,
  `service_name` varchar(255) DEFAULT NULL,
  `service_scope` varchar(255) DEFAULT NULL,
  `status` int(11) NOT NULL,
  `business_value_id` bigint(20) DEFAULT NULL,
  `agreement_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_7vtc6h6w1len3s3xrol2rma8o` (`business_value_id`),
  KEY `FK_pufedjn479knv894hyqrlfpgx` (`agreement_id`),
  CONSTRAINT `FK_pufedjn479knv894hyqrlfpgx` FOREIGN KEY (`agreement_id`) REFERENCES `agreement` (`id`),
  CONSTRAINT `FK_7vtc6h6w1len3s3xrol2rma8o` FOREIGN KEY (`business_value_id`) REFERENCES `business_value_list` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `penalty`
--

DROP TABLE IF EXISTS `penalty`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `penalty` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `agreement_id` varchar(255) DEFAULT NULL,
  `datetime` datetime DEFAULT NULL,
  `kpi_name` varchar(255) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `definition_id` bigint(20) NOT NULL,
  `violation_id` bigint(20) NOT NULL,
  `guarantee_term_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_s4japyqgwxvss8e63ejryo4js` (`definition_id`),
  KEY `FK_ecbrf8mo6b45v7s2ilcpb1qb6` (`violation_id`),
  KEY `FK_v8in0arhs3ff1i109d9ftxbm` (`guarantee_term_id`),
  CONSTRAINT `FK_v8in0arhs3ff1i109d9ftxbm` FOREIGN KEY (`guarantee_term_id`) REFERENCES `guarantee_term` (`id`),
  CONSTRAINT `FK_ecbrf8mo6b45v7s2ilcpb1qb6` FOREIGN KEY (`violation_id`) REFERENCES `violation` (`id`),
  CONSTRAINT `FK_s4japyqgwxvss8e63ejryo4js` FOREIGN KEY (`definition_id`) REFERENCES `penaltydefinition` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `penaltydefinition`
--

DROP TABLE IF EXISTS `penaltydefinition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `penaltydefinition` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `action` varchar(255) NOT NULL,
  `number` int(11) NOT NULL,
  `kind` varchar(255) NOT NULL,
  `time_interval` datetime NOT NULL,
  `validity` varchar(255) NOT NULL,
  `value_expression` varchar(255) NOT NULL,
  `value_unit` varchar(255) NOT NULL,
  `business_value_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_5xxk7uh9k3v0uqt0pd5cwhi6j` (`business_value_id`),
  CONSTRAINT `FK_5xxk7uh9k3v0uqt0pd5cwhi6j` FOREIGN KEY (`business_value_id`) REFERENCES `business_value_list` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `policy`
--

DROP TABLE IF EXISTS `policy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `policy` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `number` int(11) DEFAULT NULL,
  `time_interval` datetime DEFAULT NULL,
  `variable` varchar(255) DEFAULT NULL,
  `guarantee_term_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_p4wup2luxq7ido6gqris3mmd8` (`guarantee_term_id`),
  CONSTRAINT `FK_p4wup2luxq7ido6gqris3mmd8` FOREIGN KEY (`guarantee_term_id`) REFERENCES `guarantee_term` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `provider`
--

DROP TABLE IF EXISTS `provider`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `provider` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_cfgo93bl0v243co72ay26bs94` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `service_properties`
--

DROP TABLE IF EXISTS `service_properties`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_properties` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `service_name` varchar(255) DEFAULT NULL,
  `agreement_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_q19s0rkjhma344krrm6rbfpdt` (`agreement_id`),
  CONSTRAINT `FK_q19s0rkjhma344krrm6rbfpdt` FOREIGN KEY (`agreement_id`) REFERENCES `agreement` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `template`
--

DROP TABLE IF EXISTS `template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `service_id` varchar(255) DEFAULT NULL,
  `text` longtext NOT NULL,
  `uuid` varchar(255) NOT NULL,
  `provider_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_m0a0t99pnntf75psmk1lptr3n` (`uuid`),
  KEY `FK_5p28ldeg0v7loq063g2s9gykx` (`provider_id`),
  CONSTRAINT `FK_5p28ldeg0v7loq063g2s9gykx` FOREIGN KEY (`provider_id`) REFERENCES `provider` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `variable`
--

DROP TABLE IF EXISTS `variable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `variable` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `location` varchar(255) DEFAULT NULL,
  `metric` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `service_properties_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_pyy0qp89btrs2q66ivtwe46ue` (`service_properties_id`),
  CONSTRAINT `FK_pyy0qp89btrs2q66ivtwe46ue` FOREIGN KEY (`service_properties_id`) REFERENCES `service_properties` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `violation`
--

DROP TABLE IF EXISTS `violation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `violation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `actual_value` varchar(255) DEFAULT NULL,
  `contract_uuid` varchar(255) DEFAULT NULL,
  `datetime` datetime DEFAULT NULL,
  `expected_value` varchar(255) DEFAULT NULL,
  `kpi_name` varchar(255) DEFAULT NULL,
  `service_name` varchar(255) DEFAULT NULL,
  `service_scope` varchar(255) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `policy_id` bigint(20) DEFAULT NULL,
  `guarantee_term_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_dxe6b01v3e51k3ab3fy7rk4xv` (`policy_id`),
  KEY `FK_ia6898r6ple40dimipcg8ru2u` (`guarantee_term_id`),
  CONSTRAINT `FK_ia6898r6ple40dimipcg8ru2u` FOREIGN KEY (`guarantee_term_id`) REFERENCES `guarantee_term` (`id`),
  CONSTRAINT `FK_dxe6b01v3e51k3ab3fy7rk4xv` FOREIGN KEY (`policy_id`) REFERENCES `policy` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-04-20 17:06:04
