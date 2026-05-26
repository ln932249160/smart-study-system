package com.kg.interfaces.controller;

import com.kg.application.service.NotificationApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 通知消息控制器。
 */
@Tag(name = "通知消息", description = "消息列表与已读")
@RestController
public class NotificationController {

    private final NotificationApplicationService notificationApplicationService;

    public NotificationController(NotificationApplicationService notificationApplicationService) {
        this.notificationApplicationService = notificationApplicationService;
    }

    /**
     * 分页查询当前用户的通知消息。
     */
    @Operation(summary = "消息列表")
    @GetMapping("/notification/list")
    public Map<String, Object> list(@RequestParam(defaultValue = "1") int pageNum,
                                    @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> data = notificationApplicationService.page(pageNum, pageSize);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", data);
        return result;
    }

    /**
     * 单条消息标记为已读。
     */
    @Operation(summary = "标记已读")
    @PostMapping("/notification/read")
    public Map<String, Object> markRead(@RequestBody Map<String, Long> body) {
        Long messageId = body.get("messageId");
        notificationApplicationService.markRead(messageId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200);
        result.put("message", "已读");
        return result;
    }
}
