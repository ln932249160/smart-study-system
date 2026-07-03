package com.kg.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.infrastructure.entity.LeaveRequestEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/** 请假申请 Mapper，操作 leave_request 表。 */
@Mapper
public interface LeaveRequestMapper extends BaseMapper<LeaveRequestEntity> {

    /** 我的请假分页（LEFT JOIN 查出 userName） */
    @Select("<script>" +
            "SELECT lr.*, su.name AS userName " +
            "FROM leave_request lr LEFT JOIN sys_user su ON lr.user_id = su.id " +
            "WHERE lr.user_id = #{userId} " +
            "<if test='leaveType != null and leaveType != \"\"'>AND lr.leave_type = #{leaveType} </if>" +
            "<if test='status != null and status != \"\"'>AND lr.status = #{status} </if>" +
            "<if test='startDateBegin != null and startDateBegin != \"\"'>AND lr.start_date &gt;= #{startDateBegin} </if>" +
            "<if test='startDateEnd != null and startDateEnd != \"\"'>AND lr.start_date &lt;= #{startDateEnd} </if>" +
            "ORDER BY lr.create_time DESC LIMIT #{offset}, #{limit}</script>")
    List<Map<String, Object>> pageMyLeaves(@Param("userId") Long userId,
                                           @Param("leaveType") String leaveType,
                                           @Param("status") String status,
                                           @Param("startDateBegin") String startDateBegin,
                                           @Param("startDateEnd") String startDateEnd,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);

    /** 我的请假计数 */
    @Select("<script>" +
            "SELECT COUNT(*) FROM leave_request lr " +
            "WHERE lr.user_id = #{userId} " +
            "<if test='leaveType != null and leaveType != \"\"'>AND lr.leave_type = #{leaveType} </if>" +
            "<if test='status != null and status != \"\"'>AND lr.status = #{status} </if>" +
            "<if test='startDateBegin != null and startDateBegin != \"\"'>AND lr.start_date &gt;= #{startDateBegin} </if>" +
            "<if test='startDateEnd != null and startDateEnd != \"\"'>AND lr.start_date &lt;= #{startDateEnd} </if>" +
            "</script>")
    long countMyLeaves(@Param("userId") Long userId,
                       @Param("leaveType") String leaveType,
                       @Param("status") String status,
                       @Param("startDateBegin") String startDateBegin,
                       @Param("startDateEnd") String startDateEnd);

    /** 老师审批列表（LEFT JOIN 查出 userName） */
    @Select("<script>" +
            "SELECT lr.*, su.name AS userName " +
            "FROM leave_request lr LEFT JOIN sys_user su ON lr.user_id = su.id " +
            "WHERE lr.approve_role = #{approveRole} " +
            "<if test='status != null and status != \"\"'>AND lr.status = #{status} </if>" +
            "ORDER BY lr.create_time DESC LIMIT #{offset}, #{limit}</script>")
    List<Map<String, Object>> pageByApproveRole(@Param("approveRole") String approveRole,
                                                 @Param("status") String status,
                                                 @Param("offset") int offset,
                                                 @Param("limit") int limit);

    /** 老师审批计数 */
    @Select("<script>" +
            "SELECT COUNT(*) FROM leave_request lr " +
            "WHERE lr.approve_role = #{approveRole} " +
            "<if test='status != null and status != \"\"'>AND lr.status = #{status} </if>" +
            "</script>")
    long countByApproveRole(@Param("approveRole") String approveRole,
                            @Param("status") String status);

    /** 班长审批列表（LEFT JOIN 查出 userName） */
    @Select("<script>" +
            "SELECT lr.*, su.name AS userName " +
            "FROM leave_request lr LEFT JOIN sys_user su ON lr.user_id = su.id " +
            "WHERE lr.approver_id = #{approverId} " +
            "<if test='status != null and status != \"\"'>AND lr.status = #{status} </if>" +
            "ORDER BY lr.create_time DESC LIMIT #{offset}, #{limit}</script>")
    List<Map<String, Object>> pageByApproverId(@Param("approverId") Long approverId,
                                                @Param("status") String status,
                                                @Param("offset") int offset,
                                                @Param("limit") int limit);

    /** 班长审批计数 */
    @Select("<script>" +
            "SELECT COUNT(*) FROM leave_request lr " +
            "WHERE lr.approver_id = #{approverId} " +
            "<if test='status != null and status != \"\"'>AND lr.status = #{status} </if>" +
            "</script>")
    long countByApproverId(@Param("approverId") Long approverId,
                           @Param("status") String status);
}
