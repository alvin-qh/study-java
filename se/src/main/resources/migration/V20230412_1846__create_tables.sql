CREATE TABLE mptt (
    `id`   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50),
    `lft`  BIGINT UNSIGNED,
    `rht`  BIGINT UNSIGNED,
    PRIMARY KEY (`id`),
    KEY `ix_lft` (`lft`),
    KEY `ix_rht` (`rht`)
);
