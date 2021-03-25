CREATE TABLE `frequency` (
	`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(255) NOT NULL COLLATE 'utf8_unicode_ci',
	PRIMARY KEY (`id`) USING BTREE
)
COLLATE='utf8_unicode_ci'
ENGINE=InnoDB
AUTO_INCREMENT=11
;

CREATE TABLE `platform` (
	`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(255) NOT NULL COLLATE 'utf8_unicode_ci',
	`url` VARCHAR(255) NOT NULL COLLATE 'utf8_unicode_ci',
	`settings_json` VARCHAR(1024) NULL DEFAULT NULL COLLATE 'utf8_unicode_ci',
	`last_checked_at` DATE NULL DEFAULT NULL,
	`update_time` INT(2) NOT NULL DEFAULT '0',
	`data_type` VARCHAR(50) NOT NULL COLLATE 'utf8_unicode_ci',
	`created_at` TIMESTAMP NULL DEFAULT NULL,
	`updated_at` TIMESTAMP NULL DEFAULT NULL,
	PRIMARY KEY (`id`) USING BTREE
)
COLLATE='utf8_unicode_ci'
ENGINE=InnoDB
AUTO_INCREMENT=5
;

CREATE TABLE `title` (
	`id` INT(10) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(255) NOT NULL COLLATE 'utf8_unicode_ci',
	`platform` INT(10) NOT NULL,
	`frequency` INT(10) NOT NULL,
	`last_updated_at` DATE NULL DEFAULT current_timestamp(),
	`last_checked_at` DATE NULL DEFAULT current_timestamp(),
	`latest_url` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8_unicode_ci',
	`url` VARCHAR(255) NOT NULL COLLATE 'utf8_unicode_ci',
	`update_check_url` VARCHAR(255) NOT NULL COLLATE 'utf8_unicode_ci',
	`finished` TINYINT(1) NOT NULL,
	`created_at` TIMESTAMP NULL DEFAULT NULL,
	`updated_at` TIMESTAMP NULL DEFAULT NULL
)
COLLATE='utf8_unicode_ci'
ENGINE=InnoDB
;

CREATE TABLE `scraping_history` (
	`last_checked_at` TIMESTAMP NOT NULL DEFAULT current_timestamp()
)
COLLATE='utf8_unicode_ci'
ENGINE=InnoDB
;
