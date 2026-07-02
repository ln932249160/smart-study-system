package com.kg.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/** 学习阶段班级关联实体，映射 study_phase_class 表。 */
@TableName("study_phase_class")
public class StudyPhaseClassEntity {
    @TableId(type = IdType.AUTO) private Long id;
    private Long phaseId;
    private Long classId;
    private LocalDateTime createTime;

    public Long getId() { return id; } public void setId(Long v) { this.id = v; }
    public Long getPhaseId() { return phaseId; } public void setPhaseId(Long v) { this.phaseId = v; }
    public Long getClassId() { return classId; } public void setClassId(Long v) { this.classId = v; }
    public LocalDateTime getCreateTime() { return createTime; } public void setCreateTime(LocalDateTime v) { this.createTime = v; }
}
