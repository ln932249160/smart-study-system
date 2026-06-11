package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

/** 我的任务详情 VO */
@Schema(description = "任务详情")
public class MyTaskDetailVO {
    // ---- 任务信息 ----
    @Schema(description = "任务ID") private Long taskId;
    @Schema(description = "任务名称") private String taskName;
    @Schema(description = "任务类型") private String taskType;
    @Schema(description = "任务描述") private String taskDescription;
    @Schema(description = "优先级") private Integer priority;
    @Schema(description = "轮次") private Integer roundNo;
    @Schema(description = "任务开始时间") private String taskStartTime;
    @Schema(description = "任务结束时间") private String taskEndTime;
    // ---- 任务分配信息 ----
    @Schema(description = "分配ID") private Long taskUserId;
    @Schema(description = "状态") private String status;
    @Schema(description = "开始时间") private String startTime;
    @Schema(description = "完成时间") private String finishTime;
    @Schema(description = "耗时(分钟)") private Integer durationMinutes;
    @Schema(description = "备注") private String remark;
    @Schema(description = "总成绩") private BigDecimal totalScore;
    @Schema(description = "提交时间") private String submitTime;
    // ---- 成绩明细 ----
    @Schema(description = "成绩明细") private List<ScoreItem> scores;

    @Schema(description = "成绩明细项")
    public static class ScoreItem {
        @Schema(description = "模块名称") private String moduleName;
        @Schema(description = "分数") private BigDecimal score;
        public String getModuleName() { return moduleName; } public void setModuleName(String s) { this.moduleName = s; }
        public BigDecimal getScore() { return score; } public void setScore(BigDecimal s) { this.score = s; }
    }

    public Long getTaskId() { return taskId; } public void setTaskId(Long v) { this.taskId = v; }
    public String getTaskName() { return taskName; } public void setTaskName(String v) { this.taskName = v; }
    public String getTaskType() { return taskType; } public void setTaskType(String v) { this.taskType = v; }
    public String getTaskDescription() { return taskDescription; } public void setTaskDescription(String v) { this.taskDescription = v; }
    public Integer getPriority() { return priority; } public void setPriority(Integer v) { this.priority = v; }
    public Integer getRoundNo() { return roundNo; } public void setRoundNo(Integer v) { this.roundNo = v; }
    public String getTaskStartTime() { return taskStartTime; } public void setTaskStartTime(String v) { this.taskStartTime = v; }
    public String getTaskEndTime() { return taskEndTime; } public void setTaskEndTime(String v) { this.taskEndTime = v; }
    public Long getTaskUserId() { return taskUserId; } public void setTaskUserId(Long v) { this.taskUserId = v; }
    public String getStatus() { return status; } public void setStatus(String v) { this.status = v; }
    public String getStartTime() { return startTime; } public void setStartTime(String v) { this.startTime = v; }
    public String getFinishTime() { return finishTime; } public void setFinishTime(String v) { this.finishTime = v; }
    public Integer getDurationMinutes() { return durationMinutes; } public void setDurationMinutes(Integer v) { this.durationMinutes = v; }
    public String getRemark() { return remark; } public void setRemark(String v) { this.remark = v; }
    public BigDecimal getTotalScore() { return totalScore; } public void setTotalScore(BigDecimal v) { this.totalScore = v; }
    public String getSubmitTime() { return submitTime; } public void setSubmitTime(String v) { this.submitTime = v; }
    public List<ScoreItem> getScores() { return scores; } public void setScores(List<ScoreItem> v) { this.scores = v; }
}
