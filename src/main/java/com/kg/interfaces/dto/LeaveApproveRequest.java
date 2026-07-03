package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/** 请假审批请求（支持批量） */
@Schema(description = "请假审批请求")
public class LeaveApproveRequest {
    @NotEmpty @Schema(description = "请假申请ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> ids;
    @NotBlank @Schema(description = "审批结果：1通过 2拒绝", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;
    @Schema(description = "审批意见") private String approveRemark;

    public List<Long> getIds() { return ids; } public void setIds(List<Long> ids) { this.ids = ids; }
    public String getStatus() { return status; } public void setStatus(String s) { this.status = s; }
    public String getApproveRemark() { return approveRemark; } public void setApproveRemark(String s) { this.approveRemark = s; }
}
