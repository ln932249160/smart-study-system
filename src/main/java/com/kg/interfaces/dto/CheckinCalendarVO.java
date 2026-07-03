package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/** 打卡日历 VO */
@Schema(description = "打卡日历")
public class CheckinCalendarVO {
    @Schema(description = "已打卡日期列表") private List<String> dates;

    public List<String> getDates() { return dates; }
    public void setDates(List<String> dates) { this.dates = dates; }
}
