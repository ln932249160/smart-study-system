package com.kg.interfaces.controller;

import com.kg.interfaces.dto.AjaxResult;
import com.kg.application.service.BatchTaskService;
import com.kg.enums.TaskTypeEnum;
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
        batchTaskService.createDailyTaskIfAbsent("每日打卡", TaskTypeEnum.CHECK_IN.getCode(), 6, 1);
        return AjaxResult.success("每日打卡已触发");
    }

    /** 手动触发每日复盘任务 */
    @Operation(summary = "手动触发每日复盘")
    @PostMapping("/batch/daily-review")
    public Map<String, Object> triggerReview() {
        batchTaskService.createDailyTaskIfAbsent("每日复盘", TaskTypeEnum.DAILY_REVIEW.getCode(), 8, 1);
        return AjaxResult.success("每日复盘已触发");
    }
}
