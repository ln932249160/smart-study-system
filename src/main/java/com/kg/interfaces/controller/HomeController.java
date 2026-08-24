package com.kg.interfaces.controller;

import com.kg.interfaces.dto.AjaxResult;
import com.kg.application.service.HomeApplicationService;
import com.kg.interfaces.dto.HomeStatVO;
import com.kg.interfaces.dto.StudentHomeStatVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 首页统计控制器。
 */
@Tag(name = "首页统计", description = "teacher/headmaster/student 首页数据")
@RestController
public class HomeController {

    private final HomeApplicationService homeApplicationService;

    public HomeController(HomeApplicationService homeApplicationService) {
        this.homeApplicationService = homeApplicationService;
    }

    /**
     * 老师/班长首页统计。
     */
    @Operation(summary = "老师/班长首页统计")
    @GetMapping("/home/stat")
    public Map<String, Object> getStats() {
        HomeStatVO data = homeApplicationService.getStats();
        return AjaxResult.success("查询成功", data);
    }

    /**
     * 学生首页统计。
     */
    @Operation(summary = "学生首页统计")
    @GetMapping("/home/student/stat")
    public Map<String, Object> getStudentStats() {
        StudentHomeStatVO data = homeApplicationService.getStudentStats();
        return AjaxResult.success("查询成功", data);
    }
}
