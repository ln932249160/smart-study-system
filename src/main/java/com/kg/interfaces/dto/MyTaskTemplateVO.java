package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/** 我的模板任务列表项 VO */
@Schema(description = "我的模板任务")
public class MyTaskTemplateVO {
    @Schema(description = "模板ID") private Long templateId;
    @Schema(description = "模板名称") private String templateName;
    @Schema(description = "任务类型") private String taskType;
    @Schema(description = "训练次数(轮次)") private int roundCount;
    @Schema(description = "最高分") private BigDecimal maxScore;
    @Schema(description = "最低分") private BigDecimal minScore;
    @Schema(description = "平均分") private BigDecimal avgScore;
    @Schema(description = "最新成绩") private BigDecimal latestScore;

    public Long getTemplateId() { return templateId; } public void setTemplateId(Long v) { this.templateId = v; }
    public String getTemplateName() { return templateName; } public void setTemplateName(String v) { this.templateName = v; }
    public String getTaskType() { return taskType; } public void setTaskType(String v) { this.taskType = v; }
    public int getRoundCount() { return roundCount; } public void setRoundCount(int v) { this.roundCount = v; }
    public BigDecimal getMaxScore() { return maxScore; } public void setMaxScore(BigDecimal v) { this.maxScore = v; }
    public BigDecimal getMinScore() { return minScore; } public void setMinScore(BigDecimal v) { this.minScore = v; }
    public BigDecimal getAvgScore() { return avgScore; } public void setAvgScore(BigDecimal v) { this.avgScore = v; }
    public BigDecimal getLatestScore() { return latestScore; } public void setLatestScore(BigDecimal v) { this.latestScore = v; }
}
