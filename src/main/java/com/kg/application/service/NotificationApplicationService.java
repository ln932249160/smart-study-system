package com.kg.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kg.context.UserContext;
import com.kg.domain.model.SysUser;
import com.kg.exception.BusinessException;
import com.kg.infrastructure.entity.NotificationMessageEntity;
import com.kg.infrastructure.mapper.NotificationMessageMapper;
import com.kg.interfaces.dto.NotificationPageRequest;
import com.kg.interfaces.dto.NotificationVO;
import com.kg.interfaces.dto.PageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通知消息应用服务 —— 查询列表 + 已读 + 未读数。
 */
@Service
public class NotificationApplicationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationApplicationService.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int READ = 1;

    private final NotificationMessageMapper notificationMessageMapper;

    public NotificationApplicationService(NotificationMessageMapper notificationMessageMapper) {
        this.notificationMessageMapper = notificationMessageMapper;
    }

    /**
     * 分页查询当前用户消息，支持多条件筛选。
     */
    public PageVO<NotificationVO> page(NotificationPageRequest req) {
        Long userId = requireUserId();
        int offset = (req.getPageNum() - 1) * req.getPageSize();

        LambdaQueryWrapper<NotificationMessageEntity> q = new LambdaQueryWrapper<>();
        q.eq(NotificationMessageEntity::getUserId, userId);
        q.le(NotificationMessageEntity::getNotifyTime, LocalDateTime.now());
        // 默认只查有效消息，可传入 status 覆盖
        q.eq(NotificationMessageEntity::getStatus, req.getStatus() != null ? req.getStatus() : "0");
        // 筛选条件
        if (req.getTitle() != null && !req.getTitle().isEmpty()) {
            q.like(NotificationMessageEntity::getTitle, req.getTitle());
        }
        if (req.getType() != null && !req.getType().isEmpty()) {
            q.eq(NotificationMessageEntity::getType, req.getType());
        }
        if (req.getIsRead() != null) {
            q.eq(NotificationMessageEntity::getIsRead, req.getIsRead());
        }
        if (req.getPriority() != null) {
            q.eq(NotificationMessageEntity::getPriority, req.getPriority());
        }
        if (req.getRelatedId() != null) {
            q.eq(NotificationMessageEntity::getRelatedId, req.getRelatedId());
        }
        if (req.getNotifyTimeBegin() != null && !req.getNotifyTimeBegin().isEmpty()) {
            q.ge(NotificationMessageEntity::getNotifyTime, LocalDateTime.parse(req.getNotifyTimeBegin(), FMT));
        }
        if (req.getNotifyTimeEnd() != null && !req.getNotifyTimeEnd().isEmpty()) {
            q.le(NotificationMessageEntity::getNotifyTime, LocalDateTime.parse(req.getNotifyTimeEnd(), FMT));
        }
        q.orderByDesc(NotificationMessageEntity::getNotifyTime);
        long total = notificationMessageMapper.selectCount(q);

        q.last("LIMIT " + offset + "," + req.getPageSize());
        List<NotificationMessageEntity> entities = notificationMessageMapper.selectList(q);

        List<NotificationVO> list;
        if (entities == null || entities.isEmpty()) {
            list = Collections.emptyList();
        } else {
            list = entities.stream().map(e -> {
                NotificationVO vo = new NotificationVO();
                vo.setId(e.getId());
                vo.setTitle(e.getTitle());
                vo.setContent(e.getContent());
                vo.setType(e.getType());
                vo.setRelatedId(e.getRelatedId());
                vo.setStatus(e.getStatus());
                vo.setIsRead(e.getIsRead());
                vo.setPriority(e.getPriority());
                if (e.getCreatedAt() != null) vo.setCreatedAt(e.getCreatedAt().format(FMT));
                if (e.getNotifyTime() != null) vo.setNotifyTime(e.getNotifyTime().format(FMT));
                return vo;
            }).collect(Collectors.toList());
        }

        return PageVO.of(total, list);
    }

    /** 未读消息数（仅已生效的有效消息） */
    public long unreadCount() {
        Long userId = requireUserId();
        LambdaQueryWrapper<NotificationMessageEntity> q = new LambdaQueryWrapper<>();
        q.eq(NotificationMessageEntity::getUserId, userId);
        q.eq(NotificationMessageEntity::getIsRead, 0);
        q.eq(NotificationMessageEntity::getStatus, "0");
        q.le(NotificationMessageEntity::getNotifyTime, LocalDateTime.now());
        return notificationMessageMapper.selectCount(q);
    }

    /**
     * 单条消息标记为已读，校验 user_id 归属。
     */
    public void markRead(Long messageId) {
        Long userId = requireUserId();
        NotificationMessageEntity entity = notificationMessageMapper.selectById(messageId);
        if (entity == null || !"0".equals(entity.getStatus())) {
            throw new BusinessException("消息不存在");
        }
        if (!userId.equals(entity.getUserId())) {
            throw new BusinessException(403, "无权操作此消息");
        }

        LambdaUpdateWrapper<NotificationMessageEntity> w = new LambdaUpdateWrapper<>();
        w.eq(NotificationMessageEntity::getId, messageId);
        w.set(NotificationMessageEntity::getIsRead, READ);
        notificationMessageMapper.update(null, w);
        log.info("消息已读: messageId={}, userId={}", messageId, userId);
    }

    private Long requireUserId() {
        SysUser user = UserContext.getUser();
        if (user == null) throw new BusinessException(401, "未登录");
        return user.getId();
    }
}
