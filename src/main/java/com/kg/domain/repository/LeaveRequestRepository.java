package com.kg.domain.repository;

import com.kg.domain.model.LeaveRequest;

import java.util.List;
import java.util.Optional;

/** 请假申请仓储接口 */
public interface LeaveRequestRepository {
    /** 新增，回填主键 */
    void save(LeaveRequest leave);
    /** 根据主键查询 */
    Optional<LeaveRequest> findById(Long id);
    /** 分页查询自己的请假 */
    List<LeaveRequest> pageByUserId(Long userId, String leaveType, String startDateBegin, String startDateEnd, int offset, int limit);
    /** 计数 */
    long countByUserId(Long userId, String leaveType, String startDateBegin, String startDateEnd);
    /** 老师审批列表：按审批角色 */
    List<LeaveRequest> pageByApproveRole(String approveRole, String status, int offset, int limit);
    /** 计数 */
    long countByApproveRole(String approveRole, String status);
    /** 班长审批列表：按审批人 */
    List<LeaveRequest> pageByApproverId(Long approverId, String status, int offset, int limit);
    /** 计数 */
    long countByApproverId(Long approverId, String status);
    /** 更新（只更新非null字段） */
    void update(LeaveRequest leave);
}
