CREATE TABLE IF NOT EXISTS `kg_user` (
    `id`        BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`  VARCHAR(64) NOT NULL COMMENT '用户名',
    `email`     VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `create_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
