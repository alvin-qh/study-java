-- 组织表
CREATE TABLE IF NOT EXISTS `org`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `name`       VARCHAR(100) NOT NULL,
    `deleted`    BIGINT       NOT NULL DEFAULT 0,
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ix_org_name` (`name`, `deleted`) -- name 字段联合 deleted 字段建立索引, 防止软删除的组织和新建组织 name 相同, 同时不影响未删除组织 name 字段的唯一性
) ENGINE = InnoDB
  CHARSET = utf8mb4;

-- 用户表
CREATE TABLE IF NOT EXISTS `user`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `org_id`     BIGINT       NOT NULL,
    `account`    VARCHAR(100) NOT NULL,
    `password`   VARCHAR(255) NOT NULL,
    `deleted`    BIGINT       NOT NULL DEFAULT 0,
    `created_by` BIGINT,
    `updated_by` BIGINT,
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`org_id`, `id`),
    UNIQUE KEY `ux_user_id` (`id`),
    UNIQUE KEY `ux_user_account` (`org_id`, `account`, `deleted`) -- account 字段联合 deleted 字段建立索引, 防止软删除的用户和新建用户 account 相同, 同时不影响未删除用户 account 字段的唯一性
) ENGINE = InnoDB
  CHARSET = utf8mb4;

-- 员工表
CREATE TABLE IF NOT EXISTS `employee`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `org_id`     BIGINT       NOT NULL,
    `name`       VARCHAR(100) NOT NULL,
    `email`      VARCHAR(100) NOT NULL,
    `title`      VARCHAR(50)  NOT NULL,
    `deleted`    BIGINT       NOT NULL DEFAULT 0,
    `created_by` BIGINT,
    `updated_by` BIGINT,
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`org_id`, `id`),
    UNIQUE KEY `ux_employee_id` (`id`),
    UNIQUE KEY `ux_employee_name` (`org_id`, `name`, `deleted`), -- name 字段联合 deleted 字段建立索引, 防止软删除的职员和新建职员 name 相同, 同时不影响未删除职员 name 字段的唯一性
    UNIQUE KEY `ux_employee_email` (`org_id`, `email`, `deleted`)
) ENGINE = InnoDB
  CHARSET = utf8mb4;

-- 部门表
CREATE TABLE IF NOT EXISTS `department`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `org_id`     BIGINT       NOT NULL,
    `name`       VARCHAR(100) NOT NULL,
    `parent_id`  BIGINT,
    `deleted`    BIGINT       NOT NULL DEFAULT 0,
    `created_by` BIGINT,
    `updated_by` BIGINT,
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`org_id`, `id`),
    UNIQUE KEY `ux_department_id` (`id`),
    UNIQUE KEY `ux_department_name` (`org_id`, `name`, `deleted`) -- name 字段联合 deleted 字段建立索引, 防止软删除的部门和新建部门 name 相同, 同时不影响未删除部门 name 字段的唯一性
) ENGINE = InnoDB
  CHARSET = utf8mb4;

-- 员工部门关系表
CREATE TABLE IF NOT EXISTS `department_employee`
(
    `id`            BIGINT   NOT NULL AUTO_INCREMENT,
    `org_id`        BIGINT   NOT NULL,
    `employee_id`   BIGINT   NOT NULL,
    `department_id` BIGINT   NOT NULL,
    `created_by`    BIGINT,
    `updated_by`    BIGINT,
    `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`org_id`, `id`),
    UNIQUE KEY `ux_department_employee_id` (`id`),
    KEY `ix_department_employee_employee_id` (`org_id`, `employee_id`),
    KEY `ix_department_employee_department_id` (`org_id`, `department_id`)
) ENGINE = InnoDB
  CHARSET = utf8mb4;
