package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "编辑模板请求")
public class TaskTemplateUpdateRequest {
    @NotBlank @Schema(description = "模板名称") private String templateName;
    @NotBlank @Schema(description = "任务类型") private String taskType;
    @NotNull @Schema(description = "是否强制") private Integer isMandatory;
    @Schema(description = "任务描述") private String taskDescription;
    @Schema(description = "默认优先级") private Integer defaultPriority;

    public String getTemplateName() { return templateName; } public void setTemplateName(String s) { this.templateName = s; }
    public String getTaskType() { return taskType; } public void setTaskType(String s) { this.taskType = s; }
    public Integer getIsMandatory() { return isMandatory; } public void setIsMandatory(Integer i) { this.isMandatory = i; }
    public String getTaskDescription() { return taskDescription; } public void setTaskDescription(String s) { this.taskDescription = s; }
    public Integer getDefaultPriority() { return defaultPriority; } public void setDefaultPriority(Integer i) { this.defaultPriority = i; }
}
