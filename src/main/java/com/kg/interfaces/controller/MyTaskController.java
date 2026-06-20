package com.kg.interfaces.controller;

import com.kg.application.service.MyTaskApplicationService;
import com.kg.interfaces.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 我的任务控制器 —— 个人任务列表 + 详情 + 完成 + 修改 + 模板统计。
 */
@Tag(name = "我的任务", description = "个人任务列表、详情、完成、模板统计")
@RestController
public class MyTaskController {

    private final MyTaskApplicationService myTaskApplicationService;

    public MyTaskController(MyTaskApplicationService myTaskApplicationService) {
        this.myTaskApplicationService = myTaskApplicationService;
    }

    // ======================== 任务列表 ========================

    /** 分页查询，支持多条件筛选（新增 finishTimeStart/finishTimeEnd） */
    @Operation(summary = "我的任务列表")
    @PostMapping("/my-task/list")
    public Map<String, Object> list(@Valid @RequestBody MyTaskPageRequest request) {
        Map<String, Object> data = myTaskApplicationService.page(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200); result.put("message", "查询成功"); result.put("data", data);
        return result;
    }

    // ======================== 任务详情 ========================

    /** 查询任务完整信息（task + task_user + task_score）。老师可传 userId 查看学生详情。 */
    @Operation(summary = "任务详情")
    @GetMapping("/my-task/detail")
    public Map<String, Object> detail(@RequestParam Long taskUserId,
                                       @RequestParam(required = false) Long userId) {
        MyTaskDetailVO data = myTaskApplicationService.getDetail(taskUserId, userId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200); result.put("message", "查询成功"); result.put("data", data);
        return result;
    }

    // ======================== 待办/已办统计 ========================

    @Operation(summary = "待办已办统计")
    @PostMapping("/my-task/stats")
    public Map<String, Object> stats(@RequestBody MyTaskPageRequest request) {
        Map<String, Object> data = myTaskApplicationService.stats(request.getUserId());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200); result.put("message", "查询成功"); result.put("data", data);
        return result;
    }

    // ======================== 一键打卡 ========================

    @Operation(summary = "一键打卡（自动完成今日所有待办打卡）")
    @PostMapping("/my-task/checkin")
    public Map<String, Object> checkin() {
        int count = myTaskApplicationService.checkin();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200); result.put("message", "打卡成功"); result.put("data", count);
        return result;
    }

    // ======================== 完成任务 ========================

    @Operation(summary = "完成任务")
    @PostMapping("/my-task/complete")
    public Map<String, Object> complete(@Valid @RequestBody MyTaskCompleteRequest request) {
        myTaskApplicationService.complete(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200); result.put("message", "提交成功");
        return result;
    }

    // ======================== 修改已完成任务 ========================

    /** 仅允许修改 remark / totalScore / 模块成绩 */
    @Operation(summary = "修改已完成任务")
    @PutMapping("/my-task")
    public Map<String, Object> update(@Valid @RequestBody MyTaskUpdateRequest request) {
        myTaskApplicationService.updateMyTask(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200); result.put("message", "修改成功");
        return result;
    }

    // ======================== 我的模板任务 ========================

    /** 分页查询用户做过的模板任务训练记录（汇总统计） */
    @Operation(summary = "我的模板任务列表")
    @PostMapping("/my-task/template/page")
    public Map<String, Object> templatePage(@Valid @RequestBody MyTaskTemplatePageRequest req) {
        Map<String, Object> data = myTaskApplicationService.pageTemplates(req);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200); result.put("message", "查询成功"); result.put("data", data);
        return result;
    }

    /** 模板任务详情：模板信息 + 各轮次记录 + 成绩明细 */
    @Operation(summary = "模板任务详情")
    @GetMapping("/my-task/template/detail")
    public Map<String, Object> templateDetail(@RequestParam Long templateId,
                                               @RequestParam(required = false) Long userId) {
        MyTaskTemplateDetailVO data = myTaskApplicationService.getTemplateDetail(templateId, userId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200); result.put("message", "查询成功"); result.put("data", data);
        return result;
    }
}
