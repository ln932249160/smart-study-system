package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/** 班费新增/编辑请求 */
@Schema(description = "班费新增/编辑请求")
public class ClassFeeRequest {
    @NotNull @Schema(description = "班级ID", requiredMode = Schema.RequiredMode.REQUIRED) private Long classId;
    @Schema(description = "物料") private String material;
    @NotNull @Schema(description = "费用金额", requiredMode = Schema.RequiredMode.REQUIRED) private BigDecimal amount;
    @NotBlank @Schema(description = "费用日期", requiredMode = Schema.RequiredMode.REQUIRED) private String feeDate;

    public Long getClassId() { return classId; } public void setClassId(Long v) { this.classId = v; }
    public String getMaterial() { return material; } public void setMaterial(String v) { this.material = v; }
    public BigDecimal getAmount() { return amount; } public void setAmount(BigDecimal v) { this.amount = v; }
    public String getFeeDate() { return feeDate; } public void setFeeDate(String v) { this.feeDate = v; }
}
