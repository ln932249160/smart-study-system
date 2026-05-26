package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 完成率最低任务 VO
 */
@Schema(description = "完成率最低任务")
public class LowCompletedTaskVO {

    @Schema(description = "任务ID")
    private Long taskId;

    @Schema(description = "任务名称")
    private String taskName;

    @Schema(description = "分配总人数")
    private int totalUserCount;

    @Schema(description = "已完成人数")
    private int completedUserCount;

    @Schema(description = "未完成人数")
    private int unCompletedUserCount;

    @Schema(description = "完成率")
    private int completedRate;

    @Schema(description = "结束时间")
    private String endTime;

    // ======================== getters / setters ========================

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public int getTotalUserCount() { return totalUserCount; }
    public void setTotalUserCount(int totalUserCount) { this.totalUserCount = totalUserCount; }
    public int getCompletedUserCount() { return completedUserCount; }
    public void setCompletedUserCount(int completedUserCount) { this.completedUserCount = completedUserCount; }
    public int getUnCompletedUserCount() { return unCompletedUserCount; }
    public void setUnCompletedUserCount(int unCompletedUserCount) { this.unCompletedUserCount = unCompletedUserCount; }
    public int getCompletedRate() { return completedRate; }
    public void setCompletedRate(int completedRate) { this.completedRate = completedRate; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}
