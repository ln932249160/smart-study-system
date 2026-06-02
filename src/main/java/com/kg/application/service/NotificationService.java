package com.kg.application.service;

import com.kg.domain.model.SysUser;
import com.kg.domain.model.Task;
import com.kg.domain.repository.SysUserRepository;
import com.kg.infrastructure.entity.NotificationMessageEntity;
import com.kg.infrastructure.mapper.NotificationMessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 通知消息生成服务 —— 封装消息生成逻辑，在任务创建/编辑时调用。
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    /** 消息类型 */
    private static final String TYPE_START = "TASK_START";
    private static final String TYPE_DEADLINE = "TASK_DEADLINE";

    private final NotificationMessageMapper messageMapper;
    private final SysUserRepository sysUserRepository;

    public NotificationService(NotificationMessageMapper messageMapper,
                               SysUserRepository sysUserRepository) {
        this.messageMapper = messageMapper;
        this.sysUserRepository = sysUserRepository;
    }

    /**
     * 任务创建/编辑后调用：生成 TASK_START + TASK_DEADLINE 两条通知。
     * 先删除该任务的旧未触发消息，再生成新消息。
     */
    public void regenerateForTask(Task task) {
        // 1. 删除旧未触发消息
        deletePendingForTask(task.getId());
        // 2. 解析接收用户
        List<Long> userIds = resolveUserIds(task);
        if (userIds.isEmpty()) return;
        // 3. 批量生成
        List<NotificationMessageEntity> batch = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Long uid : userIds) {
            // TASK_START：在任务开始时间生效
            NotificationMessageEntity startMsg = new NotificationMessageEntity();
            startMsg.setUserId(uid);
            startMsg.setTitle("【" + task.getTaskName() + "】开始了");
            startMsg.setContent(task.getTaskDescription() != null ? task.getTaskDescription() : "");
            startMsg.setPriority(1);
            startMsg.setType(TYPE_START);
            startMsg.setRelatedId(task.getId());
            startMsg.setIsRead(0);
            startMsg.setCreatedAt(now);
            startMsg.setNotifyTime(task.getTaskStartTime());
            batch.add(startMsg);

            // TASK_DEADLINE：在任务90%时间点生效
            if (task.getTaskStartTime() != null && task.getTaskEndTime() != null) {
                long totalSeconds = Duration.between(task.getTaskStartTime(), task.getTaskEndTime()).getSeconds();
                LocalDateTime deadlineNotifyTime = task.getTaskStartTime().plusSeconds((long) (totalSeconds * 0.9));

                NotificationMessageEntity deadlineMsg = new NotificationMessageEntity();
                deadlineMsg.setUserId(uid);
                deadlineMsg.setTitle("【" + task.getTaskName() + "】即将截止");
                deadlineMsg.setContent("任务即将结束，请尽快完成");
                deadlineMsg.setPriority(1);
                deadlineMsg.setType(TYPE_DEADLINE);
                deadlineMsg.setRelatedId(task.getId());
                deadlineMsg.setIsRead(0);
                deadlineMsg.setCreatedAt(now);
                deadlineMsg.setNotifyTime(deadlineNotifyTime);
                batch.add(deadlineMsg);
            }
        }

        for (NotificationMessageEntity msg : batch) {
            messageMapper.insert(msg);
        }
        log.info("为任务 {} 生成 {} 条通知消息", task.getId(), batch.size());
    }

    /** 解析任务分配的目标用户列表 */
    private List<Long> resolveUserIds(Task task) {
        if (task.getTargetType() == null || task.getTargetIds() == null || task.getTargetIds().isEmpty()) {
            return new ArrayList<>();
        }
        if (task.getTargetType() == 1) {
            // 班级分配
            List<Long> ids = new ArrayList<>();
            for (String s : task.getTargetIds().split(",")) {
                try {
                    Long cid = Long.valueOf(s.trim());
                    ids.addAll(sysUserRepository.findByClassId(cid).stream()
                            .map(SysUser::getId).collect(java.util.stream.Collectors.toList()));
                } catch (NumberFormatException ignored) {}
            }
            return ids;
        } else if (task.getTargetType() == 2) {
            // 学生分配
            List<Long> ids = new ArrayList<>();
            for (String s : task.getTargetIds().split(",")) {
                try {
                    ids.add(Long.valueOf(s.trim()));
                } catch (NumberFormatException ignored) {}
            }
            return ids;
        }
        return new ArrayList<>();
    }

    /** 删除任务相关的未触发消息（notify_time > now） */
    public void deletePendingForTask(Long taskId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<NotificationMessageEntity> q =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        q.eq(NotificationMessageEntity::getRelatedId, taskId);
        q.gt(NotificationMessageEntity::getNotifyTime, LocalDateTime.now());
        messageMapper.delete(q);
    }
}
