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
import com.kg.infrastructure.mapper.MyTaskMapper;
import com.kg.interfaces.dto.MyTaskCompleteRequest;
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
    private final TaskUserRepository taskUserRepository;
    private final TaskScoreRepository taskScoreRepository;

    public MyTaskApplicationService(MyTaskMapper myTaskMapper,
                                    TaskUserRepository taskUserRepository,
                                    TaskScoreRepository taskScoreRepository) {
        this.myTaskMapper = myTaskMapper;
        this.taskUserRepository = taskUserRepository;
        this.taskScoreRepository = taskScoreRepository;
    }

    /**
     * 分页查询当前用户的任务列表。
     *
     * @param status   状态过滤：0未完成 1已完成，null 查全部
     * @param pageNum  页码
     * @param pageSize 每页条数
     */
    public Map<String, Object> page(String status, int pageNum, int pageSize) {
        Long userId = getCurrentUserId();
        int offset = (pageNum - 1) * pageSize;

        long total = myTaskMapper.count(userId, status);
        List<Map<String, Object>> rows = myTaskMapper.page(userId, status, offset, pageSize);

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

    // ======================== 工具方法 ========================

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
