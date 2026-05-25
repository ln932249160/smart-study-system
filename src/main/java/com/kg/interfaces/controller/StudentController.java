package com.kg.interfaces.controller;

import com.kg.application.service.StudentApplicationService;
import com.kg.interfaces.dto.ClassOptionVO;
import com.kg.interfaces.dto.StudentCreateRequest;
import com.kg.interfaces.dto.StudentPageRequest;
import com.kg.interfaces.dto.StudentUpdateRequest;
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
 * 学生管理控制器 —— 管理 student / headmaster 角色用户。
 */
@Tag(name = "学生管理", description = "学生与班主任的增删改查")
@RestController
public class StudentController {

    private final StudentApplicationService studentApplicationService;

    public StudentController(StudentApplicationService studentApplicationService) {
        this.studentApplicationService = studentApplicationService;
    }

    // ======================== 分页查询 ========================

    /**
     * 分页查询学生/班主任。
     */
    @Operation(summary = "分页查询学生/班主任")
    @PostMapping("/student/page")
    public Map<String, Object> page(@Valid @RequestBody StudentPageRequest request) {
        Map<String, Object> pageResult = studentApplicationService.page(
                request.getName(),
                request.getPageNum(),
                request.getPageSize());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", pageResult);
        return result;
    }

    // ======================== 新增 ========================

    /**
     * 新增学生/班主任。
     */
    @Operation(summary = "新增学生/班主任")
    @PostMapping("/student")
    public Map<String, Object> create(@Valid @RequestBody StudentCreateRequest request) {
        studentApplicationService.create(request);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200);
        result.put("message", "新增成功");
        return result;
    }

    // ======================== 编辑 ========================

    /**
     * 编辑学生/班主任。
     */
    @Operation(summary = "编辑学生/班主任")
    @PutMapping("/student/{id}")
    public Map<String, Object> update(@PathVariable Long id,
                                      @Valid @RequestBody StudentUpdateRequest request) {
        studentApplicationService.update(id, request);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200);
        result.put("message", "编辑成功");
        return result;
    }

    // ======================== 删除 ========================

    /**
     * 物理删除学生/班主任。
     */
    @Operation(summary = "删除学生/班主任")
    @DeleteMapping("/student/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        studentApplicationService.delete(id);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200);
        result.put("message", "删除成功");
        return result;
    }

    // ======================== 班级下拉 ========================

    /**
     * 班级下拉选项查询。
     */
    @Operation(summary = "班级下拉选项")
    @GetMapping("/class/options")
    public Map<String, Object> listClassOptions() {
        List<ClassOptionVO> options = studentApplicationService.listClassOptions();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", options);
        return result;
    }
}
