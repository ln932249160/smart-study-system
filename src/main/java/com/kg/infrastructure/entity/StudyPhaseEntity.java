package com.kg.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** 学习阶段持久化实体，映射 study_phase 表。 */
@TableName("study_phase")
public class StudyPhaseEntity {
    @TableId(type = IdType.AUTO) private Long id;
    private String phaseTitle;
    private String phaseContent;
    private LocalDate startDate;
    private LocalDate endDate;
    private String displayColor;
    private Integer status;
    private Long createBy;
    private LocalDateTime createTime;
    private Long updateBy;
    private LocalDateTime updateTime;

    public Long getId() { return id; } public void setId(Long v) { this.id = v; }
    public String getPhaseTitle() { return phaseTitle; } public void setPhaseTitle(String v) { this.phaseTitle = v; }
    public String getPhaseContent() { return phaseContent; } public void setPhaseContent(String v) { this.phaseContent = v; }
    public LocalDate getStartDate() { return startDate; } public void setStartDate(LocalDate v) { this.startDate = v; }
    public LocalDate getEndDate() { return endDate; } public void setEndDate(LocalDate v) { this.endDate = v; }
    public String getDisplayColor() { return displayColor; } public void setDisplayColor(String v) { this.displayColor = v; }
    public Integer getStatus() { return status; } public void setStatus(Integer v) { this.status = v; }
    public Long getCreateBy() { return createBy; } public void setCreateBy(Long v) { this.createBy = v; }
    public LocalDateTime getCreateTime() { return createTime; } public void setCreateTime(LocalDateTime v) { this.createTime = v; }
    public Long getUpdateBy() { return updateBy; } public void setUpdateBy(Long v) { this.updateBy = v; }
    public LocalDateTime getUpdateTime() { return updateTime; } public void setUpdateTime(LocalDateTime v) { this.updateTime = v; }
}
