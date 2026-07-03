-- 参考用，实际表结构以数据库 SHOW CREATE TABLE 为准

CREATE TABLE IF NOT EXISTS `notification_message` (
    `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`     BIGINT(20)   NOT NULL COMMENT '接收用户',
    `title`       VARCHAR(100) DEFAULT NULL COMMENT '标题',
    `content`     VARCHAR(500) DEFAULT NULL COMMENT '内容',
    `priority`    INT(11)      DEFAULT '0' COMMENT '消息优先级',
    `type`        VARCHAR(30)  DEFAULT NULL COMMENT '消息类型：TASK_START/TASK_DEADLINE/TASK_WARNING/TASK_END',
    `related_id`  BIGINT(20)   DEFAULT NULL COMMENT '关联业务id',
    `status`      CHAR(1)      DEFAULT '0' COMMENT '状态：0有效 1失效',
    `is_read`     TINYINT(4)   DEFAULT '0' COMMENT '是否已读：0未读 1已读',
    `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `notify_time` DATETIME     DEFAULT NULL COMMENT '通知生效时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_status` (`user_id`, `status`),
    KEY `idx_related_type` (`related_id`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知消息表';

CREATE TABLE IF NOT EXISTS `leave_request` (
    `id`            BIGINT(20)     NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`       BIGINT(20)     NOT NULL COMMENT '请假学生ID',
    `leave_type`    CHAR(1)        DEFAULT NULL COMMENT '请假类型：1事假 2病假 3其他',
    `start_date`    DATE           NOT NULL COMMENT '开始日期',
    `start_period`  VARCHAR(10)    NOT NULL COMMENT '开始时段：AM上午 PM下午 EV晚上',
    `end_date`      DATE           NOT NULL COMMENT '结束日期',
    `end_period`    VARCHAR(10)    NOT NULL COMMENT '结束时段：AM上午 PM下午 EV晚上',
    `leave_days`    DECIMAL(5,2)   NOT NULL COMMENT '请假天数',
    `reason`        VARCHAR(1000)  DEFAULT NULL COMMENT '请假原因',
    `approver_id`   BIGINT(20)     DEFAULT NULL COMMENT '审批人ID',
    `approve_role`  CHAR(1)        DEFAULT NULL COMMENT '审批角色：2班长 1老师',
    `status`        CHAR(1)        DEFAULT '0' COMMENT '状态：0待审批 1通过 2拒绝',
    `approve_remark` VARCHAR(500)  DEFAULT NULL COMMENT '审批意见',
    `approve_time`  DATETIME       DEFAULT NULL COMMENT '审批时间',
    `create_by`     BIGINT(20)     DEFAULT NULL COMMENT '创建人',
    `create_time`   DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`     BIGINT(20)     DEFAULT NULL COMMENT '修改人',
    `update_time`   DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_approver_id` (`approver_id`),
    KEY `idx_status` (`status`),
    KEY `idx_start_date` (`start_date`),
    KEY `idx_end_date` (`end_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请假申请表';
