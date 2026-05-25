package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 新增任务请求 DTO
 */
@Schema(description = "新增任务请求")
public class TaskCreateRequest {

    @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String taskName;

    @Schema(description = "任务类型：0每日复盘 1打卡 2学习 3刷题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String taskType;

    @Schema(description = "是否模板：1是 0否", example = "0")
    private Integer isTemplate;

    @Schema(description = "模板任务ID（当选择模板时从模板任务中选择）")
    private Long templateTaskId;

    @Schema(description = "轮次，模板为0", example = "1")
    private Integer roundNo;

    @Schema(description = "是否强制：1强制 2不强制", example = "1")
    private Integer isMandatory;

    @Schema(description = "任务描述")
    private String taskDescription;

    @Schema(description = "开始时间", example = "2026-06-01 08:00:00")
    private String taskStartTime;

    @Schema(description = "结束时间", example = "2026-06-07 23:59:59")
    private String taskEndTime;

    @Schema(description = "优先级")
    private Integer priority;

    @Schema(description = "班级ID（与 studentIds 二选一）")
    private Long classId;

    @Schema(description = "学生ID列表（与 classId 二选一）")
    private List<Long> studentIds;

    // ======================== getters / setters ========================

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
    public String getTaskStartTime() { return taskStartTime; }
    public void setTaskStartTime(String taskStartTime) { this.taskStartTime = taskStartTime; }
    public String getTaskEndTime() { return taskEndTime; }
    public void setTaskEndTime(String taskEndTime) { this.taskEndTime = taskEndTime; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }
    public List<Long> getStudentIds() { return studentIds; }
    public void setStudentIds(List<Long> studentIds) { this.studentIds = studentIds; }
}
