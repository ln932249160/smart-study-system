package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** 学生打卡统计 VO */
@Schema(description = "打卡统计")
public class CheckinStatVO {
    @Schema(description = "连续打卡天数") private int continuousDays;
    @Schema(description = "本月打卡次数") private int monthCheckinCount;
    @Schema(description = "今日是否已打卡") private boolean todayChecked;

    public int getContinuousDays() { return continuousDays; }
    public void setContinuousDays(int continuousDays) { this.continuousDays = continuousDays; }
    public int getMonthCheckinCount() { return monthCheckinCount; }
    public void setMonthCheckinCount(int monthCheckinCount) { this.monthCheckinCount = monthCheckinCount; }
    public boolean isTodayChecked() { return todayChecked; }
    public void setTodayChecked(boolean todayChecked) { this.todayChecked = todayChecked; }
}
