package com.kg.interfaces.controller;

import com.kg.application.service.StudyPhaseApplicationService;
import com.kg.interfaces.dto.StudyPhaseRequest;
import com.kg.interfaces.dto.StudyPhaseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 学习阶段控制器 */
@Tag(name = "学习阶段", description = "学习阶段增删改查及日历")
@RestController
public class StudyPhaseController {
    private final StudyPhaseApplicationService service;
    public StudyPhaseController(StudyPhaseApplicationService service) { this.service = service; }

    @Operation(summary = "新增学习阶段")
    @PostMapping("/study-phase/add")
    public Map<String, Object> add(@Valid @RequestBody StudyPhaseRequest req) {
        service.add(req);
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200); r.put("message", "新增成功");
        return r;
    }

    @Operation(summary = "编辑学习阶段")
    @PutMapping("/study-phase/update")
    public Map<String, Object> update(@Valid @RequestBody StudyPhaseRequest req) {
        service.update(req);
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200); r.put("message", "编辑成功");
        return r;
    }

    @Operation(summary = "删除学习阶段（逻辑删除）")
    @DeleteMapping("/study-phase/delete/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        service.delete(id);
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200); r.put("message", "删除成功");
        return r;
    }

    @Operation(summary = "学习阶段详情")
    @GetMapping("/study-phase/detail/{id}")
    public Map<String, Object> detail(@PathVariable Long id) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200); r.put("message", "查询成功"); r.put("data", service.detail(id));
        return r;
    }

    @Operation(summary = "学习阶段分页列表")
    @GetMapping("/study-phase/page")
    public Map<String, Object> page(@RequestParam(defaultValue = "1") int pageNum,
                                     @RequestParam(defaultValue = "10") int pageSize,
                                     @RequestParam(required = false) String phaseTitle,
                                     @RequestParam(required = false) Long classId,
                                     @RequestParam(required = false) String startDate,
                                     @RequestParam(required = false) String endDate,
                                     @RequestParam(required = false) Integer status) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200); r.put("message", "查询成功");
        r.put("data", service.page(phaseTitle, classId, startDate, endDate, status, pageNum, pageSize));
        return r;
    }

    @Operation(summary = "学习阶段日历查询")
    @GetMapping("/study-phase/calendar")
    public Map<String, Object> calendar(@RequestParam String startDate,
                                         @RequestParam String endDate,
                                         @RequestParam(required = false) Long classId) {
        List<StudyPhaseVO> data = service.calendar(startDate, endDate, classId);
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200); r.put("message", "查询成功"); r.put("data", data);
        return r;
    }
}
