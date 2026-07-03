package com.kg.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 请假申请领域模型 —— 字段来源于 leave_request 表。
 */
public class LeaveRequest {
    private Long id;
    /** 请假学生ID */
    private Long userId;
    /** 请假类型：1事假 2病假 3其他 */
    private String leaveType;
    /** 开始日期 */
    private LocalDate startDate;
    /** 开始时段：AM上午 PM下午 EV晚上 */
    private String startPeriod;
    /** 结束日期 */
    private LocalDate endDate;
    /** 结束时段：AM上午 PM下午 EV晚上 */
    private String endPeriod;
    /** 请假天数 */
    private BigDecimal leaveDays;
    /** 请假原因 */
    private String reason;
    /** 审批人ID */
    private Long approverId;
    /** 审批角色：2班长 1老师 */
    private String approveRole;
    /** 状态：0待审批 1通过 2拒绝 */
    private String status;
    /** 审批意见 */
    private String approveRemark;
    /** 审批时间 */
    private LocalDateTime approveTime;
    /** 创建人 */
    private Long createBy;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 修改人 */
    private Long updateBy;
    /** 修改时间 */
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public String getStartPeriod() { return startPeriod; }
    public void setStartPeriod(String startPeriod) { this.startPeriod = startPeriod; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getEndPeriod() { return endPeriod; }
    public void setEndPeriod(String endPeriod) { this.endPeriod = endPeriod; }
    public BigDecimal getLeaveDays() { return leaveDays; }
    public void setLeaveDays(BigDecimal leaveDays) { this.leaveDays = leaveDays; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Long getApproverId() { return approverId; }
    public void setApproverId(Long approverId) { this.approverId = approverId; }
    public String getApproveRole() { return approveRole; }
    public void setApproveRole(String approveRole) { this.approveRole = approveRole; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getApproveRemark() { return approveRemark; }
    public void setApproveRemark(String approveRemark) { this.approveRemark = approveRemark; }
    public LocalDateTime getApproveTime() { return approveTime; }
    public void setApproveTime(LocalDateTime approveTime) { this.approveTime = approveTime; }
    public Long getCreateBy() { return createBy; }
    public void setCreateBy(Long createBy) { this.createBy = createBy; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public Long getUpdateBy() { return updateBy; }
    public void setUpdateBy(Long updateBy) { this.updateBy = updateBy; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
