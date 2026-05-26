package com.kg.application.service;

import com.kg.context.UserContext;
import com.kg.domain.model.SysUser;
import com.kg.domain.model.Task;
import com.kg.domain.model.TaskUser;
import com.kg.domain.repository.SysUserRepository;
import com.kg.domain.repository.TaskRepository;
import com.kg.domain.repository.TaskScoreRepository;
import com.kg.domain.repository.TaskUserRepository;
import com.kg.enums.RoleEnum;
import com.kg.enums.TaskStatusEnum;
import com.kg.exception.BusinessException;
import com.kg.interfaces.dto.TaskCreateRequest;
import com.kg.interfaces.dto.TaskUpdateRequest;
import com.kg.interfaces.dto.TaskVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任务管理应用服务 —— 角色权限：teacher 全部、headmaster 本班、student 本人。
 */
@Service
public class TaskApplicationService {

    private static final Logger log = LoggerFactory.getLogger(TaskApplicationService.class);
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
     * 分页查询任务。
     * teacher → 全部任务 | headmaster → 本班任务 | student → 分配给我的任务
     */
    public Map<String, Object> page(int pageNum, int pageSize) {
        SysUser currentUser = requireCurrentUser();
        String role = currentUser.getRole();
        int offset = (pageNum - 1) * pageSize;

        List<Task> tasks;
        long total;

        if (RoleEnum.isStudent(role)) {
            // 学生：查我分配的任务
            List<TaskUser> myTaskUsers = taskUserRepository.findByUserId(currentUser.getId());
            List<Long> taskIds = myTaskUsers.stream().map(TaskUser::getTaskId).distinct().collect(Collectors.toList());
            if (taskIds.isEmpty()) {
                return emptyPageResult();
            }
            tasks = taskRepository.pageByIds(taskIds, offset, pageSize);
            total = taskRepository.countByIds(taskIds);
        } else if (RoleEnum.isHeadmaster(role)) {
            // 班长：查本班任务
            Long classId = currentUser.getClassId();
            if (classId == null) {
                return emptyPageResult();
            }
            tasks = taskRepository.pageByClassId(classId, offset, pageSize);
            total = taskRepository.countByClassId(classId);
        } else {
            // 老师：全部
            tasks = taskRepository.page(offset, pageSize);
            total = taskRepository.count();
        }

        List<TaskVO> list = tasks.stream().map(t -> {
            TaskVO vo = toVO(t);
            List<TaskUser> taskUsers = taskUserRepository.findByTaskId(t.getId());
            long completed = taskUsers.stream().filter(tu -> TaskStatusEnum.FINISHED.getCode().equals(tu.getStatus())).count();
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

    // ======================== 新增（仅 teacher） ========================

    @Transactional(rollbackFor = Exception.class)
    public void create(TaskCreateRequest request) {
        Long currentUserId = requireTeacher();
        Task task = buildTask(request, currentUserId);
        taskRepository.save(task);
        Long taskId = task.getId();
        log.info("创建任务成功: id={}, taskName={}", taskId, request.getTaskName());

        if (request.getIsTemplate() != null && request.getIsTemplate() == 1) {
            return;
        }

        List<Long> userIds = resolveStudentIds(request);
        if (userIds.isEmpty()) return;

        List<TaskUser> taskUsers = new ArrayList<>();
        for (Long uid : userIds) {
            TaskUser tu = new TaskUser();
            tu.setTaskId(taskId);
            tu.setUserId(uid);
            tu.setStatus(TaskStatusEnum.UNFINISHED.getCode());
            tu.setCreateBy(currentUserId);
            taskUsers.add(tu);
        }
        taskUserRepository.batchSave(taskUsers);
        log.info("分配任务 {} 给 {} 人", taskId, userIds.size());
    }

    // ======================== 编辑（仅 teacher） ========================

    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, TaskUpdateRequest request) {
        Long currentUserId = requireTeacher();
        Task task = buildUpdateTask(id, request, currentUserId);
        taskRepository.update(task);

        taskScoreRepository.deleteByTaskId(id);
        taskUserRepository.deleteByTaskId(id);

        List<Long> userIds = resolveUpdateStudentIds(request);
        if (!userIds.isEmpty()) {
            List<TaskUser> taskUsers = new ArrayList<>();
            for (Long uid : userIds) {
                TaskUser tu = new TaskUser();
                tu.setTaskId(id);
                tu.setUserId(uid);
                tu.setStatus(TaskStatusEnum.UNFINISHED.getCode());
                tu.setCreateBy(currentUserId);
                taskUsers.add(tu);
            }
            taskUserRepository.batchSave(taskUsers);
        }
        log.info("编辑任务成功: id={}", id);
    }

    // ======================== 删除（仅 teacher） ========================

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        requireTeacher();
        taskScoreRepository.deleteByTaskId(id);
        taskUserRepository.deleteByTaskId(id);
        taskRepository.deleteById(id);
        log.info("删除任务成功: id={}", id);
    }

    // ======================== 模板下拉 ========================

    public List<TaskVO> listTemplates() {
        return taskRepository.listTemplates().stream().map(this::toVO).collect(Collectors.toList());
    }

    // ======================== 权限校验 ========================

    private SysUser requireCurrentUser() {
        SysUser user = UserContext.getUser();
        if (user == null) throw new BusinessException(401, "未登录");
        return user;
    }

    /** 仅老师可操作，返回当前用户ID */
    private Long requireTeacher() {
        SysUser user = requireCurrentUser();
        if (!RoleEnum.isTeacher(user.getRole())) {
            throw new BusinessException(403, "仅老师可操作");
        }
        return user.getId();
    }

    // ======================== 私有方法 ========================

    private Map<String, Object> emptyPageResult() {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("total", 0);
        r.put("list", Collections.emptyList());
        return r;
    }

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

    private List<Long> resolveStudentIds(TaskCreateRequest r) {
        if (r.getClassId() != null) {
            return sysUserRepository.findByClassId(r.getClassId()).stream().map(SysUser::getId).collect(Collectors.toList());
        }
        if (r.getStudentIds() != null && !r.getStudentIds().isEmpty()) {
            return r.getStudentIds();
        }
        return new ArrayList<>();
    }

    private List<Long> resolveUpdateStudentIds(TaskUpdateRequest r) {
        if (r.getClassId() != null) {
            return sysUserRepository.findByClassId(r.getClassId()).stream().map(SysUser::getId).collect(Collectors.toList());
        }
        if (r.getStudentIds() != null && !r.getStudentIds().isEmpty()) {
            return r.getStudentIds();
        }
        return new ArrayList<>();
    }

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
