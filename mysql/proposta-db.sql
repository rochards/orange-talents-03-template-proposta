-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema proposta
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema proposta
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `proposta` DEFAULT CHARACTER SET utf8 ;
USE `proposta` ;

-- -----------------------------------------------------
-- Table `proposta`.`proposta`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proposta`.`proposta` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `documento` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `endereco` VARCHAR(255) NOT NULL,
  `nome` VARCHAR(255) NOT NULL,
  `salario` DECIMAL(10,2) UNSIGNED NOT NULL,
  `status` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UK_ay0la7s80cky229aexg8pyvod` (`documento` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `proposta`.`cartao`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proposta`.`cartao` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `contas_id_cartao` VARCHAR(255) NOT NULL,
  `emitido_em` DATETIME(6) NOT NULL,
  `status` VARCHAR(255) NOT NULL,
  `proposta_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UK_1sscemwooe2btsu7reyg82knq` (`contas_id_cartao` ASC) VISIBLE,
  INDEX `FKp9vgcwkig6ql7r93tbll6dvn0` (`proposta_id` ASC) VISIBLE,
  CONSTRAINT `FKp9vgcwkig6ql7r93tbll6dvn0`
    FOREIGN KEY (`proposta_id`)
    REFERENCES `proposta`.`proposta` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `proposta`.`aviso_viagem`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proposta`.`aviso_viagem` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `destino` VARCHAR(255) NOT NULL,
  `em_viagem_ate` DATE NOT NULL,
  `ip_cliente` VARCHAR(255) NULL DEFAULT NULL,
  `registrado_em` DATETIME(6) NOT NULL,
  `status_aviso_no_legado` VARCHAR(255) NOT NULL,
  `user_agent` VARCHAR(255) NULL DEFAULT NULL,
  `cartao_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKt6y6bh63iy2bbi2avqv2eeb5b` (`cartao_id` ASC) VISIBLE,
  CONSTRAINT `FKt6y6bh63iy2bbi2avqv2eeb5b`
    FOREIGN KEY (`cartao_id`)
    REFERENCES `proposta`.`cartao` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `proposta`.`biometria`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proposta`.`biometria` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `cadastrada_em` DATETIME(6) NOT NULL,
  `impressao_digital` VARCHAR(255) NOT NULL,
  `cartao_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK4ndi2a9skyak1eyjr66m911l0` (`cartao_id` ASC) VISIBLE,
  CONSTRAINT `FK4ndi2a9skyak1eyjr66m911l0`
    FOREIGN KEY (`cartao_id`)
    REFERENCES `proposta`.`cartao` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `proposta`.`bloqueio`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proposta`.`bloqueio` (
  `bloqueado_em` DATETIME(6) NOT NULL,
  `ip_cliente` VARCHAR(255) NOT NULL,
  `status_cartao_no_legado` VARCHAR(255) NOT NULL,
  `user_agent_cliente` VARCHAR(255) NOT NULL,
  `cartao_id` INT NOT NULL,
  PRIMARY KEY (`cartao_id`),
  CONSTRAINT `FKfyikbq2kri522nirnxfokdnyu`
    FOREIGN KEY (`cartao_id`)
    REFERENCES `proposta`.`cartao` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `proposta`.`carteira_digital`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `proposta`.`carteira_digital` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `contas_id_carteira` VARCHAR(255) NULL DEFAULT NULL,
  `email` VARCHAR(255) NOT NULL,
  `nome` VARCHAR(255) NOT NULL,
  `status` VARCHAR(255) NOT NULL,
  `cartao_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKo4ax2gigtahkbuh0yk0p8fwod` (`cartao_id` ASC) VISIBLE,
  CONSTRAINT `FKo4ax2gigtahkbuh0yk0p8fwod`
    FOREIGN KEY (`cartao_id`)
    REFERENCES `proposta`.`cartao` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
