package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/** 班费统计 VO */
@Schema(description = "班费统计")
public class ClassFeeStatVO {
    @Schema(description = "班级ID") private Long classId;
    @Schema(description = "班级名称") private String className;
    @Schema(description = "合计金额") private BigDecimal totalAmount;

    public Long getClassId() { return classId; } public void setClassId(Long v) { this.classId = v; }
    public String getClassName() { return className; } public void setClassName(String v) { this.className = v; }
    public BigDecimal getTotalAmount() { return totalAmount; } public void setTotalAmount(BigDecimal v) { this.totalAmount = v; }
}
