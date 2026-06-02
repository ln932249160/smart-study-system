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

delete from sys_dict;
INSERT INTO sys_dict (dict_code,dict_name,dict_value,sort_no,status,create_by,create_time,update_by,update_time) VALUES
	 ('role','老师','1',1,1,NULL,'2026-05-26 21:36:08',NULL,'2026-05-26 21:36:08'),
	 ('role','班长','2',2,1,NULL,'2026-05-26 21:36:08',NULL,'2026-05-26 21:36:08'),
	 ('role','学生','3',3,1,NULL,'2026-05-26 21:36:08',NULL,'2026-05-26 21:36:08'),
	 ('task_status','未完成','0',1,1,NULL,'2026-05-26 21:36:08',NULL,'2026-05-26 21:36:08'),
	 ('task_status','已完成','1',2,1,NULL,'2026-05-26 21:36:08',NULL,'2026-05-26 21:36:08'),
	 ('module_name','行测-常识','1',1,1,NULL,'2026-05-26 21:36:08',NULL,'2026-05-26 21:36:08'),
	 ('module_name','行测-言语','2',2,1,NULL,'2026-05-26 21:36:08',NULL,'2026-05-26 21:36:08'),
	 ('module_name','行测-数量','3',3,1,NULL,'2026-05-26 21:36:08',NULL,'2026-05-26 21:36:08'),
	 ('module_name','行测-判断推理','4',4,1,NULL,'2026-05-26 21:36:08',NULL,'2026-05-26 21:36:08'),
	 ('module_name','行测-资料分析','5',5,1,NULL,'2026-05-26 21:36:08',NULL,'2026-05-26 21:36:08');
INSERT INTO sys_dict (dict_code,dict_name,dict_value,sort_no,status,create_by,create_time,update_by,update_time) VALUES
	 ('module_name','申论-小题','6',6,1,NULL,'2026-05-26 21:36:08',NULL,'2026-05-26 21:36:08'),
	 ('module_name','申论-大作文','7',7,1,NULL,'2026-05-26 21:36:08',NULL,'2026-05-26 21:36:08');

CREATE TABLE IF NOT EXISTS `notification_message` (
    `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`     BIGINT(20)   NOT NULL COMMENT '接收用户',
    `title`       VARCHAR(100) DEFAULT NULL COMMENT '标题',
    `content`     VARCHAR(500) DEFAULT NULL COMMENT '内容',
    `priority`    INT(11)      DEFAULT '0' COMMENT '消息优先级',
    `type`        VARCHAR(30)  DEFAULT NULL COMMENT '消息类型',
    `related_id`  BIGINT(20)   DEFAULT NULL COMMENT '关联业务id',
    `is_read`     TINYINT(4)   DEFAULT '0' COMMENT '是否已读',
    `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `notify_time` DATETIME     DEFAULT NULL COMMENT '通知生效时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知消息表';

