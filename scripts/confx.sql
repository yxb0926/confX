-- MySQL dump 10.13  Distrib 5.6.29, for Linux (x86_64)
--
-- Host: localhost    Database: confx
-- ------------------------------------------------------
-- Server version	5.6.29-log

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
-- Current Database: `confx`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `confx` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `confx`;

--
-- Table structure for table `appname_info`
--

DROP TABLE IF EXISTS `appname_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `appname_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `appname` varchar(255) NOT NULL DEFAULT '',
  `pname` varchar(100) DEFAULT NULL,
  `groupname` varchar(255) NOT NULL DEFAULT '',
  `type` varchar(60) NOT NULL DEFAULT '',
  `created_time` datetime NOT NULL DEFAULT '2016-07-13 00:00:00',
  `modified_time` datetime NOT NULL DEFAULT '2016-07-13 00:00:00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk` (`appname`,`pname`,`groupname`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_list`
--

DROP TABLE IF EXISTS `client_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pname` varchar(200) NOT NULL DEFAULT '',
  `client_ip` varchar(20) NOT NULL DEFAULT '',
  `isdel` tinyint(4) DEFAULT '0' COMMENT '0-未删除，1-已删除',
  `gmt_created` datetime NOT NULL DEFAULT '2016-07-25 00:00:00',
  `gmt_modified` datetime NOT NULL DEFAULT '2016-07-25 00:00:00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk` (`pname`,`client_ip`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `config_info`
--

DROP TABLE IF EXISTS `config_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `config_info` (
  `id` bigint(64) unsigned NOT NULL AUTO_INCREMENT,
  `program_id` varchar(100) DEFAULT NULL,
  `data_id` varchar(100) DEFAULT NULL,
  `group_id` varchar(100) DEFAULT NULL,
  `content` longtext NOT NULL,
  `md5` varchar(32) NOT NULL DEFAULT '',
  `gmt_create` datetime NOT NULL DEFAULT '2010-05-05 00:00:00',
  `gmt_modified` datetime NOT NULL DEFAULT '2010-05-05 00:00:00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk` (`program_id`,`data_id`,`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupname_info_mysql`
--

DROP TABLE IF EXISTS `groupname_info_mysql`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupname_info_mysql` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `appname` varchar(255) NOT NULL DEFAULT '',
  `pname` varchar(100) DEFAULT NULL COMMENT 'program name',
  `groupname` varchar(255) NOT NULL DEFAULT '',
  `dbname` varchar(255) NOT NULL DEFAULT '',
  `role` varchar(10) NOT NULL DEFAULT '',
  `ip` varchar(100) NOT NULL DEFAULT '',
  `port` int(11) NOT NULL DEFAULT '0',
  `user` varchar(100) NOT NULL DEFAULT '',
  `passwd` varchar(100) NOT NULL DEFAULT '',
  `charset` varchar(100) NOT NULL DEFAULT '',
  `tbprefix` varchar(100) NOT NULL DEFAULT '',
  `timeout` int(11) NOT NULL DEFAULT '1',
  `created_time` datetime NOT NULL DEFAULT '2016-07-13 00:00:00',
  `modified_time` datetime NOT NULL DEFAULT '2016-07-13 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groupname_info_redis`
--

DROP TABLE IF EXISTS `groupname_info_redis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groupname_info_redis` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `appname` varchar(255) NOT NULL DEFAULT '',
  `pname` varchar(100) DEFAULT NULL,
  `groupname` varchar(255) NOT NULL,
  `timeout` int(11) NOT NULL,
  `read_timeout` int(11) NOT NULL,
  `role` varchar(20) NOT NULL,
  `ip` varchar(20) NOT NULL,
  `port` int(11) NOT NULL,
  `created_time` datetime NOT NULL DEFAULT '2016-07-13 00:00:00',
  `modified_time` datetime NOT NULL DEFAULT '2016-07-13 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_info`
--

DROP TABLE IF EXISTS `project_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pcode` varchar(255) NOT NULL DEFAULT '',
  `pname` varchar(100) DEFAULT NULL,
  `ptype` varchar(20) NOT NULL DEFAULT '',
  `powner` varchar(256) NOT NULL DEFAULT '',
  `pdesc` varchar(256) NOT NULL DEFAULT '',
  `pfilename` varchar(200) DEFAULT NULL,
  `addtime` datetime DEFAULT '0000-00-00 00:00:00',
  `modifytime` datetime DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk` (`pcode`,`pname`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_project`
--

DROP TABLE IF EXISTS `project_project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_project` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pname` varchar(255) NOT NULL DEFAULT '',
  `pdesc` varchar(200) NOT NULL DEFAULT '',
  `owner` varchar(200) NOT NULL DEFAULT '',
  `path` varchar(200) DEFAULT NULL,
  `pcmd` varchar(200) NOT NULL,
  `psysuser` varchar(20) NOT NULL,
  `pcodetype` varchar(20) NOT NULL DEFAULT '',
  `gmt_created` datetime NOT NULL DEFAULT '2016-01-01 00:00:00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk` (`pname`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-09-07 11:48:12
