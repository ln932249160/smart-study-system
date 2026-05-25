-- sys_user 建表语句（含 name 字段）
-- 实际表结构以数据库 SHOW CREATE TABLE 为准

CREATE TABLE IF NOT EXISTS `sys_user` (
    `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `account`     VARCHAR(50)  NOT NULL COMMENT '账号',
    `name`        VARCHAR(50)  DEFAULT NULL COMMENT '姓名',
    `password`    VARCHAR(255) NOT NULL COMMENT '密码（BCrypt 加密）',
    `role`        VARCHAR(20)  NOT NULL COMMENT '角色：teacher/headmaster/student',
    `gender`      TINYINT(4)   DEFAULT '0' COMMENT '性别：0未知 1男 2女',
    `email`       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `openid`      VARCHAR(100) DEFAULT NULL COMMENT '微信openid（小程序唯一标识）',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `class_id`    BIGINT(20)   DEFAULT NULL COMMENT '班级ID',
    `status`      TINYINT(4)   DEFAULT '1' COMMENT '状态：1正常 0禁用',
    `create_by`   BIGINT(20)   DEFAULT NULL COMMENT '创建人',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   BIGINT(20)   DEFAULT NULL COMMENT '修改人',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `account` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
