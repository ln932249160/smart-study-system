package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 学生首页统计 VO
 */
@Schema(description = "学生首页统计")
public class StudentHomeStatVO {

    /** 待完成任务数 */
    @Schema(description = "待完成任务数")
    private int todoTaskCount;

    /** 已完成任务数 */
    @Schema(description = "已完成任务数")
    private int completedTaskCount;

    /** 即将截止 TOP3 */
    @Schema(description = "即将截止任务")
    private List<NearEndTaskVO> nearEndTasks;

    // ======================== getters / setters ========================

    public int getTodoTaskCount() { return todoTaskCount; }
    public void setTodoTaskCount(int todoTaskCount) { this.todoTaskCount = todoTaskCount; }
    public int getCompletedTaskCount() { return completedTaskCount; }
    public void setCompletedTaskCount(int completedTaskCount) { this.completedTaskCount = completedTaskCount; }
    public List<NearEndTaskVO> getNearEndTasks() { return nearEndTasks; }
    public void setNearEndTasks(List<NearEndTaskVO> nearEndTasks) { this.nearEndTasks = nearEndTasks; }
}
