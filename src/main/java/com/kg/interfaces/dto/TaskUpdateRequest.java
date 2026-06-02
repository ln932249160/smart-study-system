package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 编辑任务请求 DTO
 */
@Schema(description = "编辑任务请求")
public class TaskUpdateRequest {

    @Schema(description = "任务名称")
    private String taskName;
    @Schema(description = "任务类型")
    private String taskType;
    @Schema(description = "模板ID")
    private Long templateId;
    @Schema(description = "轮次")
    private Integer roundNo;
    @Schema(description = "是否强制")
    private Integer isMandatory;
    @Schema(description = "任务描述")
    private String taskDescription;
    @Schema(description = "开始时间")
    private String taskStartTime;
    @Schema(description = "结束时间")
    private String taskEndTime;
    @Schema(description = "优先级")
    private Integer priority;
    @Schema(description = "班级ID列表（多选）")
    private List<Long> classIds;
    @Schema(description = "学生ID列表")
    private List<Long> studentIds;

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }
    public Integer getRoundNo() { return roundNo; }
    public void setRoundNo(Integer roundNo) { this.roundNo = roundNo; }
    public Integer getIsMandatory() { return isMandatory; }
    public void setIsMandatory(Integer isMandatory) { this.isMandatory = isMandatory; }
    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }
    public String getTaskStartTime() { return taskStartTime; }
    public void setTaskStartTime(String taskStartTime) { this.taskStartTime = taskStartTime; }
    public String getTaskEndTime() { return taskEndTime; }
    public void setTaskEndTime(String taskEndTime) { this.taskEndTime = taskEndTime; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public List<Long> getClassIds() { return classIds; } public void setClassIds(List<Long> l) { this.classIds = l; }
    public List<Long> getStudentIds() { return studentIds; }
    public void setStudentIds(List<Long> studentIds) { this.studentIds = studentIds; }
}
