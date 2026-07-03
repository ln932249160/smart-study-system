package com.kg.application.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kg.domain.model.SysUser;
import com.kg.domain.model.Task;
import com.kg.domain.model.TaskUser;
import com.kg.domain.repository.SysUserRepository;
import com.kg.domain.repository.TaskRepository;
import com.kg.domain.repository.TaskUserRepository;
import com.kg.enums.TaskStatusEnum;
import com.kg.exception.BusinessException;
import com.kg.infrastructure.entity.TaskEntity;
import com.kg.infrastructure.entity.TaskPlanEntity;
import com.kg.infrastructure.mapper.TaskMapper;
import com.kg.infrastructure.mapper.TaskPlanMapper;
import com.kg.interfaces.dto.TaskCreateRequest;
import com.kg.context.UserContext;
import com.kg.enums.RoleEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/* 重复任务计划服务 —— 日期生成 + 任务批量创建 */
@Service
public class TaskPlanApplicationService {
    private static final Logger log = LoggerFactory.getLogger(TaskPlanApplicationService.class);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final TaskPlanMapper planMapper;
    private final TaskMapper taskMapper;
    private final TaskRepository taskRepo;
    private final TaskUserRepository taskUserRepo;
    private final SysUserRepository sysUserRepo;

    public TaskPlanApplicationService(TaskPlanMapper planMapper, TaskMapper taskMapper,
                                       TaskRepository taskRepo, TaskUserRepository taskUserRepo,
                                       SysUserRepository sysUserRepo) {
        this.planMapper = planMapper;
        this.taskMapper = taskMapper;
        this.taskRepo = taskRepo;
        this.taskUserRepo = taskUserRepo;
        this.sysUserRepo = sysUserRepo;
    }

    // ======================== 日期生成 ========================

    /** 根据计划生成日期列表 */
    public static List<LocalDate> generatePlanDates(TaskPlanEntity plan) {
        switch (plan.getRepeatType()) {
            case "DAILY": return generateDailyDates(plan);
            case "WEEKLY": return generateWeeklyDates(plan);
            default: throw new BusinessException("当前暂不支持该重复类型: " + plan.getRepeatType());
        }
    }

    private static List<LocalDate> generateDailyDates(TaskPlanEntity plan) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate d = plan.getStartDate();
        while (!d.isAfter(plan.getEndDate())) {
            dates.add(d);
            d = d.plusDays(1);
        }
        return dates;
    }

    private static List<LocalDate> generateWeeklyDates(TaskPlanEntity plan) {
        List<LocalDate> dates = new ArrayList<>();
        String cfg = plan.getRepeatConfig();
        if (cfg == null || cfg.isEmpty()) throw new BusinessException("WEEKLY 必须提供 weekDays 配置");
        // 解析 {"weekDays":[2,4,6]}
        List<Integer> weekDays = parseWeekDays(cfg);
        LocalDate d = plan.getStartDate();
        while (!d.isAfter(plan.getEndDate())) {
            int dow = d.getDayOfWeek().getValue(); // 1=Mon ... 7=Sun
            if (weekDays.contains(dow)) dates.add(d);
            d = d.plusDays(1);
        }
        return dates;
    }

    private static List<Integer> parseWeekDays(String json) {
        // simple parser: {"weekDays":[2,4,6]}
        List<Integer> list = new ArrayList<>();
        int start = json.indexOf('[');
        int end = json.indexOf(']');
        if (start < 0 || end < 0) throw new BusinessException("repeatConfig 格式错误，需要 weekDays 数组");
        String nums = json.substring(start + 1, end);
        for (String s : nums.split(",")) {
            list.add(Integer.parseInt(s.trim()));
        }
        return list;
    }

    // ======================== 创建重复任务计划 ========================

    @Transactional(rollbackFor = Exception.class)
    public Long createPlan(TaskCreateRequest req, Long currentUserId, SysUser currentUser) {
        // 1. 保存 task_plan
        TaskPlanEntity plan = new TaskPlanEntity();
        plan.setPlanName(req.getTaskName());
        plan.setTaskType(req.getTaskType());
        plan.setTemplateId(req.getTemplateId());
        plan.setIsMandatory(req.getIsMandatory());
        plan.setTaskDescription(req.getTaskDescription());
        plan.setPriority(req.getPriority());
        plan.setRepeatType(req.getRepeatType());
        plan.setRepeatConfig(req.getRepeatConfig());
        plan.setStartDate(LocalDate.parse(req.getStartDate(), DATE_FMT));
        plan.setEndDate(LocalDate.parse(req.getEndDate(), DATE_FMT));
        // 分配信息
        if (req.getClassIds() != null && !req.getClassIds().isEmpty()) {
            plan.setTargetType(1);
            plan.setTargetIds(req.getClassIds().stream().map(String::valueOf).collect(Collectors.joining(",")));
        } else if (req.getStudentIds() != null && !req.getStudentIds().isEmpty()) {
            plan.setTargetType(2);
            plan.setTargetIds(req.getStudentIds().stream().map(String::valueOf).collect(Collectors.joining(",")));
        }
        plan.setGeneratedCount(0);
        plan.setStatus(1);
        plan.setCreateBy(currentUserId);
        plan.setCreateTime(LocalDateTime.now());
        planMapper.insert(plan);
        Long planId = plan.getId();

        // 2. 生成日期
        List<LocalDate> dates = generatePlanDates(plan);
        if (dates.isEmpty()) throw new BusinessException("计划日期范围内没有符合条件的日期");

        // 3. 每个日期生成一条 task + task_user
        List<Long> userIds = resolveUserIds(req);
        for (LocalDate date : dates) {
            Task task = buildRepeatTask(plan, req, currentUserId, date);
            taskRepo.save(task);
            // 分发 task_user
            if (userIds != null && !userIds.isEmpty()) {
                List<TaskUser> tus = new ArrayList<>();
                for (Long uid : userIds) {
                    TaskUser tu = new TaskUser();
                    tu.setTaskId(task.getId());
                    tu.setUserId(uid);
                    tu.setStatus(TaskStatusEnum.UNFINISHED.getCode());
                    tu.setCreateBy(currentUserId);
                    tus.add(tu);
                }
                taskUserRepo.batchSave(tus);
            }
        }
        // 4. 更新计数
        plan.setGeneratedCount(dates.size());
        planMapper.updateById(plan);
        log.info("创建重复任务计划: planId={}, 生成{}条task", planId, dates.size());
        return planId;
    }

    private Task buildRepeatTask(TaskPlanEntity plan, TaskCreateRequest req, Long createBy, LocalDate date) {
        Task t = new Task();
        t.setPlanId(plan.getId());
        t.setPlanDate(date);
        t.setIsRepeatTask(1);
        t.setStatus(1);
        String dayStr = date.format(DateTimeFormatter.ofPattern("MM-dd"));
        t.setTaskName(plan.getPlanName() + " " + dayStr);
        t.setTaskType(plan.getTaskType());
        t.setTemplateId(plan.getTemplateId());
        if (plan.getPriority() != null) t.setPriority(plan.getPriority());
        else t.setPriority(0);
        t.setIsMandatory(plan.getIsMandatory() != null ? plan.getIsMandatory() : 0);
        t.setTaskDescription(plan.getTaskDescription());
        t.setTaskStartTime(LocalDateTime.of(date, LocalTime.of(0, 0)));
        t.setTaskEndTime(LocalDateTime.of(date, LocalTime.of(23, 59)));
        // 分配快照
        if (plan.getTargetType() != null) t.setTargetType(plan.getTargetType());
        if (plan.getTargetIds() != null) t.setTargetIds(plan.getTargetIds());
        t.setCreateBy(createBy);
        t.setCreateTime(LocalDateTime.now());
        return t;
    }

    private List<Long> resolveUserIds(TaskCreateRequest r) {
        if (r.getClassIds() != null && !r.getClassIds().isEmpty()) {
            List<Long> ids = new ArrayList<>();
            for (Long cid : r.getClassIds())
                ids.addAll(sysUserRepo.findByClassId(cid).stream().map(SysUser::getId).collect(Collectors.toList()));
            return ids;
        }
        if (r.getStudentIds() != null && !r.getStudentIds().isEmpty()) return r.getStudentIds();
        return new ArrayList<>();
    }

    // ======================== 详情 ========================

    public TaskPlanEntity getDetail(Long planId) {
        TaskPlanEntity plan = planMapper.selectById(planId);
        if (plan == null) throw new BusinessException("计划不存在");
        return plan;
    }

    /** 查询计划下已生成的任务列表 */
    public List<TaskEntity> getPlanTasks(Long planId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<TaskEntity> q =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        q.eq(TaskEntity::getPlanId, planId);
        q.orderByAsc(TaskEntity::getPlanDate);
        return taskMapper.selectList(q);
    }

    // ======================== 停止计划 ========================

    @Transactional(rollbackFor = Exception.class)
    public void stop(Long planId) {
        TaskPlanEntity plan = planMapper.selectById(planId);
        if (plan == null) throw new BusinessException("计划不存在");
        // 更新计划状态
        plan.setStatus(0);
        planMapper.updateById(plan);
        // 禁用未来未开始的任务
        LambdaUpdateWrapper<TaskEntity> uw = new LambdaUpdateWrapper<>();
        uw.eq(TaskEntity::getPlanId, planId);
        uw.eq(TaskEntity::getStatus, 1);
        uw.gt(TaskEntity::getPlanDate, LocalDate.now());
        uw.set(TaskEntity::getStatus, 0);
        taskMapper.update(null, uw);
        log.info("停止计划: planId={}", planId);
    }
}
