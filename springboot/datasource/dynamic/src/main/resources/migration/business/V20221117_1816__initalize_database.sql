-- 数据表
CREATE TABLE IF NOT EXISTS `data`
(
    `id`         BIGINT        NOT NULL AUTO_INCREMENT,
    `name`       VARCHAR(50)   NOT NULL,
    `value`      VARCHAR(1000) NOT NULL,
    `created_at` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ux_data_name` (`name`)
) ENGINE = InnoDB
  CHARSET = utf8mb4;
