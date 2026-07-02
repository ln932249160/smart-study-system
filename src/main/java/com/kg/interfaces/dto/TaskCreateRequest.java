package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import java.util.List;

/** 新增任务请求 */
@Schema(description = "新增任务请求")
public class TaskCreateRequest {
    @NotBlank @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED) private String taskName;
    @NotBlank @Schema(description = "任务类型", requiredMode = Schema.RequiredMode.REQUIRED) private String taskType;
    @Schema(description = "是否使用模板：1是") private Integer useTemplate;
    @Schema(description = "模板ID（useTemplate=1时必填）") private Long templateId;
    @Schema(description = "轮次（普通任务手动填）") private Integer roundNo;
    @Schema(description = "是否强制") private Integer isMandatory;
    @Schema(description = "任务描述") private String taskDescription;
    @Schema(description = "开始时间") private String taskStartTime;
    @Schema(description = "结束时间") private String taskEndTime;
    @Schema(description = "优先级") private Integer priority;
    @Schema(description = "班级ID列表（多选）") private List<Long> classIds;
    @Schema(description = "指定学生ID列表") private List<Long> studentIds;
    // 重复任务参数
    @Schema(description = "创建模式：SINGLE单次 REPEAT重复") private String createMode;
    @Schema(description = "重复类型：DAILY每天 WEEKLY每周") private String repeatType;
    @Schema(description = "重复配置JSON") private String repeatConfig;
    @Schema(description = "计划开始日期") private String startDate;
    @Schema(description = "计划结束日期") private String endDate;

    public String getTaskName() { return taskName; } public void setTaskName(String s) { this.taskName = s; }
    public String getTaskType() { return taskType; } public void setTaskType(String s) { this.taskType = s; }
    public Integer getUseTemplate() { return useTemplate; } public void setUseTemplate(Integer i) { this.useTemplate = i; }
    public Long getTemplateId() { return templateId; } public void setTemplateId(Long l) { this.templateId = l; }
    public Integer getRoundNo() { return roundNo; } public void setRoundNo(Integer i) { this.roundNo = i; }
    public Integer getIsMandatory() { return isMandatory; } public void setIsMandatory(Integer i) { this.isMandatory = i; }
    public String getTaskDescription() { return taskDescription; } public void setTaskDescription(String s) { this.taskDescription = s; }
    public String getTaskStartTime() { return taskStartTime; } public void setTaskStartTime(String s) { this.taskStartTime = s; }
    public String getTaskEndTime() { return taskEndTime; } public void setTaskEndTime(String s) { this.taskEndTime = s; }
    public Integer getPriority() { return priority; } public void setPriority(Integer i) { this.priority = i; }
    public List<Long> getClassIds() { return classIds; } public void setClassIds(List<Long> l) { this.classIds = l; }
    public List<Long> getStudentIds() { return studentIds; } public void setStudentIds(List<Long> l) { this.studentIds = l; }
    public String getCreateMode() { return createMode; } public void setCreateMode(String s) { this.createMode = s; }
    public String getRepeatType() { return repeatType; } public void setRepeatType(String s) { this.repeatType = s; }
    public String getRepeatConfig() { return repeatConfig; } public void setRepeatConfig(String s) { this.repeatConfig = s; }
    public String getStartDate() { return startDate; } public void setStartDate(String s) { this.startDate = s; }
    public String getEndDate() { return endDate; } public void setEndDate(String s) { this.endDate = s; }
}
