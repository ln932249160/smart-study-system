package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

/** 我的模板任务详情 VO */
@Schema(description = "模板任务详情")
public class MyTaskTemplateDetailVO {
    @Schema(description = "模板ID") private Long templateId;
    @Schema(description = "模板名称") private String templateName;
    @Schema(description = "任务类型") private String taskType;
    @Schema(description = "任务描述") private String taskDescription;
    @Schema(description = "历史轮次记录") private List<RoundRecord> records;

    @Schema(description = "轮次记录")
    public static class RoundRecord {
        @Schema(description = "任务ID") private Long taskId;
        @Schema(description = "任务名称") private String taskName;
        @Schema(description = "轮次") private Integer roundNo;
        @Schema(description = "开始时间") private String taskStartTime;
        @Schema(description = "结束时间") private String taskEndTime;
        @Schema(description = "状态") private String status;
        @Schema(description = "总成绩") private BigDecimal totalScore;
        @Schema(description = "提交时间") private String submitTime;
        @Schema(description = "成绩明细") private List<ScoreItem> scores;

        public Long getTaskId() { return taskId; } public void setTaskId(Long v) { this.taskId = v; }
        public String getTaskName() { return taskName; } public void setTaskName(String v) { this.taskName = v; }
        public Integer getRoundNo() { return roundNo; } public void setRoundNo(Integer v) { this.roundNo = v; }
        public String getTaskStartTime() { return taskStartTime; } public void setTaskStartTime(String v) { this.taskStartTime = v; }
        public String getTaskEndTime() { return taskEndTime; } public void setTaskEndTime(String v) { this.taskEndTime = v; }
        public String getStatus() { return status; } public void setStatus(String v) { this.status = v; }
        public BigDecimal getTotalScore() { return totalScore; } public void setTotalScore(BigDecimal v) { this.totalScore = v; }
        public String getSubmitTime() { return submitTime; } public void setSubmitTime(String v) { this.submitTime = v; }
        public List<ScoreItem> getScores() { return scores; } public void setScores(List<ScoreItem> v) { this.scores = v; }
    }

    @Schema(description = "成绩明细项")
    public static class ScoreItem {
        @Schema(description = "模块名称") private String moduleName;
        @Schema(description = "分数") private BigDecimal score;
        public String getModuleName() { return moduleName; } public void setModuleName(String s) { this.moduleName = s; }
        public BigDecimal getScore() { return score; } public void setScore(BigDecimal s) { this.score = s; }
    }

    public Long getTemplateId() { return templateId; } public void setTemplateId(Long v) { this.templateId = v; }
    public String getTemplateName() { return templateName; } public void setTemplateName(String v) { this.templateName = v; }
    public String getTaskType() { return taskType; } public void setTaskType(String v) { this.taskType = v; }
    public String getTaskDescription() { return taskDescription; } public void setTaskDescription(String v) { this.taskDescription = v; }
    public List<RoundRecord> getRecords() { return records; } public void setRecords(List<RoundRecord> v) { this.records = v; }
}
