package com.kg.interfaces.controller;

import com.kg.application.service.TaskPlanApplicationService;
import com.kg.infrastructure.entity.TaskEntity;
import com.kg.infrastructure.entity.TaskPlanEntity;
import com.kg.interfaces.dto.TaskPlanDetailVO;
import com.kg.interfaces.dto.TaskPlanTaskVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 任务计划控制器 */
@Tag(name = "任务列表", description = "任务的增删改查")
@RestController
public class TaskPlanController {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final TaskPlanApplicationService planService;
    public TaskPlanController(TaskPlanApplicationService planService) { this.planService = planService; }

    @Operation(summary = "任务计划详情")
    @GetMapping("/task-plan/detail/{planId}")
    public Map<String, Object> detail(@PathVariable Long planId) {
        TaskPlanEntity plan = planService.getDetail(planId);
        List<TaskEntity> tasks = planService.getPlanTasks(planId);

        TaskPlanDetailVO vo = new TaskPlanDetailVO();
        vo.setId(plan.getId()); vo.setPlanName(plan.getPlanName());
        vo.setTaskType(plan.getTaskType()); vo.setTemplateId(plan.getTemplateId());
        vo.setRepeatType(plan.getRepeatType()); vo.setRepeatConfig(plan.getRepeatConfig());
        vo.setStartDate(plan.getStartDate() != null ? plan.getStartDate().format(DATE_FMT) : null);
        vo.setEndDate(plan.getEndDate() != null ? plan.getEndDate().format(DATE_FMT) : null);
        vo.setGeneratedCount(plan.getGeneratedCount()); vo.setStatus(plan.getStatus());
        vo.setTasks(tasks.stream().map(t -> {
            TaskPlanTaskVO tv = new TaskPlanTaskVO();
            tv.setId(t.getId()); tv.setTaskName(t.getTaskName());
            tv.setPlanDate(t.getPlanDate() != null ? t.getPlanDate().format(DATE_FMT) : null);
            tv.setTaskStartTime(t.getTaskStartTime() != null ? t.getTaskStartTime().format(FMT) : null);
            tv.setTaskEndTime(t.getTaskEndTime() != null ? t.getTaskEndTime().format(FMT) : null);
            tv.setStatus(t.getStatus());
            return tv;
        }).collect(Collectors.toList()));

        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200); r.put("message", "查询成功"); r.put("data", vo);
        return r;
    }

    @Operation(summary = "停止任务计划")
    @PostMapping("/task-plan/stop/{planId}")
    public Map<String, Object> stop(@PathVariable Long planId) {
        planService.stop(planId);
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200); r.put("message", "已停止");
        return r;
    }
}
