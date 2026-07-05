package com.kg.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 首页统计 Mapper —— 聚合 SQL，一次查询完成统计，禁止 N+1。
 */
@Mapper
public interface HomeMapper {

    // ==================== teacher 范围（全表） ====================

    /** 进行中任务数 */
    @Select("SELECT COUNT(*) FROM task WHERE task_start_time <= NOW() AND task_end_time >= NOW()")
    long countCurrentTasksForTeacher();

    /** 强制任务数 */
    @Select("SELECT COUNT(*) FROM task WHERE is_mandatory = 1")
    long countForceTasksForTeacher();

    /** 总任务数 */
    @Select("SELECT COUNT(*) FROM task")
    long countTotalTasksForTeacher();

    /** 完成统计（基于 task_user） */
    @Select("SELECT COUNT(*) AS total, " +
            "SUM(CASE WHEN tu.status IN ('1','2') THEN 1 ELSE 0 END) AS completed, " +
            "SUM(CASE WHEN tu.status = '0' THEN 1 ELSE 0 END) AS uncompleted " +
            "FROM task_user tu JOIN task t ON t.id = tu.task_id")
    Map<String, Object> completionStatsForTeacher();

    /** 完成率最低 TOP3 */
    @Select("SELECT t.id AS task_id, t.task_name, t.task_end_time AS end_time, " +
            "COUNT(*) AS total, " +
            "SUM(CASE WHEN tu.status IN ('1','2') THEN 1 ELSE 0 END) AS completed, " +
            "SUM(CASE WHEN tu.status = '0' THEN 1 ELSE 0 END) AS uncompleted " +
            "FROM task t JOIN task_user tu ON tu.task_id = t.id " +
            "GROUP BY t.id, t.task_name, t.task_end_time " +
            "ORDER BY (SUM(CASE WHEN tu.status IN ('1','2') THEN 1 ELSE 0 END) * 1.0 / COUNT(*)) ASC LIMIT 3")
    List<Map<String, Object>> lowCompletedTop3ForTeacher();

    // ==================== headmaster 范围（create_by） ====================

    @Select("SELECT COUNT(*) FROM task WHERE task_start_time <= NOW() AND task_end_time >= NOW() AND create_by = #{createBy}")
    long countCurrentTasksForHeadmaster(@Param("createBy") Long createBy);

    @Select("SELECT COUNT(*) FROM task WHERE is_mandatory = 1 AND create_by = #{createBy}")
    long countForceTasksForHeadmaster(@Param("createBy") Long createBy);

    @Select("SELECT COUNT(*) FROM task WHERE create_by = #{createBy}")
    long countTotalTasksForHeadmaster(@Param("createBy") Long createBy);

    @Select("SELECT COUNT(*) AS total, " +
            "SUM(CASE WHEN tu.status IN ('1','2') THEN 1 ELSE 0 END) AS completed, " +
            "SUM(CASE WHEN tu.status = '0' THEN 1 ELSE 0 END) AS uncompleted " +
            "FROM task_user tu JOIN task t ON t.id = tu.task_id WHERE t.create_by = #{createBy}")
    Map<String, Object> completionStatsForHeadmaster(@Param("createBy") Long createBy);

    @Select("SELECT t.id AS task_id, t.task_name, t.task_end_time AS end_time, " +
            "COUNT(*) AS total, " +
            "SUM(CASE WHEN tu.status IN ('1','2') THEN 1 ELSE 0 END) AS completed, " +
            "SUM(CASE WHEN tu.status = '0' THEN 1 ELSE 0 END) AS uncompleted " +
            "FROM task t JOIN task_user tu ON tu.task_id = t.id " +
            "WHERE t.create_by = #{createBy} " +
            "GROUP BY t.id, t.task_name, t.task_end_time " +
            "ORDER BY (SUM(CASE WHEN tu.status IN ('1','2') THEN 1 ELSE 0 END) * 1.0 / COUNT(*)) ASC LIMIT 3")
    List<Map<String, Object>> lowCompletedTop3ForHeadmaster(@Param("createBy") Long createBy);

    // ==================== student 范围 ====================

    /** 待完成任务数 */
    @Select("SELECT COUNT(*) FROM task_user WHERE user_id = #{userId} AND status = '0'")
    long countTodoForStudent(@Param("userId") Long userId);

    /** 已完成任务数 */
    @Select("SELECT COUNT(*) FROM task_user WHERE user_id = #{userId} AND status IN ('1','2')")
    long countCompletedForStudent(@Param("userId") Long userId);

    /** 即将截止 TOP3 */
    @Select("SELECT t.id AS task_id, t.task_name, t.task_end_time AS end_time " +
            "FROM task_user tu JOIN task t ON t.id = tu.task_id " +
            "WHERE tu.user_id = #{userId} AND tu.status = '0' " +
            "ORDER BY t.task_end_time ASC LIMIT 3")
    List<Map<String, Object>> nearEndTop3ForStudent(@Param("userId") Long userId);
}
