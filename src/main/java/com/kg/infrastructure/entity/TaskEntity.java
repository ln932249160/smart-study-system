package com.kg.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 任务持久化实体，映射 task 表（15 列）。
 */
@TableName("task")
public class TaskEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String taskName;
    private String taskType;
    private Integer isTemplate;
    private Long templateTaskId;
    private Integer roundNo;
    private Integer isMandatory;
    private String taskDescription;
    private LocalDateTime taskStartTime;
    private LocalDateTime taskEndTime;
    private Integer priority;
    private Long classId;
    private Long createBy;
    private LocalDateTime createTime;
    private Long updateBy;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public Integer getIsTemplate() { return isTemplate; }
    public void setIsTemplate(Integer isTemplate) { this.isTemplate = isTemplate; }
    public Long getTemplateTaskId() { return templateTaskId; }
    public void setTemplateTaskId(Long templateTaskId) { this.templateTaskId = templateTaskId; }
    public Integer getRoundNo() { return roundNo; }
    public void setRoundNo(Integer roundNo) { this.roundNo = roundNo; }
    public Integer getIsMandatory() { return isMandatory; }
    public void setIsMandatory(Integer isMandatory) { this.isMandatory = isMandatory; }
    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }
    public LocalDateTime getTaskStartTime() { return taskStartTime; }
    public void setTaskStartTime(LocalDateTime taskStartTime) { this.taskStartTime = taskStartTime; }
    public LocalDateTime getTaskEndTime() { return taskEndTime; }
    public void setTaskEndTime(LocalDateTime taskEndTime) { this.taskEndTime = taskEndTime; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }
    public Long getCreateBy() { return createBy; }
    public void setCreateBy(Long createBy) { this.createBy = createBy; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public Long getUpdateBy() { return updateBy; }
    public void setUpdateBy(Long updateBy) { this.updateBy = updateBy; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
