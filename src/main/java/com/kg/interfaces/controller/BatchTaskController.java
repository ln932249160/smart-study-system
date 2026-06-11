package com.kg.interfaces.controller;

import com.kg.application.service.BatchTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 批处理任务测试入口。
 */
@Tag(name = "系统管理", description = "字典管理")
@RestController
public class BatchTaskController {

    private final BatchTaskService batchTaskService;

    public BatchTaskController(BatchTaskService batchTaskService) {
        this.batchTaskService = batchTaskService;
    }

    /** 手动触发每日打卡任务 */
    @Operation(summary = "手动触发每日打卡")
    @PostMapping("/batch/daily-checkin")
    public Map<String, Object> triggerCheckIn() {
        batchTaskService.createDailyTaskIfAbsent("每日打卡", "2", 6, 1);
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200); r.put("message", "每日打卡已触发");
        return r;
    }

    /** 手动触发每日复盘任务 */
    @Operation(summary = "手动触发每日复盘")
    @PostMapping("/batch/daily-review")
    public Map<String, Object> triggerReview() {
        batchTaskService.createDailyTaskIfAbsent("每日复盘", "3", 8, 1);
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200); r.put("message", "每日复盘已触发");
        return r;
    }
}
