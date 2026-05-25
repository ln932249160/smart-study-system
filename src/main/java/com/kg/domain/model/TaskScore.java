package com.kg.domain.model;

import java.time.LocalDateTime;

/**
 * 任务成绩明细领域模型 —— 字段来源于 task_score 表。
 */
public class TaskScore {

    /** 主键ID */
    private Long id;

    /** 任务分配ID */
    private Long taskUserId;

    /** 模块名称 */
    private String moduleName;

    /** 分数 */
    private java.math.BigDecimal score;

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

    public Long getTaskUserId() { return taskUserId; }
    public void setTaskUserId(Long taskUserId) { this.taskUserId = taskUserId; }

    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }

    public java.math.BigDecimal getScore() { return score; }
    public void setScore(java.math.BigDecimal score) { this.score = score; }

    public Long getCreateBy() { return createBy; }
    public void setCreateBy(Long createBy) { this.createBy = createBy; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public Long getUpdateBy() { return updateBy; }
    public void setUpdateBy(Long updateBy) { this.updateBy = updateBy; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
