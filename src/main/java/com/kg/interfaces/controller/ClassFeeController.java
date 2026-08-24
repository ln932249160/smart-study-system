package com.kg.interfaces.controller;

import com.kg.interfaces.dto.AjaxResult;
import com.kg.application.service.ClassFeeApplicationService;
import com.kg.interfaces.dto.ClassFeeRequest;
import com.kg.interfaces.dto.ClassFeeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 班费管理控制器 */
@Tag(name = "班费管理", description = "班级费用增删改查及统计")
@RestController
public class ClassFeeController {
    private final ClassFeeApplicationService service;
    public ClassFeeController(ClassFeeApplicationService service) { this.service = service; }

    @Operation(summary = "班费分页查询（支持按班级ID和物料模糊筛选）")
    @GetMapping("/class-fee/page")
    public Map<String, Object> page(@RequestParam(defaultValue = "1") int pageNum,
                                     @RequestParam(defaultValue = "10") int pageSize,
                                     @RequestParam(required = false) Long classId,
                                     @RequestParam(required = false) String material) {
        return AjaxResult.success("查询成功", service.page(pageNum, pageSize, classId, material));
    }

    @Operation(summary = "班费详情")
    @GetMapping("/class-fee/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        return AjaxResult.success("查询成功", service.getById(id));
    }

    @Operation(summary = "新增班费")
    @PostMapping("/class-fee")
    public Map<String, Object> create(@Valid @RequestBody ClassFeeRequest req) {
        service.create(req);
        return AjaxResult.success("新增成功");
    }

    @Operation(summary = "编辑班费")
    @PutMapping("/class-fee/{id}")
    public Map<String, Object> update(@PathVariable Long id, @Valid @RequestBody ClassFeeRequest req) {
        service.update(id, req);
        return AjaxResult.success("编辑成功");
    }

    @Operation(summary = "删除班费")
    @DeleteMapping("/class-fee/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        service.delete(id);
        return AjaxResult.success("删除成功");
    }

    @Operation(summary = "班费统计（按班级汇总）")
    @GetMapping("/class-fee/statistics")
    public Map<String, Object> statistics() {
        return AjaxResult.success("查询成功", service.statistics());
    }

    @Operation(summary = "班级费用明细")
    @GetMapping("/class-fee/detail/{classId}")
    public Map<String, Object> detail(@PathVariable Long classId) {
        return AjaxResult.success("查询成功", service.detail(classId));
    }
}
