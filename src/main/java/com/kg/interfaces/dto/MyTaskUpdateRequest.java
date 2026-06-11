package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

/** 修改我的任务请求 */
@Schema(description = "修改我的任务请求")
public class MyTaskUpdateRequest {
    @Schema(description = "分配ID(task_user.id)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long taskUserId;
    @Schema(description = "备注") private String remark;
    @Schema(description = "总成绩") private BigDecimal totalScore;
    @Schema(description = "模块成绩明细") private List<ScoreItem> scores;

    @Schema(description = "成绩明细项")
    public static class ScoreItem {
        @Schema(description = "模块名称") private String moduleName;
        @Schema(description = "分数") private BigDecimal score;
        public String getModuleName() { return moduleName; } public void setModuleName(String s) { this.moduleName = s; }
        public BigDecimal getScore() { return score; } public void setScore(BigDecimal s) { this.score = s; }
    }

    public Long getTaskUserId() { return taskUserId; } public void setTaskUserId(Long v) { this.taskUserId = v; }
    public String getRemark() { return remark; } public void setRemark(String v) { this.remark = v; }
    public BigDecimal getTotalScore() { return totalScore; } public void setTotalScore(BigDecimal v) { this.totalScore = v; }
    public List<ScoreItem> getScores() { return scores; } public void setScores(List<ScoreItem> v) { this.scores = v; }
}
