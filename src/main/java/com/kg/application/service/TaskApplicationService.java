package com.kg.application.service;

import com.kg.context.UserContext;
import com.kg.domain.model.SysUser;
import com.kg.domain.model.ClassInfo;
import com.kg.domain.model.Task;
import com.kg.domain.model.TaskTemplate;
import com.kg.domain.model.TaskUser;
import com.kg.domain.repository.ClassInfoRepository;
import com.kg.domain.repository.SysUserRepository;
import com.kg.domain.repository.TaskRepository;
import com.kg.domain.repository.TaskScoreRepository;
import com.kg.domain.repository.TaskTemplateRepository;
import com.kg.domain.repository.TaskUserRepository;
import com.kg.enums.RoleEnum;
import com.kg.enums.TaskStatusEnum;
import com.kg.exception.BusinessException;
import com.kg.interfaces.dto.ClassOptionVO;
import com.kg.interfaces.dto.StudentOptionVO;
import com.kg.interfaces.dto.TaskCreateRequest;
import com.kg.interfaces.dto.TaskPageRequest;
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
 * 任务管理应用服务 —— 角色权限：teacher/headmaster 全部、student 本人。
 */
@Service
public class TaskApplicationService {

    private static final Logger log = LoggerFactory.getLogger(TaskApplicationService.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TaskRepository taskRepository;
    private final TaskUserRepository taskUserRepository;
    private final TaskScoreRepository taskScoreRepository;
    private final TaskTemplateRepository taskTemplateRepository;
    private final ClassInfoRepository classInfoRepository;
    private final SysUserRepository sysUserRepository;
    private final NotificationService notificationService;
    private final TaskPlanApplicationService taskPlanAppService;

    public TaskApplicationService(TaskRepository taskRepository,
                                  TaskUserRepository taskUserRepository,
                                  TaskScoreRepository taskScoreRepository,
                                  TaskTemplateRepository taskTemplateRepository,
                                  ClassInfoRepository classInfoRepository,
                                  SysUserRepository sysUserRepository,
                                  NotificationService notificationService,
                                  TaskPlanApplicationService taskPlanAppService) {
        this.taskRepository = taskRepository;
        this.taskUserRepository = taskUserRepository;
        this.taskScoreRepository = taskScoreRepository;
        this.taskTemplateRepository = taskTemplateRepository;
        this.classInfoRepository = classInfoRepository;
        this.sysUserRepository = sysUserRepository;
        this.notificationService = notificationService;
        this.taskPlanAppService = taskPlanAppService;
    }

    // ======================== 分页查询 ========================

    /**
     * 分页查询任务。
     * teacher/headmaster → 全部任务 | student → 分配给我的任务
     */
    public Map<String, Object> page(TaskPageRequest req) {
        SysUser currentUser = requireCurrentUser();
        String role = currentUser.getRole();
        int offset = (req.getPageNum() - 1) * req.getPageSize();

        List<Task> tasks;
        long total;

        if (RoleEnum.isStudent(role)) {
            // 学生：查我分配的任务
            List<TaskUser> myTaskUsers = taskUserRepository.findByUserId(currentUser.getId());
            List<Long> taskIds = myTaskUsers.stream().map(TaskUser::getTaskId).distinct().collect(Collectors.toList());
            if (taskIds.isEmpty()) {
                return emptyPageResult();
            }
            tasks = taskRepository.pageByIds(taskIds, offset, req.getPageSize());
            total = taskRepository.countByIds(taskIds);
        } else if (RoleEnum.isHeadmaster(role)) {
            // 班长：(target_type=1 AND FIND_IN_SET(classId,target_ids)) OR create_by=userId
            Long classId = currentUser.getClassId();
            if (classId == null) {
                return emptyPageResult();
            }
            tasks = taskRepository.pageByHeadmaster(classId, currentUser.getId(), offset, req.getPageSize());
            total = taskRepository.countByHeadmaster(classId, currentUser.getId());
        } else {
            // 老师：全部 + 过滤
            tasks = taskRepository.pageWithFilters(req.getTaskType(), req.getTaskName(), req.getIsMandatory(),
                    req.getStartTimeBegin(), req.getStartTimeEnd(), req.getEndTimeBegin(), req.getEndTimeEnd(),
                    offset, req.getPageSize());
            total = taskRepository.countWithFilters(req.getTaskType(), req.getTaskName(), req.getIsMandatory(),
                    req.getStartTimeBegin(), req.getStartTimeEnd(), req.getEndTimeBegin(), req.getEndTimeEnd());
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
            populateSelectedLists(t, vo);
            return vo;
        }).collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", total);
        result.put("list", list);
        return result;
    }

    // ======================== 详情 ========================

    /** 任务详情（含完成统计） */
    public TaskVO getById(Long id) {
        Task t = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("任务不存在"));
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
        // 分配快照回显
        populateSelectedLists(t, vo);
        return vo;
    }

    /** 根据 target_type/target_ids 回显选中的班级/学生 */
    private void populateSelectedLists(Task t, TaskVO vo) {
        if (t.getTargetType() == null || t.getTargetIds() == null || t.getTargetIds().isEmpty()) return;
        String[] idArr = t.getTargetIds().split(",");
        if (t.getTargetType() == 1) {
            List<ClassOptionVO> classes = new ArrayList<>();
            for (String s : idArr) {
                try {
                    Long cid = Long.valueOf(s.trim());
                    classInfoRepository.findById(cid).ifPresent(c ->
                        classes.add(ClassOptionVO.of(c.getId(), c.getClassName())));
                } catch (NumberFormatException ignored) {}
            }
            vo.setSelectedClassList(classes);
        } else if (t.getTargetType() == 2) {
            List<StudentOptionVO> students = new ArrayList<>();
            for (String s : idArr) {
                try {
                    Long uid = Long.valueOf(s.trim());
                    sysUserRepository.findById(uid).ifPresent(u ->
                        students.add(StudentOptionVO.of(u.getId(), u.getName(), u.getClassId())));
                } catch (NumberFormatException ignored) {}
            }
            vo.setSelectedStudentList(students);
        }
    }

    // ======================== 新增（teacher/headmaster） ========================

    @Transactional(rollbackFor = Exception.class)
    public void create(TaskCreateRequest request) {
        Long currentUserId = requireNotStudent();
        // 班长权限校验：只能操作本班班级或本班学生
        SysUser currentUser = requireCurrentUser();
        if (RoleEnum.isHeadmaster(currentUser.getRole())) {
            validateHeadmasterScope(currentUser, request.getClassIds(), request.getStudentIds());
        }
        // 重复任务模式 → 委托给 TaskPlanApplicationService
        if ("REPEAT".equalsIgnoreCase(request.getCreateMode())) {
            taskPlanAppService.createPlan(request, currentUserId, currentUser);
            return;
        }
        Task task = buildTask(request, currentUserId);
        taskRepository.save(task);
        Long taskId = task.getId();
        log.info("创建任务成功: id={}, taskName={}", taskId, request.getTaskName());

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
        // 生成通知消息
        notificationService.regenerateForTask(task);
    }

    // ======================== 编辑（teacher/headmaster） ========================

    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, TaskUpdateRequest request) {
        Long currentUserId = requireNotStudent();
        // 班长权限校验：只能操作本班班级或本班学生
        SysUser currentUser = requireCurrentUser();
        if (RoleEnum.isHeadmaster(currentUser.getRole())) {
            validateHeadmasterScope(currentUser, request.getClassIds(), request.getStudentIds());
        }
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
        // 重新生成通知消息（内部会先删除旧未触发消息）
        notificationService.regenerateForTask(taskRepository.findById(id).orElse(null));
        log.info("编辑任务成功: id={}", id);
    }

    // ======================== 删除（teacher/headmaster） ========================

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        requireNotStudent();
        taskScoreRepository.deleteByTaskId(id);
        taskUserRepository.deleteByTaskId(id);
        taskRepository.deleteById(id);
        log.info("删除任务成功: id={}", id);
    }

    // ======================== 权限校验 ========================

    private SysUser requireCurrentUser() {
        SysUser user = UserContext.getUser();
        if (user == null) throw new BusinessException(401, "未登录");
        return user;
    }

    /** teacher + headmaster 可操作，student 无权限 */
    private Long requireNotStudent() {
        SysUser user = requireCurrentUser();
        if (RoleEnum.isStudent(user.getRole())) {
            throw new BusinessException(403, "学生无权限");
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
        t.setTemplateId(r.getTemplateId());
        t.setIsMandatory(r.getIsMandatory());
        t.setTaskDescription(r.getTaskDescription());
        t.setTaskStartTime(parseDateTime(r.getTaskStartTime()));
        t.setTaskEndTime(parseDateTime(r.getTaskEndTime()));
        t.setPriority(r.getPriority());
        t.setCreateBy(createBy);
        // 分配快照：记录创建时的选择范围
        if (r.getClassIds() != null && !r.getClassIds().isEmpty()) {
            t.setTargetType(1);
            t.setTargetIds(r.getClassIds().stream().map(String::valueOf).collect(Collectors.joining(",")));
        } else if (r.getStudentIds() != null && !r.getStudentIds().isEmpty()) {
            t.setTargetType(2);
            t.setTargetIds(r.getStudentIds().stream().map(String::valueOf).collect(Collectors.joining(",")));
        }
        t.setCreateTime(LocalDateTime.now());
        // 模板模式：useTemplate=1 → 根据 templateId 查模板，自动填充字段 + 计算轮次
        if (r.getUseTemplate() != null && r.getUseTemplate() == 1 && r.getTemplateId() != null) {
            TaskTemplate tmpl = taskTemplateRepository.findById(r.getTemplateId())
                    .orElseThrow(() -> new BusinessException("模板不存在"));
            t.setTaskType(tmpl.getTaskType());
            int nextRound = taskRepository.maxRoundNoByTemplateId(r.getTemplateId()) + 1;
            t.setRoundNo(nextRound);
            t.setTaskName(tmpl.getTemplateName() + " 第" + nextRound + "轮");
            if (t.getIsMandatory() == null) t.setIsMandatory(tmpl.getIsMandatory());
            if (t.getTaskDescription() == null) t.setTaskDescription(tmpl.getTaskDescription());
            if (t.getPriority() == null) t.setPriority(tmpl.getDefaultPriority());
        } else {
            // 普通模式
            t.setTaskName(r.getTaskName());
            t.setTaskType(r.getTaskType());
            t.setRoundNo(r.getRoundNo());
        }
        return t;
    }

    private Task buildUpdateTask(Long id, TaskUpdateRequest r, Long updateBy) {
        Task t = new Task();
        t.setId(id);
        t.setTaskName(r.getTaskName());
        t.setTaskType(r.getTaskType());
        t.setTemplateId(r.getTemplateId());
        t.setRoundNo(r.getRoundNo());
        t.setIsMandatory(r.getIsMandatory());
        t.setTaskDescription(r.getTaskDescription());
        t.setTaskStartTime(parseDateTime(r.getTaskStartTime()));
        t.setTaskEndTime(parseDateTime(r.getTaskEndTime()));
        t.setPriority(r.getPriority());
        // 分配快照
        if (r.getClassIds() != null && !r.getClassIds().isEmpty()) {
            t.setTargetType(1);
            t.setTargetIds(r.getClassIds().stream().map(String::valueOf).collect(Collectors.joining(",")));
        } else if (r.getStudentIds() != null && !r.getStudentIds().isEmpty()) {
            t.setTargetType(2);
            t.setTargetIds(r.getStudentIds().stream().map(String::valueOf).collect(Collectors.joining(",")));
        }
        t.setUpdateBy(updateBy);
        return t;
    }

    private List<Long> resolveStudentIds(TaskCreateRequest r) {
        if (r.getClassIds() != null && !r.getClassIds().isEmpty()) {
            List<Long> ids = new ArrayList<>();
            for (Long cid : r.getClassIds()) {
                ids.addAll(sysUserRepository.findByClassId(cid).stream().map(SysUser::getId).collect(Collectors.toList()));
            }
            return ids;
        }
        if (r.getStudentIds() != null && !r.getStudentIds().isEmpty()) {
            return r.getStudentIds();
        }
        return new ArrayList<>();
    }

    private List<Long> resolveUpdateStudentIds(TaskUpdateRequest r) {
        if (r.getClassIds() != null && !r.getClassIds().isEmpty()) {
            List<Long> ids = new ArrayList<>();
            for (Long cid : r.getClassIds()) {
                ids.addAll(sysUserRepository.findByClassId(cid).stream().map(SysUser::getId).collect(Collectors.toList()));
            }
            return ids;
        }
        if (r.getStudentIds() != null && !r.getStudentIds().isEmpty()) {
            return r.getStudentIds();
        }
        return new ArrayList<>();
    }

    /** 班长只能操作本班班级或本班学生，越权直接抛异常 */
    private void validateHeadmasterScope(SysUser headmaster, List<Long> classIds, List<Long> studentIds) {
        Long ownClassId = headmaster.getClassId();
        if (classIds != null && !classIds.isEmpty()) {
            if (classIds.size() != 1 || !classIds.get(0).equals(ownClassId)) {
                throw new BusinessException(403, "班长只能给自己班级布置任务");
            }
        }
        if (studentIds != null && !studentIds.isEmpty()) {
            List<SysUser> classUsers = sysUserRepository.findByClassId(ownClassId);
            List<Long> ownClassUserIds = classUsers.stream().map(SysUser::getId).collect(Collectors.toList());
            for (Long sid : studentIds) {
                if (!ownClassUserIds.contains(sid)) {
                    throw new BusinessException(403, "班长只能给本班学生布置任务");
                }
            }
        }
    }

    private TaskVO toVO(Task t) {
        TaskVO vo = new TaskVO();
        vo.setId(t.getId());
        vo.setTaskName(t.getTaskName());
        vo.setTaskType(t.getTaskType());
        vo.setTemplateId(t.getTemplateId());
        vo.setRoundNo(t.getRoundNo());
        vo.setIsMandatory(t.getIsMandatory());
        vo.setTaskDescription(t.getTaskDescription());
        vo.setTaskStartTime(t.getTaskStartTime() != null ? t.getTaskStartTime().format(FMT) : null);
        vo.setTaskEndTime(t.getTaskEndTime() != null ? t.getTaskEndTime().format(FMT) : null);
        vo.setPriority(t.getPriority());
        vo.setClassId(t.getClassId());
        vo.setTargetType(t.getTargetType());
        vo.setTargetIds(t.getTargetIds());
        vo.setPlanId(t.getPlanId());
        vo.setPlanDate(t.getPlanDate() != null ? t.getPlanDate().toString() : null);
        vo.setIsRepeatTask(t.getIsRepeatTask() != null ? t.getIsRepeatTask() : 0);
        vo.setStatus(t.getStatus());
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
