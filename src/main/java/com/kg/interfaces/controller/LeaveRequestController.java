package com.kg.interfaces.controller;

import com.kg.application.service.LeaveRequestApplicationService;
import com.kg.interfaces.dto.LeaveApproveRequest;
import com.kg.interfaces.dto.LeaveCreateRequest;
import com.kg.interfaces.dto.LeavePageRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 请假申请控制器 —— 学生请假、老师/班长审批。
 */
@Tag(name = "请假管理", description = "学生请假申请与审批")
@RestController
public class LeaveRequestController {

    private final LeaveRequestApplicationService service;

    public LeaveRequestController(LeaveRequestApplicationService service) { this.service = service; }

    // ======================== 学生：请假申请 ========================

    /**
     * 学生新增请假申请。自动计算请假天数、确定审批人、生成通知消息。
     */
    @Operation(summary = "新增请假申请")
    @PostMapping("/leave")
    public Map<String, Object> create(@Valid @RequestBody LeaveCreateRequest req) {
        service.create(req);
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200); r.put("message", "申请成功");
        return r;
    }

    /**
     * 学生分页查询自己的请假申请。支持按请假类型、开始日期范围筛选。
     */
    @Operation(summary = "我的请假列表")
    @PostMapping("/leave/page")
    public Map<String, Object> pageMine(@Valid @RequestBody LeavePageRequest req) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200); r.put("message", "查询成功");
        r.put("data", service.pageMine(req));
        return r;
    }

    // ======================== 审批列表 ========================

    /**
     * 审批列表。老师查审批角色为自己的全部请假，班长查审批人为自己的请假。
     * tab=pending 查待办(status=0)，tab=done 查已办(status=1,2)。
     */
    @Operation(summary = "审批列表（待办/已办）")
    @GetMapping("/leave/approve/list")
    public Map<String, Object> approveList(@RequestParam(defaultValue = "pending") String tab,
                                           @RequestParam(defaultValue = "1") int pageNum,
                                           @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200); r.put("message", "查询成功");
        r.put("data", service.pageApprove(tab, pageNum, pageSize));
        return r;
    }

    // ======================== 审批操作 ========================

    /**
     * 批量审批通过/拒绝。带 WHERE status=0 条件防并发，老师审批后使相关通知失效。
     */
    @Operation(summary = "批量审批")
    @PostMapping("/leave/approve")
    public Map<String, Object> approve(@Valid @RequestBody LeaveApproveRequest req) {
        service.approve(req);
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200); r.put("message", "操作成功");
        return r;
    }
}
