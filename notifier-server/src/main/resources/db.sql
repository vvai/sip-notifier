SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

DROP SCHEMA IF EXISTS `notifier` ;
CREATE SCHEMA IF NOT EXISTS `notifier` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `notifier` ;

-- -----------------------------------------------------
-- Table `notifier`.`user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `notifier`.`user` ;

CREATE  TABLE IF NOT EXISTS `notifier`.`user` (
  `iduser` INT NOT NULL AUTO_INCREMENT ,
  `username` VARCHAR(45) NOT NULL ,
  `sip` INT,
  `notified` BOOL NOT NULL,
  PRIMARY KEY (`iduser`) ,
  UNIQUE INDEX `username_UNIQUE` (`username` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `notifier`.`event`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `notifier`.`event` ;

CREATE  TABLE IF NOT EXISTS `notifier`.`event` (
  `idevent` INT NOT NULL AUTO_INCREMENT ,
  `uid` VARCHAR(128) NOT NULL,
  `description` VARCHAR(256) NULL ,
  `date` DATETIME NOT NULL ,
  `notified` BOOL NOT NULL ,
  `iduser` INT NOT NULL ,
  `auto_call` BOOL NOT NULL DEFAULT FALSE,
  `conference_number` INT ,
  PRIMARY KEY (`idevent`, `iduser`) ,
  INDEX `fk_event_user_idx` (`iduser` ASC) ,
  UNIQUE INDEX `uid_UNIQUE` (`uid` ASC),
  CONSTRAINT `fk_event_user`
    FOREIGN KEY (`iduser` )
    REFERENCES `notifier`.`user` (`iduser` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

USE `notifier` ;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
