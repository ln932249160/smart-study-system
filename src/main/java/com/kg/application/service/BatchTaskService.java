package com.kg.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kg.domain.model.ClassInfo;
import com.kg.domain.model.SysUser;
import com.kg.domain.model.Task;
import com.kg.domain.model.TaskUser;
import com.kg.domain.repository.ClassInfoRepository;
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
 * 批处理任务服务 —— 定时任务 + 启动补偿的共享逻辑。
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
    private final ClassInfoRepository classInfoRepository;
    private final NotificationMessageMapper notificationMessageMapper;

    public BatchTaskService(TaskRepository taskRepository,
                            TaskUserRepository taskUserRepository,
                            SysUserRepository sysUserRepository,
                            ClassInfoRepository classInfoRepository,
                            NotificationMessageMapper notificationMessageMapper) {
        this.taskRepository = taskRepository;
        this.taskUserRepository = taskUserRepository;
        this.sysUserRepository = sysUserRepository;
        this.classInfoRepository = classInfoRepository;
        this.notificationMessageMapper = notificationMessageMapper;
    }

    // ======================== 对外入口（兼容旧调用） ========================

    /** 定时任务入口：创建今天的每日任务，幂等 */
    @Transactional(rollbackFor = Exception.class)
    public void createDailyTaskIfAbsent(String taskName, String taskType,
                                        int startHour, int forceFlag) {
        createDailyTaskForDate(taskName, taskType, LocalDate.now(), startHour, forceFlag);
    }

    /** 启动补偿入口：检查指定日期的任务是否存在，不存在则补生成 */
    public void ensureDailyTaskExists(String taskName, String taskType,
                                       LocalDate date, int startHour, int forceFlag) {
        if (existsDailyTask(date, taskType)) {
            log.info("【启动补偿】{} ({}) 已存在，跳过", taskName, date);
            return;
        }
        log.info("【启动补偿】{} ({}) 不存在，开始补生成", taskName, date);
        createDailyTaskForDate(taskName, taskType, date, startHour, forceFlag);
    }

    // ======================== 核心：指定日期生成 ========================

    /**
     * 为指定日期创建每日任务（打卡/复盘），同一天同类型只创建一次。
     */
    @Transactional(rollbackFor = Exception.class)
    public void createDailyTaskForDate(String taskName, String taskType,
                                        LocalDate date, int startHour, int forceFlag) {
        // 幂等校验
        if (existsDailyTask(date, taskType)) {
            log.info("{} ({}) 已存在，跳过", taskName, date);
            return;
        }

        // 获取所有班级ID
        List<ClassInfo> classes = classInfoRepository.listActiveClasses();
        String targetIds = classes.stream()
                .map(c -> String.valueOf(c.getId()))
                .reduce((a, b) -> a + "," + b).orElse("");

        // 创建 task（班级任务）
        Task task = new Task();
        task.setTaskName(taskName);
        task.setTaskType(taskType);
        task.setIsMandatory(forceFlag);
        task.setTargetType(1);
        task.setTargetIds(targetIds);
        task.setTaskStartTime(LocalDateTime.of(date, LocalTime.of(startHour, 0)));
        task.setTaskEndTime(LocalDateTime.of(date, LocalTime.of(23, 59)));
        task.setCreateBy(SYSTEM_USER_ID);
        task.setCreateTime(LocalDateTime.now());
        taskRepository.save(task);

        // 查所有学生+班长
        List<SysUser> students = sysUserRepository.listByRoles(STUDENT_ROLES);
        if (students.isEmpty()) {
            log.info("{}创建成功(id={}, date={})，无学生需要分配", taskName, task.getId(), date);
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
        log.info("{}创建成功: taskId={}, date={}, targetType=1, 分配{}人",
                taskName, task.getId(), date, students.size());
    }

    // ======================== 存在检查 ========================

    /** 检查指定日期 + 指定类型的每日任务是否已存在 */
    public boolean existsDailyTask(LocalDate date, String taskType) {
        List<Task> allTasks = taskRepository.page(0, 500);
        return allTasks.stream()
                .anyMatch(t -> taskType.equals(t.getTaskType())
                        && t.getCreateTime() != null
                        && t.getCreateTime().toLocalDate().equals(date));
    }

    // ======================== 通知生成 ========================

    @Transactional(rollbackFor = Exception.class)
    public void generateNotifications() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        List<Task> allTasks = taskRepository.page(0, 500);
        if (allTasks == null || allTasks.isEmpty()) return;

        List<NotificationMessageEntity> toInsert = new ArrayList<>();

        for (Task task : allTasks) {
            LocalDateTime start = task.getTaskStartTime();
            LocalDateTime end = task.getTaskEndTime();
            if (start == null || end == null) continue;

            String type = null;
            String title = null;

            if (truncated(start).equals(now)) {
                type = "TASK_START";
                title = "任务「" + task.getTaskName() + "」已开始";
            } else if (end.minusHours(1).truncatedTo(ChronoUnit.MINUTES).equals(now)) {
                type = "TASK_WARNING";
                title = "任务「" + task.getTaskName() + "」即将截止";
            } else if (truncated(end).equals(now)) {
                type = "TASK_END";
                title = "任务「" + task.getTaskName() + "」已结束";
            }

            if (type == null) continue;

            List<TaskUser> taskUsers = taskUserRepository.findByTaskId(task.getId());
            for (TaskUser tu : taskUsers) {
                if (notificationExists(tu.getUserId(), task.getId(), type)) continue;
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
