package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/** 通知消息分页查询请求 */
@Schema(description = "通知消息分页查询请求")
public class NotificationPageRequest {
    @NotNull @Min(1) @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED) private Integer pageNum;
    @NotNull @Min(1) @Schema(description = "每页条数", requiredMode = Schema.RequiredMode.REQUIRED) private Integer pageSize;
    @Schema(description = "标题（模糊）") private String title;
    @Schema(description = "消息类型：TASK_START / TASK_DEADLINE") private String type;
    @Schema(description = "是否已读：0未读 1已读") private Integer isRead;
    @Schema(description = "优先级") private Integer priority;
    @Schema(description = "关联任务ID") private Long relatedId;
    @Schema(description = "通知时间起") private String notifyTimeBegin;
    @Schema(description = "通知时间止") private String notifyTimeEnd;

    public Integer getPageNum() { return pageNum; } public void setPageNum(Integer i) { this.pageNum = i; }
    public Integer getPageSize() { return pageSize; } public void setPageSize(Integer i) { this.pageSize = i; }
    public String getTitle() { return title; } public void setTitle(String s) { this.title = s; }
    public String getType() { return type; } public void setType(String s) { this.type = s; }
    public Integer getIsRead() { return isRead; } public void setIsRead(Integer i) { this.isRead = i; }
    public Integer getPriority() { return priority; } public void setPriority(Integer i) { this.priority = i; }
    public Long getRelatedId() { return relatedId; } public void setRelatedId(Long l) { this.relatedId = l; }
    public String getNotifyTimeBegin() { return notifyTimeBegin; } public void setNotifyTimeBegin(String s) { this.notifyTimeBegin = s; }
    public String getNotifyTimeEnd() { return notifyTimeEnd; } public void setNotifyTimeEnd(String s) { this.notifyTimeEnd = s; }
}
