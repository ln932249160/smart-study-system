package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 完成任务请求 DTO
 */
@Schema(description = "完成任务请求")
public class TaskCompleteRequest {

    @Schema(description = "任务ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long taskId;

    @Schema(description = "耗时（分钟）")
    private Integer durationMinutes;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "总成绩")
    private java.math.BigDecimal totalScore;

    @Schema(description = "成绩明细")
    private List<ScoreItem> scores;

    /**
     * 成绩明细项
     */
    @Schema(description = "成绩明细项")
    public static class ScoreItem {

        @Schema(description = "模块名称")
        private String moduleName;

        @Schema(description = "分数")
        private java.math.BigDecimal score;

        public String getModuleName() { return moduleName; }
        public void setModuleName(String moduleName) { this.moduleName = moduleName; }
        public java.math.BigDecimal getScore() { return score; }
        public void setScore(java.math.BigDecimal score) { this.score = score; }
    }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public java.math.BigDecimal getTotalScore() { return totalScore; }
    public void setTotalScore(java.math.BigDecimal totalScore) { this.totalScore = totalScore; }
    public List<ScoreItem> getScores() { return scores; }
    public void setScores(List<ScoreItem> scores) { this.scores = scores; }
}
