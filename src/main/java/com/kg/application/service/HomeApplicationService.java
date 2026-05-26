package com.kg.application.service;

import com.kg.context.UserContext;
import com.kg.domain.model.SysUser;
import com.kg.enums.RoleEnum;
import com.kg.exception.BusinessException;
import com.kg.infrastructure.mapper.HomeMapper;
import com.kg.interfaces.dto.HomeStatVO;
import com.kg.interfaces.dto.LowCompletedTaskVO;
import com.kg.interfaces.dto.NearEndTaskVO;
import com.kg.interfaces.dto.StudentHomeStatVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 首页统计应用服务 —— 基于聚合 SQL 提供 teacher / headmaster / student 三种视角的统计数据。
 */
@Service
public class HomeApplicationService {

    private static final Logger log = LoggerFactory.getLogger(HomeApplicationService.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final HomeMapper homeMapper;

    public HomeApplicationService(HomeMapper homeMapper) {
        this.homeMapper = homeMapper;
    }

    /**
     * teacher / headmaster 首页统计。
     */
    public HomeStatVO getStats() {
        SysUser user = requireUser();
        String role = user.getRole();

        HomeStatVO vo = new HomeStatVO();

        if (RoleEnum.isTeacher(role)) {
            fillTeacherStats(vo);
        } else if (RoleEnum.isHeadmaster(role)) {
            fillHeadmasterStats(vo, user.getId());
        } else {
            throw new BusinessException(403, "请使用学生首页接口");
        }

        return vo;
    }

    /**
     * student 首页统计。
     */
    public StudentHomeStatVO getStudentStats() {
        SysUser user = requireUser();
        if (!RoleEnum.isStudent(user.getRole())) {
            throw new BusinessException(403, "请使用老师/班长首页接口");
        }

        StudentHomeStatVO vo = new StudentHomeStatVO();
        Long userId = user.getId();

        vo.setTodoTaskCount((int) homeMapper.countTodoForStudent(userId));
        vo.setCompletedTaskCount((int) homeMapper.countCompletedForStudent(userId));

        List<NearEndTaskVO> nearEndTasks = new ArrayList<>();
        List<Map<String, Object>> rows = homeMapper.nearEndTop3ForStudent(userId);
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                NearEndTaskVO item = new NearEndTaskVO();
                item.setTaskId(toLong(row.get("task_id")));
                item.setTaskName((String) row.get("task_name"));
                item.setEndTime(formatTime(row.get("end_time")));
                nearEndTasks.add(item);
            }
        }
        vo.setNearEndTasks(nearEndTasks);

        return vo;
    }

    // ======================== teacher ========================

    private void fillTeacherStats(HomeStatVO vo) {
        vo.setCurrentTaskCount((int) homeMapper.countCurrentTasksForTeacher());
        vo.setForceTaskCount((int) homeMapper.countForceTasksForTeacher());
        vo.setTotalTaskCount((int) homeMapper.countTotalTasksForTeacher());

        Map<String, Object> stats = homeMapper.completionStatsForTeacher();
        fillCompletionStats(vo, stats);

        vo.setLowCompletedTasks(buildLowCompletedList(homeMapper.lowCompletedTop3ForTeacher()));
    }

    // ======================== headmaster ========================

    private void fillHeadmasterStats(HomeStatVO vo, Long createBy) {
        vo.setCurrentTaskCount((int) homeMapper.countCurrentTasksForHeadmaster(createBy));
        vo.setForceTaskCount((int) homeMapper.countForceTasksForHeadmaster(createBy));
        vo.setTotalTaskCount((int) homeMapper.countTotalTasksForHeadmaster(createBy));

        Map<String, Object> stats = homeMapper.completionStatsForHeadmaster(createBy);
        fillCompletionStats(vo, stats);

        vo.setLowCompletedTasks(buildLowCompletedList(homeMapper.lowCompletedTop3ForHeadmaster(createBy)));
    }

    // ======================== 公共映射 ========================

    private void fillCompletionStats(HomeStatVO vo, Map<String, Object> stats) {
        if (stats == null) return;
        long total = toLong(stats.get("total"));
        long completed = toLong(stats.get("completed"));
        long uncompleted = toLong(stats.get("uncompleted"));

        vo.setCompletedTaskUserCount((int) completed);
        vo.setUnCompletedTaskUserCount((int) uncompleted);
        if (total > 0) {
            vo.setCompletedRate((int) Math.round(completed * 100.0 / total));
        }
    }

    private List<LowCompletedTaskVO> buildLowCompletedList(List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) return Collections.emptyList();

        List<LowCompletedTaskVO> list = new ArrayList<>(rows.size());
        for (Map<String, Object> row : rows) {
            LowCompletedTaskVO item = new LowCompletedTaskVO();
            item.setTaskId(toLong(row.get("task_id")));
            item.setTaskName((String) row.get("task_name"));
            long total = toLong(row.get("total"));
            long completed = toLong(row.get("completed"));
            long uncompleted = toLong(row.get("uncompleted"));
            item.setTotalUserCount((int) total);
            item.setCompletedUserCount((int) completed);
            item.setUnCompletedUserCount((int) uncompleted);
            if (total > 0) {
                item.setCompletedRate((int) Math.round(completed * 100.0 / total));
            }
            item.setEndTime(formatTime(row.get("end_time")));
            list.add(item);
        }
        return list;
    }

    // ======================== 工具方法 ========================

    private SysUser requireUser() {
        SysUser user = UserContext.getUser();
        if (user == null) throw new BusinessException(401, "未登录");
        return user;
    }

    private long toLong(Object val) {
        if (val == null) return 0;
        if (val instanceof BigDecimal) return ((BigDecimal) val).longValue();
        if (val instanceof Number) return ((Number) val).longValue();
        return Long.parseLong(val.toString());
    }

    private String formatTime(Object val) {
        if (val == null) return null;
        if (val instanceof Timestamp) return ((Timestamp) val).toLocalDateTime().format(FMT);
        return val.toString();
    }
}
