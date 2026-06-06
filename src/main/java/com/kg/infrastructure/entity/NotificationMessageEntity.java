package com.kg.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 通知消息持久化实体，映射 notification_message 表。
 */
@TableName("notification_message")
public class NotificationMessageEntity {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 接收用户ID */
    private Long userId;

    /** 标题 */
    private String title;

    /** 内容 */
    private String content;

    /** 优先级 */
    private Integer priority;

    /** 消息类型：TASK_START / TASK_WARNING / TASK_END / SYSTEM */
    private String type;

    /** 关联业务ID（task_id） */
    private Long relatedId;

    /** 状态：0有效 1失效 */
    private String status;

    /** 是否已读：0未读 1已读 */
    private Integer isRead;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 通知生效时间（任务创建时计算，到达后才对用户可见） */
    private LocalDateTime notifyTime;

    // ======================== getters / setters ========================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Long getRelatedId() { return relatedId; }
    public void setRelatedId(Long relatedId) { this.relatedId = relatedId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getIsRead() { return isRead; }
    public void setIsRead(Integer isRead) { this.isRead = isRead; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getNotifyTime() { return notifyTime; }
    public void setNotifyTime(LocalDateTime notifyTime) { this.notifyTime = notifyTime; }
}
