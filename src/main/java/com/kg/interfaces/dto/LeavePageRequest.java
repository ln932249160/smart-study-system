package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/** 请假申请分页查询请求 */
@Schema(description = "请假申请分页查询请求")
public class LeavePageRequest {
    @NotNull @Min(1) @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED) private Integer pageNum;
    @NotNull @Min(1) @Schema(description = "每页条数", requiredMode = Schema.RequiredMode.REQUIRED) private Integer pageSize;
    @Schema(description = "请假类型：1事假 2病假 3其他") private String leaveType;
    @Schema(description = "开始日期起") private String startDateBegin;
    @Schema(description = "开始日期止") private String startDateEnd;
    @Schema(description = "状态：0待审批 1通过 2拒绝") private String status;

    public Integer getPageNum() { return pageNum; } public void setPageNum(Integer i) { this.pageNum = i; }
    public Integer getPageSize() { return pageSize; } public void setPageSize(Integer i) { this.pageSize = i; }
    public String getLeaveType() { return leaveType; } public void setLeaveType(String s) { this.leaveType = s; }
    public String getStartDateBegin() { return startDateBegin; } public void setStartDateBegin(String s) { this.startDateBegin = s; }
    public String getStartDateEnd() { return startDateEnd; } public void setStartDateEnd(String s) { this.startDateEnd = s; }
    public String getStatus() { return status; } public void setStatus(String s) { this.status = s; }
}
