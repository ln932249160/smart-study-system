package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "模板任务")
public class TaskTemplateVO {
    @Schema(description = "ID") private Long id;
    @Schema(description = "模板名称") private String templateName;
    @Schema(description = "任务类型") private String taskType;
    @Schema(description = "是否强制") private Integer isMandatory;
    @Schema(description = "任务描述") private String taskDescription;
    @Schema(description = "默认优先级") private Integer defaultPriority;
    @Schema(description = "创建时间") private String createTime;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getTemplateName() { return templateName; } public void setTemplateName(String s) { this.templateName = s; }
    public String getTaskType() { return taskType; } public void setTaskType(String s) { this.taskType = s; }
    public Integer getIsMandatory() { return isMandatory; } public void setIsMandatory(Integer i) { this.isMandatory = i; }
    public String getTaskDescription() { return taskDescription; } public void setTaskDescription(String s) { this.taskDescription = s; }
    public Integer getDefaultPriority() { return defaultPriority; } public void setDefaultPriority(Integer i) { this.defaultPriority = i; }
    public String getCreateTime() { return createTime; } public void setCreateTime(String s) { this.createTime = s; }
}
