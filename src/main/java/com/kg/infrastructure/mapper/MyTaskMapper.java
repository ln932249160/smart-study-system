package com.kg.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 我的任务 Mapper —— 以 task_user 为核心 JOIN task。
 */
@Mapper
public interface MyTaskMapper {

    // ======================== 我的任务分页 ========================

    /** 分页，按创建时间倒序 */
    @Select("<script>" +
            "SELECT u.name AS task_create_name,t.round_no AS round_no,t.template_id AS template_id,t.id AS task_id, t.task_name, t.task_type, " +
            "t.is_mandatory AS force_flag, t.task_description, t.priority, " +
            "t.task_start_time AS start_time, t.task_end_time AS end_time, " +
            "t.plan_id AS planId, t.plan_date AS planDate, t.is_repeat_task AS isRepeatTask, t.status AS taskStatus, " +
            "tu.id AS task_user_id, tu.status, tu.total_score, tu.submit_time " +
            "FROM task_user tu JOIN task t ON t.id = tu.task_id LEFT JOIN sys_user u ON u.id = t.create_by " +
            "WHERE tu.user_id = #{userId} " +
            "<if test='status != null and status != \"\"'>AND tu.status = #{status} </if>" +
            "<if test='taskName != null and taskName != \"\"'>AND t.task_name LIKE CONCAT('%', #{taskName}, '%') </if>" +
            "<if test='taskType != null and taskType != \"\"'>AND t.task_type = #{taskType} </if>" +
            "<if test='forceFlag != null'>AND t.is_mandatory = #{forceFlag} </if>" +
            "<if test='startTimeBegin != null and startTimeBegin != \"\"'>AND t.task_start_time &gt;= #{startTimeBegin} </if>" +
            "<if test='startTimeEnd != null and startTimeEnd != \"\"'>AND t.task_start_time &lt;= #{startTimeEnd} </if>" +
            "<if test='endTimeBegin != null and endTimeBegin != \"\"'>AND t.task_end_time &gt;= #{endTimeBegin} </if>" +
            "<if test='endTimeEnd != null and endTimeEnd != \"\"'>AND t.task_end_time &lt;= #{endTimeEnd} </if>" +
            "<if test='finishTimeStart != null and finishTimeStart != \"\"'>AND tu.finish_time &gt;= #{finishTimeStart} </if>" +
            "<if test='finishTimeEnd != null and finishTimeEnd != \"\"'>AND tu.finish_time &lt;= #{finishTimeEnd} </if>" +
            "<if test='isTemplate != null and isTemplate == 1'>AND t.template_id IS NOT NULL </if>" +
            "<if test='isTemplate != null and isTemplate == 0'>AND t.template_id IS NULL </if>" +
            "ORDER BY t.create_time DESC " +
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
            @Param("finishTimeStart") String finishTimeStart,
            @Param("finishTimeEnd") String finishTimeEnd,
            @Param("isTemplate") Integer isTemplate,
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
            "<if test='finishTimeStart != null and finishTimeStart != \"\"'>AND tu.finish_time &gt;= #{finishTimeStart} </if>" +
            "<if test='finishTimeEnd != null and finishTimeEnd != \"\"'>AND tu.finish_time &lt;= #{finishTimeEnd} </if>" +
            "<if test='isTemplate != null and isTemplate == 1'>AND t.template_id IS NOT NULL </if>" +
            "<if test='isTemplate != null and isTemplate == 0'>AND t.template_id IS NULL </if>" +
            "</script>")
    long count(@Param("userId") Long userId,
               @Param("status") String status,
               @Param("taskName") String taskName,
               @Param("taskType") String taskType,
               @Param("forceFlag") Integer forceFlag,
               @Param("startTimeBegin") String startTimeBegin,
               @Param("startTimeEnd") String startTimeEnd,
               @Param("endTimeBegin") String endTimeBegin,
               @Param("endTimeEnd") String endTimeEnd,
               @Param("finishTimeStart") String finishTimeStart,
               @Param("finishTimeEnd") String finishTimeEnd,
               @Param("isTemplate") Integer isTemplate);

    // ======================== 任务详情 ========================

    /** 查询任务完整信息（task + task_user） */
    @Select("SELECT t.id AS task_id, t.task_name, t.task_type, t.task_description, " +
            "t.priority, t.round_no, t.task_start_time, t.task_end_time, " +
            "t.plan_id AS planId, t.plan_date AS planDate, t.is_repeat_task AS isRepeatTask, t.status AS taskStatus, " +
            "tu.id AS task_user_id, tu.status, tu.start_time, tu.finish_time, " +
            "tu.duration_minutes, tu.remark, tu.total_score, tu.submit_time " +
            "FROM task_user tu JOIN task t ON t.id = tu.task_id " +
            "WHERE tu.id = #{taskUserId} AND tu.user_id = #{userId}")
    Map<String, Object> findDetail(@Param("taskUserId") Long taskUserId, @Param("userId") Long userId);

    // ======================== 我的模板任务 ========================

    /** 查询用户做过的所有模板任务的分组统计（支持 templateName/taskType/isMandatory 筛选） */
    @Select("<script>" +
            "SELECT tt.id AS template_id, tt.template_name, tt.task_type, " +
            "COUNT(DISTINCT t.id) AS round_count, " +
            "MAX(tu.total_score) AS max_score, " +
            "MIN(tu.total_score) AS min_score, " +
            "AVG(tu.total_score) AS avg_score, " +
            "(SELECT tu2.total_score FROM task_user tu2 JOIN task t2 ON t2.id = tu2.task_id " +
            " WHERE t2.template_id = tt.id AND tu2.user_id = #{userId} AND tu2.status IN ('1','2') " +
            " ORDER BY t2.round_no DESC LIMIT 1) AS latest_score " +
            "FROM task t " +
            "JOIN task_user tu ON tu.task_id = t.id " +
            "JOIN task_template tt ON tt.id = t.template_id " +
            "WHERE tu.user_id = #{userId} AND tu.status IN ('1','2') AND t.template_id IS NOT NULL " +
            "<if test='templateName != null and templateName != \"\"'>AND tt.template_name LIKE CONCAT('%', #{templateName}, '%') </if>" +
            "<if test='taskType != null and taskType != \"\"'>AND tt.task_type = #{taskType} </if>" +
            "<if test='isMandatory != null'>AND t.is_mandatory = #{isMandatory} </if>" +
            "GROUP BY tt.id, tt.template_name, tt.task_type " +
            "ORDER BY tt.id DESC " +
            "LIMIT #{offset}, #{limit}</script>")
    List<Map<String, Object>> pageTemplates(@Param("userId") Long userId,
                                             @Param("templateName") String templateName,
                                             @Param("taskType") String taskType,
                                             @Param("isMandatory") Integer isMandatory,
                                             @Param("offset") int offset,
                                             @Param("limit") int limit);

    /** 模板任务分组计数 */
    @Select("<script>" +
            "SELECT COUNT(DISTINCT tt.id) " +
            "FROM task t " +
            "JOIN task_user tu ON tu.task_id = t.id " +
            "JOIN task_template tt ON tt.id = t.template_id " +
            "WHERE tu.user_id = #{userId} AND tu.status IN ('1','2') AND t.template_id IS NOT NULL " +
            "<if test='templateName != null and templateName != \"\"'>AND tt.template_name LIKE CONCAT('%', #{templateName}, '%') </if>" +
            "<if test='taskType != null and taskType != \"\"'>AND tt.task_type = #{taskType} </if>" +
            "<if test='isMandatory != null'>AND t.is_mandatory = #{isMandatory} </if>" +
            "</script>")
    long countTemplates(@Param("userId") Long userId,
                        @Param("templateName") String templateName,
                        @Param("taskType") String taskType,
                        @Param("isMandatory") Integer isMandatory);

    /** 查询指定模板下用户的所有轮次记录（按 roundNo 升序） */
    @Select("SELECT t.id AS task_id, t.task_name, t.round_no, " +
            "t.task_start_time, t.task_end_time, " +
            "tu.id AS task_user_id, tu.status, tu.total_score, tu.submit_time " +
            "FROM task t " +
            "JOIN task_user tu ON tu.task_id = t.id " +
            "WHERE t.template_id = #{templateId} AND tu.user_id = #{userId} " +
            "ORDER BY t.round_no ASC")
    List<Map<String, Object>> findTemplateRounds(@Param("templateId") Long templateId,
                                                  @Param("userId") Long userId);
}
