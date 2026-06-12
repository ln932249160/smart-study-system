package com.kg.application.service;

import com.kg.context.UserContext;
import com.kg.domain.model.SysUser;
import com.kg.domain.model.TaskScore;
import com.kg.domain.model.TaskUser;
import com.kg.domain.repository.TaskScoreRepository;
import com.kg.domain.repository.TaskUserRepository;
import com.kg.enums.RoleEnum;
import com.kg.enums.TaskStatusEnum;
import com.kg.exception.BusinessException;
import com.kg.infrastructure.entity.TaskUserEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kg.infrastructure.mapper.MyTaskMapper;
import com.kg.infrastructure.mapper.TaskTemplateMapper;
import com.kg.infrastructure.mapper.TaskUserMapper;
import com.kg.interfaces.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 我的任务应用服务 —— 个人任务列表 + 完成任务 + 详情 + 修改 + 模板统计。
 */
@Service
public class MyTaskApplicationService {

    private static final Logger log = LoggerFactory.getLogger(MyTaskApplicationService.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final MyTaskMapper myTaskMapper;
    private final TaskUserMapper taskUserMapper;
    private final TaskTemplateMapper taskTemplateMapper;
    private final TaskUserRepository taskUserRepository;
    private final TaskScoreRepository taskScoreRepository;

    public MyTaskApplicationService(MyTaskMapper myTaskMapper,
                                    TaskUserMapper taskUserMapper,
                                    TaskTemplateMapper taskTemplateMapper,
                                    TaskUserRepository taskUserRepository,
                                    TaskScoreRepository taskScoreRepository) {
        this.myTaskMapper = myTaskMapper;
        this.taskUserMapper = taskUserMapper;
        this.taskTemplateMapper = taskTemplateMapper;
        this.taskUserRepository = taskUserRepository;
        this.taskScoreRepository = taskScoreRepository;
    }

    // ======================== 任务列表 ========================

    /** 分页查询，增加 taskDescription / priority / finishTime 筛选，排序改为 priority DESC + end_time ASC */
    public Map<String, Object> page(MyTaskPageRequest req) {
        Long userId = resolveUserId(req.getUserId());
        int offset = (req.getPageNum() - 1) * req.getPageSize();

        long total = myTaskMapper.count(userId,
                req.getStatus(), req.getTaskName(), req.getTaskType(),
                req.getForceFlag(), req.getStartTimeBegin(), req.getStartTimeEnd(),
                req.getEndTimeBegin(), req.getEndTimeEnd(),
                req.getFinishTimeStart(), req.getFinishTimeEnd(),
                req.getIsTemplate());
        List<Map<String, Object>> rows = myTaskMapper.page(userId,
                req.getStatus(), req.getTaskName(), req.getTaskType(),
                req.getForceFlag(), req.getStartTimeBegin(), req.getStartTimeEnd(),
                req.getEndTimeBegin(), req.getEndTimeEnd(),
                req.getFinishTimeStart(), req.getFinishTimeEnd(),
                req.getIsTemplate(),
                offset, req.getPageSize());

        List<MyTaskVO> list = rows == null || rows.isEmpty() ? Collections.emptyList()
                : rows.stream().map(row -> {
            MyTaskVO vo = new MyTaskVO();
            vo.setTaskId(toLong(row.get("task_id")));
            vo.setTaskUserId(toLong(row.get("task_user_id")));
            vo.setTaskName((String) row.get("task_name"));
            vo.setTaskType((String) row.get("task_type"));
            vo.setForceFlag(toInt(row.get("force_flag")));
            vo.setTaskDescription((String) row.get("task_description"));
            Object prio = row.get("priority");
            vo.setPriority(prio instanceof Number ? ((Number) prio).intValue() : null);
            vo.setStartTime(formatTime(row.get("start_time")));
            vo.setEndTime(formatTime(row.get("end_time")));
            vo.setStatus((String) row.get("status"));
            vo.setTaskCreateName((String) row.get("task_create_name"));
            vo.setRoundNo((Integer) row.get("round_no"));
            Object score = row.get("total_score");
            vo.setTotalScore(score instanceof BigDecimal ? (BigDecimal) score : null);
            vo.setSubmitTime(formatTime(row.get("submit_time")));
            return vo;
        }).collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", total);
        result.put("list", list);
        return result;
    }

    // ======================== 任务详情 ========================

    /** 查询任务完整信息（task + task_user + task_score）。老师可指定 userId 查看学生详情。 */
    public MyTaskDetailVO getDetail(Long taskUserId, Long reqUserId) {
        Long userId = resolveUserId(reqUserId);
        Map<String, Object> row = myTaskMapper.findDetail(taskUserId, userId);
        if (row == null) throw new BusinessException("任务记录不存在");

        MyTaskDetailVO vo = new MyTaskDetailVO();
        vo.setTaskId(toLong(row.get("task_id")));
        vo.setTaskName((String) row.get("task_name"));
        vo.setTaskType((String) row.get("task_type"));
        vo.setTaskDescription((String) row.get("task_description"));
        Object prio = row.get("priority");
        vo.setPriority(prio instanceof Number ? ((Number) prio).intValue() : null);
        Object rn = row.get("round_no");
        vo.setRoundNo(rn instanceof Number ? ((Number) rn).intValue() : null);
        vo.setTaskStartTime(formatTime(row.get("task_start_time")));
        vo.setTaskEndTime(formatTime(row.get("task_end_time")));
        vo.setTaskUserId(toLong(row.get("task_user_id")));
        vo.setStatus((String) row.get("status"));
        vo.setStartTime(formatTime(row.get("start_time")));
        vo.setFinishTime(formatTime(row.get("finish_time")));
        Object dm = row.get("duration_minutes");
        vo.setDurationMinutes(dm instanceof Number ? ((Number) dm).intValue() : null);
        vo.setRemark((String) row.get("remark"));
        Object ts = row.get("total_score");
        vo.setTotalScore(ts instanceof BigDecimal ? (BigDecimal) ts : null);
        vo.setSubmitTime(formatTime(row.get("submit_time")));

        // 成绩明细
        List<TaskScore> scores = taskScoreRepository.findByTaskUserId(taskUserId);
        if (scores != null && !scores.isEmpty()) {
            vo.setScores(scores.stream().map(s -> {
                MyTaskDetailVO.ScoreItem si = new MyTaskDetailVO.ScoreItem();
                si.setModuleName(s.getModuleName());
                si.setScore(s.getScore());
                return si;
            }).collect(Collectors.toList()));
        }
        return vo;
    }

    // ======================== 完成任务 ========================

    @Transactional(rollbackFor = Exception.class)
    public void complete(MyTaskCompleteRequest request) {
        Long currentUserId = getCurrentUserId();
        Long taskId = request.getTaskId();

        TaskUser taskUser = taskUserRepository.findByTaskIdAndUserId(taskId, currentUserId);
        if (taskUser == null) throw new BusinessException("未找到您的任务分配记录");

        taskUser.setStatus(TaskStatusEnum.FINISHED.getCode());
        taskUser.setFinishTime(LocalDateTime.now());
        taskUser.setSubmitTime(LocalDateTime.now());
        taskUser.setDurationMinutes(request.getDurationMinutes());
        taskUser.setRemark(request.getRemark());
        taskUser.setUpdateBy(currentUserId);

        BigDecimal totalScore = request.getTotalScore();
        if (totalScore != null) taskUser.setTotalScore(totalScore);
        taskUserRepository.update(taskUser);

        List<MyTaskCompleteRequest.ScoreItem> scores = request.getScores();
        if (scores != null && !scores.isEmpty()) {
            List<TaskScore> scoreList = new ArrayList<>();
            for (MyTaskCompleteRequest.ScoreItem item : scores) {
                TaskScore ts = new TaskScore();
                ts.setTaskUserId(taskUser.getId());
                ts.setModuleName(item.getModuleName());
                ts.setScore(item.getScore());
                ts.setCreateBy(currentUserId);
                scoreList.add(ts);
            }
            taskScoreRepository.batchSave(scoreList);

            if (totalScore == null) {
                BigDecimal sum = BigDecimal.ZERO;
                for (TaskScore ts : scoreList) {
                    if (ts.getScore() != null) sum = sum.add(ts.getScore());
                }
                taskUser.setTotalScore(sum);
                taskUserRepository.update(taskUser);
            }
        }
        log.info("完成任务成功: taskId={}, userId={}", taskId, currentUserId);
    }

    // ======================== 修改已完成任务 ========================

    /** 仅允许修改 remark / totalScore / 模块成绩 */
    @Transactional(rollbackFor = Exception.class)
    public void updateMyTask(MyTaskUpdateRequest req) {
        Long currentUserId = getCurrentUserId();
        Long taskUserId = req.getTaskUserId();

        // 校验归属
        LambdaQueryWrapper<TaskUserEntity> q = new LambdaQueryWrapper<>();
        q.eq(TaskUserEntity::getId, taskUserId);
        q.eq(TaskUserEntity::getUserId, currentUserId);
        TaskUserEntity entity = taskUserMapper.selectOne(q);
        if (entity == null) throw new BusinessException("未找到您的任务记录");

        // 更新 task_user
        com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<TaskUserEntity> uw =
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<>();
        uw.eq(TaskUserEntity::getId, taskUserId);
        if (req.getRemark() != null) uw.set(TaskUserEntity::getRemark, req.getRemark());
        if (req.getTotalScore() != null) uw.set(TaskUserEntity::getTotalScore, req.getTotalScore());
        taskUserMapper.update(null, uw);

        // 更新模块成绩：先删后插
        if (req.getScores() != null) {
            taskScoreRepository.deleteByTaskUserId(taskUserId);
            if (!req.getScores().isEmpty()) {
                List<TaskScore> scoreList = new ArrayList<>();
                for (MyTaskUpdateRequest.ScoreItem item : req.getScores()) {
                    TaskScore ts = new TaskScore();
                    ts.setTaskUserId(taskUserId);
                    ts.setModuleName(item.getModuleName());
                    ts.setScore(item.getScore());
                    ts.setCreateBy(currentUserId);
                    scoreList.add(ts);
                }
                taskScoreRepository.batchSave(scoreList);
            }
        }

        log.info("修改任务成功: taskUserId={}", taskUserId);
    }

    // ======================== 待办/已办统计 ========================

    public Map<String, Object> stats(Long reqUserId) {
        Long userId = resolveUserId(reqUserId);
        LambdaQueryWrapper<TaskUserEntity> q = new LambdaQueryWrapper<>();
        q.eq(TaskUserEntity::getUserId, userId);
        q.eq(TaskUserEntity::getStatus, TaskStatusEnum.UNFINISHED.getCode());
        long unfinished = taskUserMapper.selectCount(q);

        LambdaQueryWrapper<TaskUserEntity> q2 = new LambdaQueryWrapper<>();
        q2.eq(TaskUserEntity::getUserId, userId);
        q2.eq(TaskUserEntity::getStatus, TaskStatusEnum.FINISHED.getCode());
        long finished = taskUserMapper.selectCount(q2);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("unfinished", unfinished);
        result.put("finished", finished);
        return result;
    }

    // ======================== 我的模板任务 ========================

    /** 分页查询用户做过的模板任务训练记录（汇总统计） */
    public Map<String, Object> pageTemplates(MyTaskTemplatePageRequest req) {
        Long userId = resolveUserId(req.getUserId());
        int offset = (req.getPageNum() - 1) * req.getPageSize();

        long total = myTaskMapper.countTemplates(userId, req.getTemplateName(), req.getTaskType(), req.getIsMandatory());
        List<Map<String, Object>> rows = myTaskMapper.pageTemplates(userId, req.getTemplateName(), req.getTaskType(), req.getIsMandatory(), offset, req.getPageSize());

        List<MyTaskTemplateVO> list = rows == null || rows.isEmpty() ? Collections.emptyList()
                : rows.stream().map(row -> {
            MyTaskTemplateVO vo = new MyTaskTemplateVO();
            vo.setTemplateId(toLong(row.get("template_id")));
            vo.setTemplateName((String) row.get("template_name"));
            vo.setTaskType((String) row.get("task_type"));
            Object rc = row.get("round_count");
            vo.setRoundCount(rc instanceof Number ? ((Number) rc).intValue() : 0);
            Object max = row.get("max_score");
            vo.setMaxScore(max instanceof BigDecimal ? (BigDecimal) max : null);
            Object min = row.get("min_score");
            vo.setMinScore(min instanceof BigDecimal ? (BigDecimal) min : null);
            Object avg = row.get("avg_score");
            vo.setAvgScore(avg instanceof BigDecimal ? (BigDecimal) avg : null);
            Object latest = row.get("latest_score");
            vo.setLatestScore(latest instanceof BigDecimal ? (BigDecimal) latest : null);
            return vo;
        }).collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", total);
        result.put("list", list);
        return result;
    }

    /** 模板任务详情：模板信息 + 各轮次记录 + 成绩明细 */
    public MyTaskTemplateDetailVO getTemplateDetail(Long templateId, Long reqUserId) {
        Long userId = resolveUserId(reqUserId);

        // 模板信息：从 task_template 表查
        List<Map<String, Object>> rounds = myTaskMapper.findTemplateRounds(templateId, userId);
        if (rounds == null || rounds.isEmpty()) throw new BusinessException("没有找到该模板的训练记录");

        // 读第一条取模板名等信息（通过 task 表也能反查，这里走 task 第一条的 task_name 前缀）
        // 实际模板信息从 task_template 表查
        MyTaskTemplateDetailVO vo = new MyTaskTemplateDetailVO();
        vo.setTemplateId(templateId);

        // 查模板信息
        com.kg.infrastructure.entity.TaskTemplateEntity tpl =
                taskTemplateMapper.selectById(templateId);
        if (tpl == null) {
            // 兼容：从 task 记录也能推断
            Map<String, Object> firstRow = rounds.get(0);
            vo.setTemplateName((String) firstRow.get("task_name"));
            vo.setTaskType(null);
            vo.setTaskDescription(null);
        } else {
            vo.setTemplateName(tpl.getTemplateName());
            vo.setTaskType(tpl.getTaskType());
            vo.setTaskDescription(tpl.getTaskDescription());
        }

        List<MyTaskTemplateDetailVO.RoundRecord> records = rounds.stream().map(row -> {
            MyTaskTemplateDetailVO.RoundRecord rr = new MyTaskTemplateDetailVO.RoundRecord();
            rr.setTaskId(toLong(row.get("task_id")));
            rr.setTaskName((String) row.get("task_name"));
            Object rn = row.get("round_no");
            rr.setRoundNo(rn instanceof Number ? ((Number) rn).intValue() : null);
            rr.setTaskStartTime(formatTime(row.get("task_start_time")));
            rr.setTaskEndTime(formatTime(row.get("task_end_time")));
            rr.setStatus((String) row.get("status"));
            Object ts = row.get("total_score");
            rr.setTotalScore(ts instanceof BigDecimal ? (BigDecimal) ts : null);
            rr.setSubmitTime(formatTime(row.get("submit_time")));

            // 每轮成绩明细
            Long tuId = toLong(row.get("task_user_id"));
            if (tuId != null) {
                List<TaskScore> scores = taskScoreRepository.findByTaskUserId(tuId);
                if (scores != null && !scores.isEmpty()) {
                    rr.setScores(scores.stream().map(s -> {
                        MyTaskTemplateDetailVO.ScoreItem si = new MyTaskTemplateDetailVO.ScoreItem();
                        si.setModuleName(s.getModuleName());
                        si.setScore(s.getScore());
                        return si;
                    }).collect(Collectors.toList()));
                }
            }
            return rr;
        }).collect(Collectors.toList());
        vo.setRecords(records);

        return vo;
    }

    // ======================== 工具方法 ========================

    private Long resolveUserId(Long reqUserId) {
        if (reqUserId != null) {
            SysUser user = UserContext.getUser();
            if (user == null) throw new BusinessException(401, "未登录");
            return reqUserId;
        }
        return getCurrentUserId();
    }

    private Long getCurrentUserId() {
        SysUser user = UserContext.getUser();
        if (user == null) throw new BusinessException(401, "未登录");
        String role = user.getRole();
        if (RoleEnum.isTeacher(role)) throw new BusinessException(403, "老师请使用任务管理模块");
        if (!RoleEnum.isHeadmaster(role) && !RoleEnum.isStudent(role)) throw new BusinessException(403, "无权访问");
        return user.getId();
    }

    private Long toLong(Object val) {
        if (val == null) return null;
        if (val instanceof BigDecimal) return ((BigDecimal) val).longValue();
        if (val instanceof Number) return ((Number) val).longValue();
        return Long.parseLong(val.toString());
    }

    private int toInt(Object val) {
        if (val == null) return 0;
        return ((Number) val).intValue();
    }

    private String formatTime(Object val) {
        if (val == null) return null;
        if (val instanceof Timestamp) return ((Timestamp) val).toLocalDateTime().format(FMT);
        if (val instanceof LocalDateTime) return ((LocalDateTime) val).format(FMT);
        return val.toString();
    }
}
