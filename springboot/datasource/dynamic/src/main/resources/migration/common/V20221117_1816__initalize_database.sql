-- 配置表
CREATE TABLE IF NOT EXISTS `config`
(
    `id`         BIGINT      NOT NULL AUTO_INCREMENT,
    `org`        VARCHAR(50) NOT NULL,
    `db_name`    VARCHAR(50) NOT NULL,
    `valid`      TINYINT(1)  NOT NULL DEFAULT 1,
    `created_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ux_config_org` (`org`),
    UNIQUE KEY `ux_config_db_name` (`db_name`)
) ENGINE = InnoDB
  CHARSET = utf8mb4;
