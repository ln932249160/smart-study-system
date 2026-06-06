package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/** 请假申请新增请求 */
@Schema(description = "请假申请新增请求")
public class LeaveCreateRequest {
    @NotBlank @Schema(description = "请假类型：1事假 2病假 3其他", requiredMode = Schema.RequiredMode.REQUIRED)
    private String leaveType;
    @NotNull @Schema(description = "开始日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private String startDate;
    @NotBlank @Schema(description = "开始时段：AM上午 PM下午 EV晚上", requiredMode = Schema.RequiredMode.REQUIRED)
    private String startPeriod;
    @NotNull @Schema(description = "结束日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private String endDate;
    @NotBlank @Schema(description = "结束时段：AM上午 PM下午 EV晚上", requiredMode = Schema.RequiredMode.REQUIRED)
    private String endPeriod;
    @Schema(description = "请假原因") private String reason;

    public String getLeaveType() { return leaveType; } public void setLeaveType(String s) { this.leaveType = s; }
    public String getStartDate() { return startDate; } public void setStartDate(String s) { this.startDate = s; }
    public String getStartPeriod() { return startPeriod; } public void setStartPeriod(String s) { this.startPeriod = s; }
    public String getEndDate() { return endDate; } public void setEndDate(String s) { this.endDate = s; }
    public String getEndPeriod() { return endPeriod; } public void setEndPeriod(String s) { this.endPeriod = s; }
    public String getReason() { return reason; } public void setReason(String s) { this.reason = s; }
}
