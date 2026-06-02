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
import com.kg.infrastructure.mapper.TaskUserMapper;
import com.kg.interfaces.dto.MyTaskCompleteRequest;
import com.kg.interfaces.dto.MyTaskPageRequest;
import com.kg.interfaces.dto.MyTaskVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 我的任务应用服务 —— 个人任务列表 + 完成任务。
 */
@Service
public class MyTaskApplicationService {

    private static final Logger log = LoggerFactory.getLogger(MyTaskApplicationService.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final MyTaskMapper myTaskMapper;
    private final TaskUserMapper taskUserMapper;
    private final TaskUserRepository taskUserRepository;
    private final TaskScoreRepository taskScoreRepository;

    public MyTaskApplicationService(MyTaskMapper myTaskMapper,
                                    TaskUserMapper taskUserMapper,
                                    TaskUserRepository taskUserRepository,
                                    TaskScoreRepository taskScoreRepository) {
        this.myTaskMapper = myTaskMapper;
        this.taskUserMapper = taskUserMapper;
        this.taskUserRepository = taskUserRepository;
        this.taskScoreRepository = taskScoreRepository;
    }

    /**
     * 分页查询当前用户的任务列表，支持多条件筛选。
     */
    public Map<String, Object> page(MyTaskPageRequest req) {
        Long userId = resolveUserId(req.getUserId());
        int offset = (req.getPageNum() - 1) * req.getPageSize();

        long total = myTaskMapper.count(userId,
                req.getStatus(), req.getTaskName(), req.getTaskType(),
                req.getForceFlag(), req.getStartTimeBegin(), req.getStartTimeEnd(),
                req.getEndTimeBegin(), req.getEndTimeEnd());
        List<Map<String, Object>> rows = myTaskMapper.page(userId,
                req.getStatus(), req.getTaskName(), req.getTaskType(),
                req.getForceFlag(), req.getStartTimeBegin(), req.getStartTimeEnd(),
                req.getEndTimeBegin(), req.getEndTimeEnd(), offset, req.getPageSize());

        List<MyTaskVO> list;
        if (rows == null || rows.isEmpty()) {
            list = Collections.emptyList();
        } else {
            list = rows.stream().map(row -> {
                MyTaskVO vo = new MyTaskVO();
                vo.setTaskId(toLong(row.get("task_id")));
                vo.setTaskName((String) row.get("task_name"));
                vo.setTaskType((String) row.get("task_type"));
                vo.setForceFlag(toInt(row.get("force_flag")));
                vo.setStartTime(formatTime(row.get("start_time")));
                vo.setEndTime(formatTime(row.get("end_time")));
                vo.setStatus((String) row.get("status"));
                Object score = row.get("total_score");
                vo.setTotalScore(score instanceof BigDecimal ? (BigDecimal) score : null);
                vo.setSubmitTime(formatTime(row.get("submit_time")));
                return vo;
            }).collect(Collectors.toList());
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", total);
        result.put("list", list);
        return result;
    }

    /**
     * 当前用户完成任务。
     */
    @Transactional(rollbackFor = Exception.class)
    public void complete(MyTaskCompleteRequest request) {
        Long currentUserId = getCurrentUserId();
        Long taskId = request.getTaskId();

        TaskUser taskUser = taskUserRepository.findByTaskIdAndUserId(taskId, currentUserId);
        if (taskUser == null) {
            throw new BusinessException("未找到您的任务分配记录");
        }

        taskUser.setStatus(TaskStatusEnum.FINISHED.getCode());
        taskUser.setFinishTime(LocalDateTime.now());
        taskUser.setSubmitTime(LocalDateTime.now());
        taskUser.setDurationMinutes(request.getDurationMinutes());
        taskUser.setRemark(request.getRemark());
        taskUser.setUpdateBy(currentUserId);

        BigDecimal totalScore = request.getTotalScore();
        if (totalScore != null) {
            taskUser.setTotalScore(totalScore);
        }
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

    /**
     * 统计用户待办/已办数量。
     * @param reqUserId 可选，不传则用当前登录用户
     */
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

    // ======================== 工具方法 ========================

    /** 有入参用入参，否则用上下文用户 */
    private Long resolveUserId(Long reqUserId) {
        if (reqUserId != null) {
            // 入参指定了用户ID，只需校验登录
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
        if (RoleEnum.isTeacher(role)) {
            throw new BusinessException(403, "老师请使用任务管理模块");
        }
        if (!RoleEnum.isHeadmaster(role) && !RoleEnum.isStudent(role)) {
            throw new BusinessException(403, "无权访问");
        }
        return user.getId();
    }

    private long toLong(Object val) {
        if (val == null) return 0;
        if (val instanceof BigDecimal) return ((BigDecimal) val).longValue();
        if (val instanceof Number) return ((Number) val).longValue();
        return Long.parseLong(val.toString());
    }

    private int toInt(Object val) {
        return (int) toLong(val);
    }

    private String formatTime(Object val) {
        if (val == null) return null;
        if (val instanceof Timestamp) return ((Timestamp) val).toLocalDateTime().format(FMT);
        return val.toString();
    }
}
