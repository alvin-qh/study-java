CREATE TABLE `mptt`
(
    `id`   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50)     NOT NULL,
    `pid`  BIGINT UNSIGNED NOT NULL DEFAULT 0,
    `lft`  BIGINT UNSIGNED NOT NULL DEFAULT 0,
    `rht`  BIGINT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `ix_pid` (`pid`),
    KEY `ix_lft` (`lft`),
    KEY `ix_rht` (`rht`)
);
