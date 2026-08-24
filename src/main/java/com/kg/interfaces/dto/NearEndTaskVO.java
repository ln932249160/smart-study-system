package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 即将截止任务 VO
 */
@Schema(description = "即将截止任务")
public class NearEndTaskVO {

    @Schema(description = "任务ID")
    private Long taskId;

    @Schema(description = "分配ID") private Long taskUserId;

    @Schema(description = "任务名称")
    private String taskName;

    @Schema(description = "截止时间")
    private String endTime;

    // ======================== getters / setters ========================
    public Long getTaskUserId() { return taskUserId; } public void setTaskUserId(Long v) { this.taskUserId = v; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}
