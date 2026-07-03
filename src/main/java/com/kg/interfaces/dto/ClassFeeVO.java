package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/** 班费列表项 VO */
@Schema(description = "班费")
public class ClassFeeVO {
    @Schema(description = "主键") private Long id;
    @Schema(description = "班级ID") private Long classId;
    @Schema(description = "班级名称") private String className;
    @Schema(description = "物料") private String material;
    @Schema(description = "费用金额") private BigDecimal amount;
    @Schema(description = "费用日期") private String feeDate;
    @Schema(description = "状态") private Integer status;
    @Schema(description = "创建时间") private String createTime;

    public Long getId() { return id; } public void setId(Long v) { this.id = v; }
    public Long getClassId() { return classId; } public void setClassId(Long v) { this.classId = v; }
    public String getClassName() { return className; } public void setClassName(String v) { this.className = v; }
    public String getMaterial() { return material; } public void setMaterial(String v) { this.material = v; }
    public BigDecimal getAmount() { return amount; } public void setAmount(BigDecimal v) { this.amount = v; }
    public String getFeeDate() { return feeDate; } public void setFeeDate(String v) { this.feeDate = v; }
    public Integer getStatus() { return status; } public void setStatus(Integer v) { this.status = v; }
    public String getCreateTime() { return createTime; } public void setCreateTime(String v) { this.createTime = v; }
}
