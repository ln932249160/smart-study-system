package com.kg.application.service;

import com.kg.context.UserContext;
import com.kg.domain.model.SysUser;
import com.kg.domain.model.Task;
import com.kg.domain.model.TaskScore;
import com.kg.domain.model.TaskUser;
import com.kg.domain.repository.SysUserRepository;
import com.kg.domain.repository.TaskRepository;
import com.kg.domain.repository.TaskScoreRepository;
import com.kg.domain.repository.TaskUserRepository;
import com.kg.exception.BusinessException;
import com.kg.interfaces.dto.TaskCompleteRequest;
import com.kg.interfaces.dto.TaskCreateRequest;
import com.kg.interfaces.dto.TaskUpdateRequest;
import com.kg.interfaces.dto.TaskVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任务管理应用服务 —— 编排任务的 CRUD 及完成业务流程。
 */
@Service
public class TaskApplicationService {

    private static final Logger log = LoggerFactory.getLogger(TaskApplicationService.class);
    private static final String ROLE_STUDENT = "student";
    private static final String STATUS_UNFINISHED = "0";
    private static final String STATUS_FINISHED = "1";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TaskRepository taskRepository;
    private final TaskUserRepository taskUserRepository;
    private final TaskScoreRepository taskScoreRepository;
    private final SysUserRepository sysUserRepository;

    public TaskApplicationService(TaskRepository taskRepository,
                                  TaskUserRepository taskUserRepository,
                                  TaskScoreRepository taskScoreRepository,
                                  SysUserRepository sysUserRepository) {
        this.taskRepository = taskRepository;
        this.taskUserRepository = taskUserRepository;
        this.taskScoreRepository = taskScoreRepository;
        this.sysUserRepository = sysUserRepository;
    }

    // ======================== 分页查询 ========================

    /**
     * 分页查询任务列表（不含模板任务）。
     */
    public Map<String, Object> page(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        long total = taskRepository.count();
        List<Task> tasks = taskRepository.page(offset, pageSize);

        // 查 task_user 统计每个任务的完成/未完成人数
        List<TaskVO> list = tasks.stream().map(t -> {
            TaskVO vo = toVO(t);
            List<TaskUser> taskUsers = taskUserRepository.findByTaskId(t.getId());
            long completed = taskUsers.stream().filter(tu -> "1".equals(tu.getStatus())).count();
            long uncompleted = taskUsers.size() - completed;
            vo.setCompletedCount((int) completed);
            vo.setUncompletedCount((int) uncompleted);
            if (taskUsers.isEmpty()) {
                vo.setCompletionPercent(0);
            } else {
                vo.setCompletionPercent((int) Math.round(completed * 100.0 / taskUsers.size()));
            }
            return vo;
        }).collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", total);
        result.put("list", list);
        return result;
    }

    // ======================== 新增 ========================

    /**
     * 新增任务，自动为被分配的学生/班级生成 task_user 记录。
     * 模板任务不生成 task_user。
     */
    @Transactional(rollbackFor = Exception.class)
    public void create(TaskCreateRequest request) {
        Long currentUserId = getCurrentUserId();

        // 1. 插入 task
        Task task = buildTask(request, currentUserId);
        taskRepository.save(task);
        Long taskId = task.getId();
        log.info("创建任务成功: id={}, taskName={}, isTemplate={}", taskId, request.getTaskName(), request.getIsTemplate());

        // 2. 模板任务不生成分配记录
        if (request.getIsTemplate() != null && request.getIsTemplate() == 1) {
            return;
        }

        // 3. 确定分配给哪些学生
        List<Long> userIds = resolveStudentIds(request);
        if (userIds.isEmpty()) {
            return;
        }

        // 4. 批量生成 task_user
        List<TaskUser> taskUsers = new ArrayList<>(userIds.size());
        for (Long userId : userIds) {
            TaskUser tu = new TaskUser();
            tu.setTaskId(taskId);
            tu.setUserId(userId);
            tu.setStatus(STATUS_UNFINISHED);
            tu.setCreateBy(currentUserId);
            taskUsers.add(tu);
        }
        taskUserRepository.batchSave(taskUsers);
        log.info("分配任务 {} 给 {} 名学生", taskId, userIds.size());
    }

    // ======================== 编辑 ========================

    /**
     * 编辑任务：更新 task 记录，删除原分配并按新规则重新生成 task_user。
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, TaskUpdateRequest request) {
        Long currentUserId = getCurrentUserId();

        // 1. 更新 task
        Task task = buildUpdateTask(id, request, currentUserId);
        taskRepository.update(task);

        // 2. 删除旧 task_user + task_score
        taskScoreRepository.deleteByTaskId(id);
        taskUserRepository.deleteByTaskId(id);

        // 3. 按新规则重新生成 task_user
        List<Long> userIds = resolveUpdateStudentIds(request);
        if (!userIds.isEmpty()) {
            List<TaskUser> taskUsers = new ArrayList<>(userIds.size());
            for (Long userId : userIds) {
                TaskUser tu = new TaskUser();
                tu.setTaskId(id);
                tu.setUserId(userId);
                tu.setStatus(STATUS_UNFINISHED);
                tu.setCreateBy(currentUserId);
                taskUsers.add(tu);
            }
            taskUserRepository.batchSave(taskUsers);
        }
        log.info("编辑任务成功: id={}", id);
    }

    // ======================== 删除 ========================

    /**
     * 删除任务及其所有关联的 task_user、task_score。
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        taskScoreRepository.deleteByTaskId(id);
        taskUserRepository.deleteByTaskId(id);
        taskRepository.deleteById(id);
        log.info("删除任务成功: id={}", id);
    }

    // ======================== 完成任务 ========================

    /**
     * 当前用户完成任务。
     * 更新 task_user 完成状态，若提供了成绩明细则写入 task_score。
     */
    @Transactional(rollbackFor = Exception.class)
    public void complete(TaskCompleteRequest request) {
        Long currentUserId = getCurrentUserId();
        Long taskId = request.getTaskId();

        // 查找当前用户的 task_user 记录
        TaskUser taskUser = taskUserRepository.findByTaskIdAndUserId(taskId, currentUserId);
        if (taskUser == null) {
            throw new BusinessException("未找到您的任务分配记录");
        }

        // 更新完成状态
        taskUser.setStatus(STATUS_FINISHED);
        taskUser.setFinishTime(LocalDateTime.now());
        taskUser.setSubmitTime(LocalDateTime.now());
        taskUser.setDurationMinutes(request.getDurationMinutes());
        taskUser.setRemark(request.getRemark());
        taskUser.setUpdateBy(currentUserId);

        // 成绩处理
        BigDecimal totalScore = request.getTotalScore();
        if (totalScore != null) {
            taskUser.setTotalScore(totalScore);
        }

        taskUserRepository.update(taskUser);

        // 写入成绩明细
        List<TaskCompleteRequest.ScoreItem> scores = request.getScores();
        if (scores != null && !scores.isEmpty()) {
            List<TaskScore> scoreList = new ArrayList<>(scores.size());
            for (TaskCompleteRequest.ScoreItem item : scores) {
                TaskScore ts = new TaskScore();
                ts.setTaskUserId(taskUser.getId());
                ts.setModuleName(item.getModuleName());
                ts.setScore(item.getScore());
                ts.setCreateBy(currentUserId);
                scoreList.add(ts);
            }
            taskScoreRepository.batchSave(scoreList);

            // 若未传 totalScore，用明细求和
            if (totalScore == null) {
                BigDecimal sum = BigDecimal.ZERO;
                for (TaskScore ts : scoreList) {
                    if (ts.getScore() != null) {
                        sum = sum.add(ts.getScore());
                    }
                }
                taskUser.setTotalScore(sum);
                taskUserRepository.update(taskUser);
            }
        }

        log.info("完成任务成功: taskId={}, userId={}", taskId, currentUserId);
    }

    // ======================== 模板下拉 ========================

    /**
     * 查询所有模板任务，供下拉选择。
     */
    public List<TaskVO> listTemplates() {
        return taskRepository.listTemplates().stream().map(this::toVO).collect(Collectors.toList());
    }

    // ======================== 私有方法 ========================

    /** 获取当前登录用户ID */
    private Long getCurrentUserId() {
        SysUser user = UserContext.getUser();
        if (user == null) throw new BusinessException("未登录");
        return user.getId();
    }

    /** 构造新增 Task */
    private Task buildTask(TaskCreateRequest r, Long createBy) {
        Task t = new Task();
        t.setTaskName(r.getTaskName());
        t.setTaskType(r.getTaskType());
        t.setIsTemplate(r.getIsTemplate());
        t.setTemplateTaskId(r.getTemplateTaskId());
        t.setRoundNo(r.getRoundNo());
        t.setIsMandatory(r.getIsMandatory());
        t.setTaskDescription(r.getTaskDescription());
        t.setTaskStartTime(parseDateTime(r.getTaskStartTime()));
        t.setTaskEndTime(parseDateTime(r.getTaskEndTime()));
        t.setPriority(r.getPriority());
        t.setClassId(r.getClassId());
        t.setCreateBy(createBy);
        t.setCreateTime(LocalDateTime.now());
        return t;
    }

    /** 构造更新 Task */
    private Task buildUpdateTask(Long id, TaskUpdateRequest r, Long updateBy) {
        Task t = new Task();
        t.setId(id);
        t.setTaskName(r.getTaskName());
        t.setTaskType(r.getTaskType());
        t.setIsTemplate(r.getIsTemplate());
        t.setTemplateTaskId(r.getTemplateTaskId());
        t.setRoundNo(r.getRoundNo());
        t.setIsMandatory(r.getIsMandatory());
        t.setTaskDescription(r.getTaskDescription());
        t.setTaskStartTime(parseDateTime(r.getTaskStartTime()));
        t.setTaskEndTime(parseDateTime(r.getTaskEndTime()));
        t.setPriority(r.getPriority());
        t.setClassId(r.getClassId());
        t.setUpdateBy(updateBy);
        return t;
    }

    /** 新增时：确定分配学生列表 */
    private List<Long> resolveStudentIds(TaskCreateRequest r) {
        // 班级优先
        if (r.getClassId() != null) {
            List<SysUser> users = sysUserRepository.findByClassId(r.getClassId());
            return users.stream().map(SysUser::getId).collect(Collectors.toList());
        }
        // 指定学生
        if (r.getStudentIds() != null && !r.getStudentIds().isEmpty()) {
            return r.getStudentIds();
        }
        return new ArrayList<>();
    }

    /** 编辑时：确定分配学生列表 */
    private List<Long> resolveUpdateStudentIds(TaskUpdateRequest r) {
        if (r.getClassId() != null) {
            List<SysUser> users = sysUserRepository.findByClassId(r.getClassId());
            return users.stream().map(SysUser::getId).collect(Collectors.toList());
        }
        if (r.getStudentIds() != null && !r.getStudentIds().isEmpty()) {
            return r.getStudentIds();
        }
        return new ArrayList<>();
    }

    /** Task → TaskVO */
    private TaskVO toVO(Task t) {
        TaskVO vo = new TaskVO();
        vo.setId(t.getId());
        vo.setTaskName(t.getTaskName());
        vo.setTaskType(t.getTaskType());
        vo.setIsTemplate(t.getIsTemplate());
        vo.setTemplateTaskId(t.getTemplateTaskId());
        vo.setRoundNo(t.getRoundNo());
        vo.setIsMandatory(t.getIsMandatory());
        vo.setTaskDescription(t.getTaskDescription());
        vo.setTaskStartTime(t.getTaskStartTime() != null ? t.getTaskStartTime().format(FMT) : null);
        vo.setTaskEndTime(t.getTaskEndTime() != null ? t.getTaskEndTime().format(FMT) : null);
        vo.setPriority(t.getPriority());
        vo.setClassId(t.getClassId());
        vo.setCreateTime(t.getCreateTime() != null ? t.getCreateTime().format(FMT) : null);
        return vo;
    }

    private LocalDateTime parseDateTime(String str) {
        if (str == null || str.trim().isEmpty()) return null;
        try {
            return LocalDateTime.parse(str, FMT);
        } catch (Exception e) {
            return LocalDateTime.parse(str);
        }
    }
}
