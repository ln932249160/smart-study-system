package com.kg.interfaces.controller;

import com.kg.interfaces.dto.AjaxResult;
import com.kg.application.service.StudentCheckinApplicationService;
import com.kg.interfaces.dto.CheckinCalendarVO;
import com.kg.interfaces.dto.CheckinStatVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 学生打卡统计控制器。
 */
@Tag(name = "打卡统计", description = "学生打卡统计与日历")
@RestController
public class StudentCheckinController {

    private final StudentCheckinApplicationService service;

    public StudentCheckinController(StudentCheckinApplicationService service) {
        this.service = service;
    }

    /** 打卡统计：连续天数 + 本月次数 + 今日是否已打卡 */
    @Operation(summary = "打卡统计")
    @GetMapping("/student/checkin/stat")
    public Map<String, Object> stat() {
        CheckinStatVO data = service.getStat();
        return AjaxResult.success("查询成功", data);
    }

    /** 打卡日历：指定月份已打卡日期列表 */
    @Operation(summary = "打卡日历")
    @GetMapping("/student/checkin/calendar")
    public Map<String, Object> calendar(@RequestParam String month) {
        CheckinCalendarVO data = service.getCalendar(month);
        return AjaxResult.success("查询成功", data);
    }
}
