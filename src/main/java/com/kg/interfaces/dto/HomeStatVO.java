package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 首页统计 VO（teacher / headmaster）
 */
@Schema(description = "首页统计")
public class HomeStatVO {

    /** 当前进行中任务数 */
    @Schema(description = "进行中任务数")
    private int currentTaskCount;

    /** 强制任务数 */
    @Schema(description = "强制任务数")
    private int forceTaskCount;

    /** 总任务数 */
    @Schema(description = "总任务数")
    private int totalTaskCount;

    /** 已完成任务分配数（task_user 维度） */
    @Schema(description = "已完成人数")
    private int completedTaskUserCount;

    /** 未完成任务分配数（task_user 维度） */
    @Schema(description = "未完成人数")
    private int unCompletedTaskUserCount;

    /** 完成率（0-100） */
    @Schema(description = "完成率")
    private int completedRate;

    /** 完成率最低 TOP3 */
    @Schema(description = "完成率最低TOP3任务")
    private List<LowCompletedTaskVO> lowCompletedTasks;

    // ======================== getters / setters ========================

    public int getCurrentTaskCount() { return currentTaskCount; }
    public void setCurrentTaskCount(int currentTaskCount) { this.currentTaskCount = currentTaskCount; }
    public int getForceTaskCount() { return forceTaskCount; }
    public void setForceTaskCount(int forceTaskCount) { this.forceTaskCount = forceTaskCount; }
    public int getTotalTaskCount() { return totalTaskCount; }
    public void setTotalTaskCount(int totalTaskCount) { this.totalTaskCount = totalTaskCount; }
    public int getCompletedTaskUserCount() { return completedTaskUserCount; }
    public void setCompletedTaskUserCount(int completedTaskUserCount) { this.completedTaskUserCount = completedTaskUserCount; }
    public int getUnCompletedTaskUserCount() { return unCompletedTaskUserCount; }
    public void setUnCompletedTaskUserCount(int unCompletedTaskUserCount) { this.unCompletedTaskUserCount = unCompletedTaskUserCount; }
    public int getCompletedRate() { return completedRate; }
    public void setCompletedRate(int completedRate) { this.completedRate = completedRate; }
    public List<LowCompletedTaskVO> getLowCompletedTasks() { return lowCompletedTasks; }
    public void setLowCompletedTasks(List<LowCompletedTaskVO> lowCompletedTasks) { this.lowCompletedTasks = lowCompletedTasks; }
}
