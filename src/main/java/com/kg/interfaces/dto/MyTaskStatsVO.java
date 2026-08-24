package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** 我的任务统计 VO */
@Schema(description = "我的任务统计")
public class MyTaskStatsVO {
    @Schema(description = "待办数") private long unfinished;
    @Schema(description = "已办数") private long finished;

    public long getUnfinished() { return unfinished; } public void setUnfinished(long v) { this.unfinished = v; }
    public long getFinished() { return finished; } public void setFinished(long v) { this.finished = v; }
}
