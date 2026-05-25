package com.kg.interfaces.controller;

import com.kg.application.service.ClassApplicationService;
import com.kg.interfaces.dto.ClassCreateRequest;
import com.kg.interfaces.dto.ClassPageRequest;
import com.kg.interfaces.dto.ClassUpdateRequest;
import com.kg.interfaces.dto.StudentOptionVO;
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
 * 班级管理控制器 —— 班级的增删改查及学生分配。
 */
@Tag(name = "班级管理", description = "班级增删改查与学生分配")
@RestController
public class ClassController {

    private final ClassApplicationService classApplicationService;

    public ClassController(ClassApplicationService classApplicationService) {
        this.classApplicationService = classApplicationService;
    }

    // ======================== 分页查询 ========================

    /**
     * 分页查询班级列表，含各班学生人数。
     */
    @Operation(summary = "分页查询班级")
    @PostMapping("/class/page")
    public Map<String, Object> page(@Valid @RequestBody ClassPageRequest request) {
        Map<String, Object> pageResult = classApplicationService.page(
                request.getPageNum(), request.getPageSize());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", pageResult);
        return result;
    }

    // ======================== 新增班级 ========================

    /**
     * 新增班级，可同时选择学生分配到该班级。
     */
    @Operation(summary = "新增班级")
    @PostMapping("/class")
    public Map<String, Object> create(@Valid @RequestBody ClassCreateRequest request) {
        classApplicationService.create(request);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200);
        result.put("message", "新增成功");
        return result;
    }

    // ======================== 编辑班级 ========================

    /**
     * 编辑班级信息，可重新分配学生。
     */
    @Operation(summary = "编辑班级")
    @PutMapping("/class/{id}")
    public Map<String, Object> update(@PathVariable Long id,
                                      @Valid @RequestBody ClassUpdateRequest request) {
        classApplicationService.update(id, request);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200);
        result.put("message", "编辑成功");
        return result;
    }

    // ======================== 删除班级 ========================

    /**
     * 删除班级，该班级下的学生自动变为未分班状态。
     */
    @Operation(summary = "删除班级")
    @DeleteMapping("/class/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        classApplicationService.delete(id);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200);
        result.put("message", "删除成功");
        return result;
    }

    // ======================== 学生选项 ========================

    /**
     * 获取学生复选框选项列表（role=student）。
     * 返回学生 ID、姓名、当前班级 ID。
     */
    @Operation(summary = "学生复选框选项")
    @GetMapping("/class/student-options")
    public Map<String, Object> listStudentOptions() {
        List<StudentOptionVO> options = classApplicationService.listStudentOptions();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", options);
        return result;
    }
}
