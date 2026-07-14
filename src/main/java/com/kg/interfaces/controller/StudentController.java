package com.kg.interfaces.controller;

import com.alibaba.excel.EasyExcel;
import com.kg.application.service.StudentApplicationService;
import com.kg.interfaces.dto.ClassOptionVO;
import com.kg.interfaces.dto.StudentCreateRequest;
import com.kg.interfaces.dto.StudentPageRequest;
import com.kg.interfaces.dto.StudentUpdateRequest;
import com.kg.interfaces.dto.UserImportDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器 —— 管理 student / headmaster 角色用户。
 */
@Tag(name = "用户管理", description = "学生与班主任的增删改查及 Excel 导入导出")
@RestController
public class StudentController {

    private final StudentApplicationService studentApplicationService;

    public StudentController(StudentApplicationService studentApplicationService) {
        this.studentApplicationService = studentApplicationService;
    }

    // ======================== 分页查询 ========================

    @Operation(summary = "分页查询用户")
    @PostMapping("/student/page")
    public Map<String, Object> page(@Valid @RequestBody StudentPageRequest request) {
        Map<String, Object> pageResult = studentApplicationService.page(
                request.getName(), request.getRole(), request.getPhone(),
                request.getUserId(), request.getClassId(), request.getClassName(),
                request.getPageNum(), request.getPageSize());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200); result.put("message", "查询成功"); result.put("data", pageResult);
        return result;
    }

    // ======================== 详情 ========================

    @Operation(summary = "用户详情")
    @GetMapping("/student/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", studentApplicationService.getById(id));
        return result;
    }

    // ======================== 新增 ========================

    @Operation(summary = "新增用户")
    @PostMapping("/student")
    public Map<String, Object> create(@Valid @RequestBody StudentCreateRequest request) {
        studentApplicationService.create(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200); result.put("message", "新增成功");
        return result;
    }

    // ======================== 编辑 ========================

    @Operation(summary = "编辑用户")
    @PutMapping("/student/{id}")
    public Map<String, Object> update(@PathVariable Long id,
                                      @Valid @RequestBody StudentUpdateRequest request) {
        studentApplicationService.update(id, request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200); result.put("message", "编辑成功");
        return result;
    }

    // ======================== 重置密码 ========================

    /** 老师将指定用户密码重置为默认密码 123456 */
    @Operation(summary = "重置密码")
    @PutMapping("/user/reset-password/{userId}")
    public Map<String, Object> resetPassword(@PathVariable Long userId) {
        studentApplicationService.resetPassword(userId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200); result.put("message", "密码已重置为123456");
        return result;
    }

    // ======================== 删除 ========================

    @Operation(summary = "删除用户")
    @DeleteMapping("/student/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        studentApplicationService.delete(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200); result.put("message", "删除成功");
        return result;
    }

    // ======================== 班级下拉 ========================

    @Operation(summary = "班级下拉选项")
    @GetMapping("/class/options")
    public Map<String, Object> listClassOptions() {
        List<ClassOptionVO> options = studentApplicationService.listClassOptions();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200); result.put("message", "查询成功"); result.put("data", options);
        return result;
    }

    // ======================== Excel 模板下载 ========================

    /**
     * 下载用户导入模板（Excel）。
     * 模板包含表头行：姓名、角色、性别、手机号、邮箱、描述、班级ID、账号。
     */
    @Operation(summary = "下载用户导入模板")
    @GetMapping("/user/template")
    public ResponseEntity<byte[]> downloadTemplate() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        EasyExcel.write(os, UserImportDTO.class)
                .sheet("用户导入")
                .doWrite(new java.util.ArrayList<>());

        String fileName = URLEncoder.encode("用户导入模板.xlsx", StandardCharsets.UTF_8.name())
                .replaceAll("\\+", "%20");
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + fileName)
                .body(os.toByteArray());
    }

    // ======================== Excel 导入 ========================

    /**
     * 批量导入用户（Excel）。
     * 支持 .xlsx / .xls 格式，模板与下载的模板一致。
     */
    @Operation(summary = "批量导入用户")
    @PostMapping(value = "/user/import", consumes = "multipart/form-data")
    public Map<String, Object> importUsers(@RequestParam("file") MultipartFile file) throws IOException {
        List<UserImportDTO> list = EasyExcel.read(file.getInputStream())
                .head(UserImportDTO.class)
                .sheet()
                .doReadSync();

        Map<String, Object> data = studentApplicationService.importUsers(list);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200);
        result.put("message", "导入完成");
        result.put("data", data);
        return result;
    }
}
