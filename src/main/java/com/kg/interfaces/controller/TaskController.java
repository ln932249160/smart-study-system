package com.kg.interfaces.controller;

import com.kg.application.service.TaskApplicationService;
import com.kg.interfaces.dto.TaskCreateRequest;
import com.kg.interfaces.dto.TaskPageRequest;
import com.kg.interfaces.dto.TaskUpdateRequest;
import com.kg.interfaces.dto.TaskVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务管理控制器。
 */
@Tag(name = "任务管理", description = "任务的增删改查（仅老师）")
@RestController
public class TaskController {

    private final TaskApplicationService taskApplicationService;

    public TaskController(TaskApplicationService taskApplicationService) {
        this.taskApplicationService = taskApplicationService;
    }

    /** 分页查询任务 */
    @Operation(summary = "分页查询任务")
    @PostMapping("/task/page")
    public Map<String, Object> page(@Valid @RequestBody TaskPageRequest req) {
        Map<String, Object> data = taskApplicationService.page(req.getPageNum(), req.getPageSize());
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200); r.put("message", "查询成功"); r.put("data", data);
        return r;
    }

    /** 新增任务 */
    @Operation(summary = "新增任务")
    @PostMapping("/task")
    public Map<String, Object> create(@Valid @RequestBody TaskCreateRequest req) {
        taskApplicationService.create(req);
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200); r.put("message", "新增成功");
        return r;
    }

    /** 编辑任务 */
    @Operation(summary = "编辑任务")
    @PutMapping("/task/{id}")
    public Map<String, Object> update(@PathVariable Long id, @Valid @RequestBody TaskUpdateRequest req) {
        taskApplicationService.update(id, req);
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200); r.put("message", "编辑成功");
        return r;
    }

    /** 删除任务 */
    @Operation(summary = "删除任务")
    @DeleteMapping("/task/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        taskApplicationService.delete(id);
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200); r.put("message", "删除成功");
        return r;
    }

    /** 模板下拉 */
    @Operation(summary = "模板任务下拉")
    @GetMapping("/task/templates")
    public Map<String, Object> listTemplates() {
        List<TaskVO> templates = taskApplicationService.listTemplates();
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200); r.put("message", "查询成功"); r.put("data", templates);
        return r;
    }
}
