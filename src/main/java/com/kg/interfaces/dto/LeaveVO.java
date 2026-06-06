package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/** 请假申请列表项 VO */
@Schema(description = "请假申请")
public class LeaveVO {
    @Schema(description = "主键ID") private Long id;
    @Schema(description = "请假学生ID") private Long userId;
    @Schema(description = "请假学生姓名") private String userName;
    @Schema(description = "请假类型") private String leaveType;
    @Schema(description = "开始日期") private String startDate;
    @Schema(description = "开始时段") private String startPeriod;
    @Schema(description = "结束日期") private String endDate;
    @Schema(description = "结束时段") private String endPeriod;
    @Schema(description = "请假天数") private BigDecimal leaveDays;
    @Schema(description = "请假原因") private String reason;
    @Schema(description = "审批人ID") private Long approverId;
    @Schema(description = "审批人姓名") private String approverName;
    @Schema(description = "审批角色") private String approveRole;
    @Schema(description = "状态：0待审批 1通过 2拒绝") private String status;
    @Schema(description = "审批意见") private String approveRemark;
    @Schema(description = "审批时间") private String approveTime;
    @Schema(description = "创建时间") private String createTime;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; } public void setUserName(String userName) { this.userName = userName; }
    public String getLeaveType() { return leaveType; } public void setLeaveType(String leaveType) { this.leaveType = leaveType; }
    public String getStartDate() { return startDate; } public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getStartPeriod() { return startPeriod; } public void setStartPeriod(String startPeriod) { this.startPeriod = startPeriod; }
    public String getEndDate() { return endDate; } public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getEndPeriod() { return endPeriod; } public void setEndPeriod(String endPeriod) { this.endPeriod = endPeriod; }
    public BigDecimal getLeaveDays() { return leaveDays; } public void setLeaveDays(BigDecimal leaveDays) { this.leaveDays = leaveDays; }
    public String getReason() { return reason; } public void setReason(String reason) { this.reason = reason; }
    public Long getApproverId() { return approverId; } public void setApproverId(Long approverId) { this.approverId = approverId; }
    public String getApproverName() { return approverName; } public void setApproverName(String approverName) { this.approverName = approverName; }
    public String getApproveRole() { return approveRole; } public void setApproveRole(String approveRole) { this.approveRole = approveRole; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
    public String getApproveRemark() { return approveRemark; } public void setApproveRemark(String approveRemark) { this.approveRemark = approveRemark; }
    public String getApproveTime() { return approveTime; } public void setApproveTime(String approveTime) { this.approveTime = approveTime; }
    public String getCreateTime() { return createTime; } public void setCreateTime(String createTime) { this.createTime = createTime; }
}
