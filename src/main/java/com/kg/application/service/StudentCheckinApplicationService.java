package com.kg.application.service;

import com.kg.context.UserContext;
import com.kg.domain.model.SysUser;
import com.kg.exception.BusinessException;
import com.kg.infrastructure.mapper.StudentCheckinMapper;
import com.kg.interfaces.dto.CheckinCalendarVO;
import com.kg.interfaces.dto.CheckinStatVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 学生打卡统计服务 —— 连续天数 + 本月次数 + 日历。
 */
@Service
public class StudentCheckinApplicationService {

    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final StudentCheckinMapper checkinMapper;

    public StudentCheckinApplicationService(StudentCheckinMapper checkinMapper) {
        this.checkinMapper = checkinMapper;
    }

    // ======================== 打卡统计 ========================

    /** 连续打卡天数 + 本月打卡次数 + 今日是否已打卡 */
    public CheckinStatVO getStat() {
        Long userId = requireStudentId();
        Set<String> dates = new HashSet<>(checkinMapper.findCheckinDates(userId));
        LocalDate today = LocalDate.now();

        // 连续打卡天数
        int continuousDays = 0;
        if (dates.contains(today.toString())) {
            continuousDays = 1;
            LocalDate cursor = today.minusDays(1);
            while (dates.contains(cursor.toString())) {
                continuousDays++;
                cursor = cursor.minusDays(1);
            }
        }

        // 本月打卡次数
        String monthPrefix = today.format(MONTH_FMT);
        int monthCount = 0;
        for (String d : dates) {
            if (d.startsWith(monthPrefix)) monthCount++;
        }

        CheckinStatVO vo = new CheckinStatVO();
        vo.setContinuousDays(continuousDays);
        vo.setMonthCheckinCount(monthCount);
        vo.setTodayChecked(dates.contains(today.toString()));
        return vo;
    }

    // ======================== 打卡日历 ========================

    /** 查询指定月份已打卡日期列表 */
    public CheckinCalendarVO getCalendar(String month) {
        Long userId = requireStudentId();
        List<String> allDates = checkinMapper.findCheckinDates(userId);
        // 过滤出指定月份
        List<String> monthDates = allDates.stream()
                .filter(d -> d.startsWith(month))
                .sorted()
                .collect(Collectors.toList());

        CheckinCalendarVO vo = new CheckinCalendarVO();
        vo.setDates(monthDates);
        return vo;
    }

    // ======================== 工具方法 ========================

    private Long requireStudentId() {
        SysUser user = UserContext.getUser();
        if (user == null) throw new BusinessException(401, "未登录");
        return user.getId();
    }
}
