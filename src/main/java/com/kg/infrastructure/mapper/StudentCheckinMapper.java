package com.kg.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 学生打卡 Mapper —— 查询当前学生所有已完成打卡日期。
 */
@Mapper
public interface StudentCheckinMapper {

    /**
     * 查询学生所有已完成打卡日期（去重，倒序）。
     * 打卡日期来源：DATE(task.task_start_time)
     */
    @Select("SELECT DISTINCT DATE(t.task_start_time) AS checkinDate " +
            "FROM task_user tu JOIN task t ON t.id = tu.task_id " +
            "WHERE tu.user_id = #{userId} AND t.task_type = #{taskType} AND tu.status IN ('1','2') " +
            "ORDER BY checkinDate DESC")
    List<String> findCheckinDates(@Param("userId") Long userId, @Param("taskType") String taskType);
}
