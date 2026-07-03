package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 通知消息列表项 VO
 */
@Schema(description = "通知消息")
public class NotificationVO {

    @Schema(description = "消息ID") private Long id;
    @Schema(description = "标题") private String title;
    @Schema(description = "内容") private String content;
    @Schema(description = "类型") private String type;
    @Schema(description = "关联任务ID") private Long relatedId;
    @Schema(description = "状态：0有效 1失效") private String status;
    @Schema(description = "是否已读") private Integer isRead;
    @Schema(description = "优先级") private Integer priority;
    @Schema(description = "创建时间") private String createdAt;
    @Schema(description = "通知生效时间") private String notifyTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Long getRelatedId() { return relatedId; }
    public void setRelatedId(Long relatedId) { this.relatedId = relatedId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getIsRead() { return isRead; }
    public void setIsRead(Integer isRead) { this.isRead = isRead; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getNotifyTime() { return notifyTime; }
    public void setNotifyTime(String notifyTime) { this.notifyTime = notifyTime; }
}
