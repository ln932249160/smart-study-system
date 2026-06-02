package com.kg.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 我的任务 Mapper —— 以 task_user 为核心 JOIN task，一次 SQL 查完。
 */
@Mapper
public interface MyTaskMapper {

    /**
     * 分页查询当前用户的任务，支持多条件筛选。
     * 排序：is_mandatory=1 优先，然后 create_time 倒序。
     */
    @Select("<script>" +
            "SELECT t.id AS task_id, t.task_name, t.task_type, " +
            "t.is_mandatory AS force_flag, " +
            "t.task_start_time AS start_time, t.task_end_time AS end_time, " +
            "tu.status, tu.total_score, tu.submit_time " +
            "FROM task_user tu JOIN task t ON t.id = tu.task_id " +
            "WHERE tu.user_id = #{userId} " +
            "<if test='status != null and status != \"\"'>AND tu.status = #{status} </if>" +
            "<if test='taskName != null and taskName != \"\"'>AND t.task_name LIKE CONCAT('%', #{taskName}, '%') </if>" +
            "<if test='taskType != null and taskType != \"\"'>AND t.task_type = #{taskType} </if>" +
            "<if test='forceFlag != null'>AND t.is_mandatory = #{forceFlag} </if>" +
            "<if test='startTimeBegin != null and startTimeBegin != \"\"'>AND t.task_start_time &gt;= #{startTimeBegin} </if>" +
            "<if test='startTimeEnd != null and startTimeEnd != \"\"'>AND t.task_start_time &lt;= #{startTimeEnd} </if>" +
            "<if test='endTimeBegin != null and endTimeBegin != \"\"'>AND t.task_end_time &gt;= #{endTimeBegin} </if>" +
            "<if test='endTimeEnd != null and endTimeEnd != \"\"'>AND t.task_end_time &lt;= #{endTimeEnd} </if>" +
            "ORDER BY t.is_mandatory DESC, t.create_time DESC " +
            "LIMIT #{offset}, #{limit}</script>")
    List<Map<String, Object>> page(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("taskName") String taskName,
            @Param("taskType") String taskType,
            @Param("forceFlag") Integer forceFlag,
            @Param("startTimeBegin") String startTimeBegin,
            @Param("startTimeEnd") String startTimeEnd,
            @Param("endTimeBegin") String endTimeBegin,
            @Param("endTimeEnd") String endTimeEnd,
            @Param("offset") int offset,
            @Param("limit") int limit);

    /** 总数 */
    @Select("<script>" +
            "SELECT COUNT(*) FROM task_user tu JOIN task t ON t.id = tu.task_id " +
            "WHERE tu.user_id = #{userId} " +
            "<if test='status != null and status != \"\"'>AND tu.status = #{status} </if>" +
            "<if test='taskName != null and taskName != \"\"'>AND t.task_name LIKE CONCAT('%', #{taskName}, '%') </if>" +
            "<if test='taskType != null and taskType != \"\"'>AND t.task_type = #{taskType} </if>" +
            "<if test='forceFlag != null'>AND t.is_mandatory = #{forceFlag} </if>" +
            "<if test='startTimeBegin != null and startTimeBegin != \"\"'>AND t.task_start_time &gt;= #{startTimeBegin} </if>" +
            "<if test='startTimeEnd != null and startTimeEnd != \"\"'>AND t.task_start_time &lt;= #{startTimeEnd} </if>" +
            "<if test='endTimeBegin != null and endTimeBegin != \"\"'>AND t.task_end_time &gt;= #{endTimeBegin} </if>" +
            "<if test='endTimeEnd != null and endTimeEnd != \"\"'>AND t.task_end_time &lt;= #{endTimeEnd} </if>" +
            "</script>")
    long count(@Param("userId") Long userId,
               @Param("status") String status,
               @Param("taskName") String taskName,
               @Param("taskType") String taskType,
               @Param("forceFlag") Integer forceFlag,
               @Param("startTimeBegin") String startTimeBegin,
               @Param("startTimeEnd") String startTimeEnd,
               @Param("endTimeBegin") String endTimeBegin,
               @Param("endTimeEnd") String endTimeEnd);
}
