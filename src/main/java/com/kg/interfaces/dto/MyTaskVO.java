package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 我的任务列表项 VO
 */
@Schema(description = "我的任务")
public class MyTaskVO {

    /** 任务ID */
    @Schema(description = "任务ID")
    private Long taskId;

    /** 任务名称 */
    @Schema(description = "任务名称")
    private String taskName;

    /** 任务类型：0每日复盘 1打卡 2学习 3刷题 */
    @Schema(description = "任务类型")
    private String taskType;

    /** 是否强制：1强制 2不强制 */
    @Schema(description = "是否强制")
    private Integer forceFlag;

    @Schema(description = "任务描述")
    private String taskDescription;

    @Schema(description = "优先级")
    private Integer priority;

    /** 任务开始时间 */
    @Schema(description = "开始时间")
    private String startTime;

    /** 任务结束时间 */
    @Schema(description = "结束时间")
    private String endTime;

    /** 完成状态：0未完成 1已完成 */
    @Schema(description = "完成状态")
    private String status;

    /** 总成绩 */
    @Schema(description = "总成绩")
    private java.math.BigDecimal totalScore;

    /** 提交时间 */
    @Schema(description = "提交时间")
    private String submitTime;


    @Schema(description = "轮次")
    private Integer roundNo;


    @Schema(description = "模板ID（null=普通任务）")
    private Long templateId;

    @Schema(description = "计划ID（重复任务）")
    private Long planId;
    @Schema(description = "计划日期（重复任务）")
    private String planDate;
    @Schema(description = "是否重复任务：1是 0否")
    private Integer isRepeatTask;
    @Schema(description = "任务启用状态：1正常 0禁用")
    private Integer taskStatus;

    @Schema(description = "分配ID") private Long taskUserId;

    @Schema(description = "任务创建人") private String taskCreateName;

    public String getTaskCreateName() {
        return taskCreateName;
    }

    public void setTaskCreateName(String taskCreateName) {
        this.taskCreateName = taskCreateName;
    }

    public Integer getRoundNo() {
        return roundNo;
    }

    public void setRoundNo(Integer roundNo) {
        this.roundNo = roundNo;
    }


    // ======================== getters / setters ========================
    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public Integer getForceFlag() { return forceFlag; }
    public void setForceFlag(Integer forceFlag) { this.forceFlag = forceFlag; }
    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public java.math.BigDecimal getTotalScore() { return totalScore; }
    public void setTotalScore(java.math.BigDecimal totalScore) { this.totalScore = totalScore; }
    public String getSubmitTime() { return submitTime; }
    public void setSubmitTime(String submitTime) { this.submitTime = submitTime; }

    public Long getTaskUserId() { return taskUserId; } public void setTaskUserId(Long v) { this.taskUserId = v; }
    public Long getPlanId() { return planId; } public void setPlanId(Long v) { this.planId = v; }
    public String getPlanDate() { return planDate; } public void setPlanDate(String v) { this.planDate = v; }
    public Integer getIsRepeatTask() { return isRepeatTask; } public void setIsRepeatTask(Integer v) { this.isRepeatTask = v; }
    public Integer getTaskStatus() { return taskStatus; } public void setTaskStatus(Integer v) { this.taskStatus = v; }
}
