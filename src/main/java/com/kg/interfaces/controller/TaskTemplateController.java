package com.kg.interfaces.controller;

import com.kg.interfaces.dto.AjaxResult;
import com.kg.application.service.TaskTemplateApplicationService;
import com.kg.interfaces.dto.TaskTemplateCreateRequest;
import com.kg.interfaces.dto.TaskTemplatePageRequest;
import com.kg.interfaces.dto.TaskTemplateUpdateRequest;
import com.kg.interfaces.dto.TaskTemplateVO;
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

/** 模板任务管理控制器 */
@Tag(name = "模板任务管理", description = "模板任务的增删改查")
@RestController
public class TaskTemplateController {

    private final TaskTemplateApplicationService service;

    public TaskTemplateController(TaskTemplateApplicationService service) { this.service = service; }

    @Operation(summary = "分页查询模板")
    @PostMapping("/task-template/page")
    public Map<String, Object> page(@Valid @RequestBody TaskTemplatePageRequest req) {
        return AjaxResult.success("查询成功", service.page(req.getPageNum(), req.getPageSize()));
    }

    @Operation(summary = "模板详情")
    @GetMapping("/task-template/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        return AjaxResult.success("查询成功", service.getById(id));
    }

    @Operation(summary = "新增模板")
    @PostMapping("/task-template")
    public Map<String, Object> create(@Valid @RequestBody TaskTemplateCreateRequest req) {
        service.create(req);
        return AjaxResult.success("新增成功");
    }

    @Operation(summary = "编辑模板")
    @PutMapping("/task-template/{id}")
    public Map<String, Object> update(@PathVariable Long id, @Valid @RequestBody TaskTemplateUpdateRequest req) {
        service.update(id, req);
        return AjaxResult.success("编辑成功");
    }

    @Operation(summary = "删除模板")
    @DeleteMapping("/task-template/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        service.delete(id);
        return AjaxResult.success("删除成功");
    }

    @Operation(summary = "模板下拉")
    @GetMapping("/task-template/options")
    public Map<String, Object> listOptions() {
        List<TaskTemplateVO> list = service.listOptions();
        return AjaxResult.success("查询成功", list);
    }
}
