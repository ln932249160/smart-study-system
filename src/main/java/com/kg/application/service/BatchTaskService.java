package com.kg.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kg.domain.model.SysUser;
import com.kg.domain.model.Task;
import com.kg.domain.model.TaskUser;
import com.kg.domain.repository.SysUserRepository;
import com.kg.domain.repository.TaskRepository;
import com.kg.domain.repository.TaskUserRepository;
import com.kg.enums.RoleEnum;
import com.kg.enums.TaskStatusEnum;
import com.kg.infrastructure.entity.NotificationMessageEntity;
import com.kg.infrastructure.mapper.NotificationMessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 批处理任务服务 —— 定时任务的共享逻辑。
 */
@Service
public class BatchTaskService {

    private static final Logger log = LoggerFactory.getLogger(BatchTaskService.class);
    private static final List<String> STUDENT_ROLES = Arrays.asList(
            RoleEnum.HEADMASTER.getCode(), RoleEnum.STUDENT.getCode());
    private static final long SYSTEM_USER_ID = 0L;

    private final TaskRepository taskRepository;
    private final TaskUserRepository taskUserRepository;
    private final SysUserRepository sysUserRepository;
    private final NotificationMessageMapper notificationMessageMapper;

    public BatchTaskService(TaskRepository taskRepository,
                            TaskUserRepository taskUserRepository,
                            SysUserRepository sysUserRepository,
                            NotificationMessageMapper notificationMessageMapper) {
        this.taskRepository = taskRepository;
        this.taskUserRepository = taskUserRepository;
        this.sysUserRepository = sysUserRepository;
        this.notificationMessageMapper = notificationMessageMapper;
    }

    /**
     * 创建每日自动任务（打卡/复盘），同一天同类型只创建一次。
     */
    @Transactional(rollbackFor = Exception.class)
    public void createDailyTaskIfAbsent(String taskName, String taskType,
                                        int startHour, int forceFlag) {
        LocalDate today = LocalDate.now();

        // 今天是否已创建
        List<Task> todayTasks = taskRepository.page(0, 100);
        boolean exists = todayTasks.stream()
                .anyMatch(t -> taskType.equals(t.getTaskType())
                        && t.getCreateTime() != null
                        && t.getCreateTime().toLocalDate().equals(today));
        if (exists) {
            log.info("今日{}已存在，跳过", taskName);
            return;
        }

        // 创建 task
        Task task = new Task();
        task.setTaskName(taskName);
        task.setTaskType(taskType);
        task.setIsMandatory(forceFlag);
        task.setTaskStartTime(LocalDateTime.of(today, LocalTime.of(startHour, 0)));
        task.setTaskEndTime(LocalDateTime.of(today, LocalTime.of(23, 59)));
        task.setCreateBy(SYSTEM_USER_ID);
        task.setCreateTime(LocalDateTime.now());
        taskRepository.save(task);

        // 查所有学生+班长
        List<SysUser> students = sysUserRepository.listByRoles(STUDENT_ROLES);
        if (students.isEmpty()) {
            log.info("{}创建成功(id={})，无学生需要分配", taskName, task.getId());
            return;
        }

        // 批量插入 task_user
        List<TaskUser> taskUsers = new ArrayList<>(students.size());
        for (SysUser u : students) {
            TaskUser tu = new TaskUser();
            tu.setTaskId(task.getId());
            tu.setUserId(u.getId());
            tu.setStatus(TaskStatusEnum.UNFINISHED.getCode());
            taskUsers.add(tu);
        }
        taskUserRepository.batchSave(taskUsers);
        log.info("{}创建成功: taskId={}, 分配{}人", taskName, task.getId(), students.size());
    }

    /**
     * 每分钟执行：扫描 task 表，生成通知消息（TASK_START / TASK_WARNING / TASK_END）。
     */
    @Transactional(rollbackFor = Exception.class)
    public void generateNotifications() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime oneHourLater = now.plusHours(1);

        // 查所有非模板任务
        List<Task> allTasks = taskRepository.page(0, 500);
        if (allTasks == null || allTasks.isEmpty()) return;

        List<NotificationMessageEntity> toInsert = new ArrayList<>();

        for (Task task : allTasks) {
            LocalDateTime start = task.getTaskStartTime();
            LocalDateTime end = task.getTaskEndTime();
            if (start == null || end == null) continue;

            String type = null;
            String title = null;

            // 任务开始
            if (truncated(start).equals(now)) {
                type = "TASK_START";
                title = "任务「" + task.getTaskName() + "」已开始";
            }
            // 1小时内截止（NOW 在 end-1h ~ end-1min 之间）
            else if (end.minusHours(1).truncatedTo(ChronoUnit.MINUTES).equals(now)) {
                type = "TASK_WARNING";
                title = "任务「" + task.getTaskName() + "」即将截止";
            }
            // 任务结束
            else if (truncated(end).equals(now)) {
                type = "TASK_END";
                title = "任务「" + task.getTaskName() + "」已结束";
            }

            if (type == null) continue;

            // 查分配的用户
            List<TaskUser> taskUsers = taskUserRepository.findByTaskId(task.getId());
            for (TaskUser tu : taskUsers) {
                // 防重：user_id + related_id + type
                if (notificationExists(tu.getUserId(), task.getId(), type)) {
                    continue;
                }
                NotificationMessageEntity msg = new NotificationMessageEntity();
                msg.setUserId(tu.getUserId());
                msg.setTitle(title);
                msg.setContent(title);
                msg.setType(type);
                msg.setRelatedId(task.getId());
                msg.setStatus("0");
                msg.setPriority(1);
                msg.setIsRead(0);
                toInsert.add(msg);
            }
        }

        if (!toInsert.isEmpty()) {
            for (NotificationMessageEntity msg : toInsert) {
                notificationMessageMapper.insert(msg);
            }
            log.info("生成通知消息: {} 条", toInsert.size());
        }
    }

    private boolean notificationExists(Long userId, Long taskId, String type) {
        LambdaQueryWrapper<NotificationMessageEntity> q = new LambdaQueryWrapper<>();
        q.eq(NotificationMessageEntity::getUserId, userId);
        q.eq(NotificationMessageEntity::getRelatedId, taskId);
        q.eq(NotificationMessageEntity::getType, type);
        q.eq(NotificationMessageEntity::getStatus, "0");
        return notificationMessageMapper.selectCount(q) > 0;
    }

    private LocalDateTime truncated(LocalDateTime dt) {
        return dt.truncatedTo(ChronoUnit.MINUTES);
    }
}
