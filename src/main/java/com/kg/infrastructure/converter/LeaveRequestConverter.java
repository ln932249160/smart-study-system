package com.kg.infrastructure.converter;

import com.kg.domain.model.LeaveRequest;
import com.kg.infrastructure.entity.LeaveRequestEntity;

/** LeaveRequest ↔ LeaveRequestEntity */
public class LeaveRequestConverter {
    public static LeaveRequest toDomain(LeaveRequestEntity e) {
        if (e == null) return null;
        LeaveRequest d = new LeaveRequest();
        d.setId(e.getId()); d.setUserId(e.getUserId()); d.setLeaveType(e.getLeaveType());
        d.setStartDate(e.getStartDate()); d.setStartPeriod(e.getStartPeriod());
        d.setEndDate(e.getEndDate()); d.setEndPeriod(e.getEndPeriod());
        d.setLeaveDays(e.getLeaveDays()); d.setReason(e.getReason());
        d.setApproverId(e.getApproverId()); d.setApproveRole(e.getApproveRole());
        d.setStatus(e.getStatus()); d.setApproveRemark(e.getApproveRemark());
        d.setApproveTime(e.getApproveTime());
        d.setCreateBy(e.getCreateBy()); d.setCreateTime(e.getCreateTime());
        d.setUpdateBy(e.getUpdateBy()); d.setUpdateTime(e.getUpdateTime());
        return d;
    }

    public static LeaveRequestEntity toEntity(LeaveRequest d) {
        if (d == null) return null;
        LeaveRequestEntity e = new LeaveRequestEntity();
        e.setId(d.getId()); e.setUserId(d.getUserId()); e.setLeaveType(d.getLeaveType());
        e.setStartDate(d.getStartDate()); e.setStartPeriod(d.getStartPeriod());
        e.setEndDate(d.getEndDate()); e.setEndPeriod(d.getEndPeriod());
        e.setLeaveDays(d.getLeaveDays()); e.setReason(d.getReason());
        e.setApproverId(d.getApproverId()); e.setApproveRole(d.getApproveRole());
        e.setStatus(d.getStatus()); e.setApproveRemark(d.getApproveRemark());
        e.setApproveTime(d.getApproveTime());
        e.setCreateBy(d.getCreateBy()); e.setCreateTime(d.getCreateTime());
        e.setUpdateBy(d.getUpdateBy()); e.setUpdateTime(d.getUpdateTime());
        return e;
    }
}
