package com.kg.domain.model;

import java.time.LocalDateTime;

/**
 * 任务分配领域模型 —— 字段来源于 task_user 表。
 */
public class TaskUser {

    /** 主键ID */
    private Long id;

    /** 任务ID */
    private Long taskId;

    /** 用户ID */
    private Long userId;

    /** 状态：0未完成 1已完成 */
    private String status;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 完成时间 */
    private LocalDateTime finishTime;

    /** 耗时（分钟） */
    private Integer durationMinutes;

    /** 备注 */
    private String remark;

    /** 总成绩 */
    private java.math.BigDecimal totalScore;

    /** 提交时间 */
    private LocalDateTime submitTime;

    /** 创建人 */
    private Long createBy;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 修改人 */
    private Long updateBy;

    /** 修改时间 */
    private LocalDateTime updateTime;

    // ======================== getters / setters ========================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getFinishTime() { return finishTime; }
    public void setFinishTime(LocalDateTime finishTime) { this.finishTime = finishTime; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public java.math.BigDecimal getTotalScore() { return totalScore; }
    public void setTotalScore(java.math.BigDecimal totalScore) { this.totalScore = totalScore; }

    public LocalDateTime getSubmitTime() { return submitTime; }
    public void setSubmitTime(LocalDateTime submitTime) { this.submitTime = submitTime; }

    public Long getCreateBy() { return createBy; }
    public void setCreateBy(Long createBy) { this.createBy = createBy; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public Long getUpdateBy() { return updateBy; }
    public void setUpdateBy(Long updateBy) { this.updateBy = updateBy; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
