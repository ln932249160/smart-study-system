package com.kg.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/** 模板任务持久化实体，映射 task_template 表 */
@TableName("task_template")
public class TaskTemplateEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String templateName;
    private String taskType;
    private Integer isMandatory;
    private String taskDescription;
    private Integer defaultPriority;
    private Long createBy;
    private LocalDateTime createTime;
    private Long updateBy;
    private LocalDateTime updateTime;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getTemplateName() { return templateName; } public void setTemplateName(String s) { this.templateName = s; }
    public String getTaskType() { return taskType; } public void setTaskType(String s) { this.taskType = s; }
    public Integer getIsMandatory() { return isMandatory; } public void setIsMandatory(Integer i) { this.isMandatory = i; }
    public String getTaskDescription() { return taskDescription; } public void setTaskDescription(String s) { this.taskDescription = s; }
    public Integer getDefaultPriority() { return defaultPriority; } public void setDefaultPriority(Integer i) { this.defaultPriority = i; }
    public Long getCreateBy() { return createBy; } public void setCreateBy(Long l) { this.createBy = l; }
    public LocalDateTime getCreateTime() { return createTime; } public void setCreateTime(LocalDateTime t) { this.createTime = t; }
    public Long getUpdateBy() { return updateBy; } public void setUpdateBy(Long l) { this.updateBy = l; }
    public LocalDateTime getUpdateTime() { return updateTime; } public void setUpdateTime(LocalDateTime t) { this.updateTime = t; }
}
