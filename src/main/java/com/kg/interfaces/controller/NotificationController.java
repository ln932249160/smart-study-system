package com.kg.interfaces.controller;

import com.kg.interfaces.dto.AjaxResult;
import com.kg.interfaces.dto.PageVO;
import com.kg.interfaces.dto.NotificationVO;
import com.kg.application.service.NotificationApplicationService;
import com.kg.interfaces.dto.NotificationPageRequest;
import com.kg.interfaces.dto.PageVO;
import com.kg.interfaces.dto.NotificationVO;
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
     * 分页查询当前用户的通知消息，支持多条件筛选。
     */
    @Operation(summary = "消息列表")
    @PostMapping("/notification/list")
    public Map<String, Object> list(@Valid @RequestBody NotificationPageRequest req) {
        PageVO<NotificationVO> data = notificationApplicationService.page(req);
        return AjaxResult.success("查询成功", data);
    }

    /**
     * 单条消息标记为已读。
     */
    @Operation(summary = "标记已读")
    @PostMapping("/notification/read")
    public Map<String, Object> markRead(@RequestBody Map<String, Long> body) {
        Long messageId = body.get("id");
        notificationApplicationService.markRead(messageId);
        return AjaxResult.success("已读");
    }

    @Operation(summary = "未读消息数")
    @GetMapping("/notification/unread-count")
    public Map<String, Object> unreadCount() {
        long count = notificationApplicationService.unreadCount();
        return AjaxResult.success("查询成功", count);
    }
}
