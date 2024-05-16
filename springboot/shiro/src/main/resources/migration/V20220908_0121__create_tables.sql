-- 用户表
CREATE TABLE IF NOT EXISTS `user`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `account`    VARCHAR(100) NOT NULL,
    `password`   VARCHAR(255) NOT NULL,
    `type`       VARCHAR(50)  NOT NULL,
    `deleted`    BIGINT       NOT NULL DEFAULT 0,
    `created_by` BIGINT,
    `updated_by` BIGINT,
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ux_user_account` (`account`, `deleted`)
) ENGINE = InnoDB
  CHARSET = utf8mb4;

-- 用户分组表
CREATE TABLE IF NOT EXISTS `group`
(
    `id`         BIGINT      NOT NULL AUTO_INCREMENT,
    `name`       VARCHAR(50) NOT NULL,
    `created_by` BIGINT,
    `updated_by` BIGINT,
    `created_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ux_group_name` (`name`)
) ENGINE = InnoDB
  CHARSET = utf8mb4;

-- 用户分组关系表
CREATE TABLE IF NOT EXISTS `user_group`
(
    `id`         BIGINT   NOT NULL AUTO_INCREMENT,
    `user_id`    BIGINT   NOT NULL,
    `group_id`   BIGINT   NOT NULL,
    `created_by` BIGINT,
    `updated_by` BIGINT,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `ix_user_group_user_id` (`user_id`),
    KEY `ix_user_group_group_id` (`group_id`)
) ENGINE = InnoDB
  CHARSET = utf8mb4;

-- 角色表
CREATE TABLE IF NOT EXISTS `role`
(
    `id`         BIGINT      NOT NULL AUTO_INCREMENT,
    `name`       VARCHAR(50) NOT NULL,
    `created_by` BIGINT,
    `updated_by` BIGINT,
    `created_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ux_role_name` (`name`)
) ENGINE = InnoDB
  CHARSET = utf8mb4;

-- 权限表
CREATE TABLE IF NOT EXISTS `permission`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `name`       VARCHAR(50)  NOT NULL,
    `resource`   VARCHAR(100) NOT NULL,
    `action`     VARCHAR(20)  NOT NULL,
    `created_by` BIGINT,
    `updated_by` BIGINT,
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ux_permission` (`name`, `resource`, `action`)
) ENGINE = InnoDB
  CHARSET = utf8mb4;

-- 角色权限关系表
CREATE TABLE IF NOT EXISTS `role_permission`
(
    `id`            BIGINT   NOT NULL AUTO_INCREMENT,
    `role_id`       BIGINT   NOT NULL,
    `permission_id` BIGINT   NOT NULL,
    `created_by`    BIGINT,
    `updated_by`    BIGINT,
    `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `ix_role_permission_role_id` (`role_id`),
    KEY `ix_role_permission_permission_id` (`permission_id`)
) ENGINE = InnoDB
  CHARSET = utf8mb4;

-- 用户/组角色表
CREATE TABLE IF NOT EXISTS `role_grant`
(
    `id`               BIGINT      NOT NULL AUTO_INCREMENT,
    `user_or_group_id` BIGINT      NOT NULL,
    `type`             VARCHAR(20) NOT NULL,
    `role_id`          BIGINT      NOT NULL,
    `created_by`       BIGINT,
    `updated_by`       BIGINT,
    `created_at`       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `ix_role_grant_user_or_group_id` (`type`, `user_or_group_id`),
    KEY `ix_role_grant_user_or_role_id` (`role_id`)
) ENGINE = InnoDB
  CHARSET = utf8mb4;

-- 菜单表
CREATE TABLE IF NOT EXISTS `menu`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `order`         INT UNSIGNED NOT NULL,
    `text`          VARCHAR(20)  NOT NULL,
    `icon`          VARCHAR(50)  NOT NULL,
    `parent_id`     BIGINT,
    `role_id`       BIGINT,
    `permission_id` BIGINT,
    PRIMARY KEY (`id`),
    KEY `ix_menu_text` (`text`)
) ENGINE = InnoDB
  CHARSET = utf8mb4;

-- SESSION 表
CREATE TABLE IF NOT EXISTS `session`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `key`        VARCHAR(100) NOT NULL,
    `value`      TEXT         NOT NULL,
    `expired_at` DATETIME     NOT NULL,
    `created_by` BIGINT,
    `updated_by` BIGINT,
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `ux_session_key` (`key`)
) ENGINE = InnoDB
  CHARSET = utf8mb4;
