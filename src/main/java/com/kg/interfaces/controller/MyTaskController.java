package com.kg.interfaces.controller;

import com.kg.application.service.MyTaskApplicationService;
import com.kg.interfaces.dto.MyTaskCompleteRequest;
import com.kg.interfaces.dto.MyTaskPageRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 我的任务控制器 —— 个人任务列表 + 完成任务。
 */
@Tag(name = "我的任务", description = "个人任务列表与完成")
@RestController
public class MyTaskController {

    private final MyTaskApplicationService myTaskApplicationService;

    public MyTaskController(MyTaskApplicationService myTaskApplicationService) {
        this.myTaskApplicationService = myTaskApplicationService;
    }

    /**
     * 分页查询当前用户的任务列表。
     * status: 0未完成 / 1已完成 / 不传查全部。
     * 排序：强制任务优先 → 创建时间倒序。
     */
    @Operation(summary = "我的任务列表")
    @GetMapping("/my-task/list")
    public Map<String, Object> list(@Valid MyTaskPageRequest request) {
        Map<String, Object> data = myTaskApplicationService.page(
                request.getStatus(), request.getPageNum(), request.getPageSize());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", data);
        return result;
    }

    /**
     * 当前用户完成任务，含成绩明细。
     */
    @Operation(summary = "完成任务")
    @PostMapping("/my-task/complete")
    public Map<String, Object> complete(@Valid @RequestBody MyTaskCompleteRequest request) {
        myTaskApplicationService.complete(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200);
        result.put("message", "提交成功");
        return result;
    }
}
