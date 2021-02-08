CREATE DATABASE  IF NOT EXISTS `esempiodb`;
USE `esempiodb`;
DROP TABLE IF EXISTS `persona`;
CREATE TABLE `persona` (
  `cognome` varchar(45) NOT NULL,
  `nome` varchar(45) NOT NULL,
  PRIMARY KEY (`nome`)
) ENGINE=InnoDB;
