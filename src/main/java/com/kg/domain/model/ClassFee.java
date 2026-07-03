package com.kg.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 班费领域模型 —— 字段来源于 class_fee 表。 */
public class ClassFee {
    private Long id;
    private Long classId;
    private String material;
    private BigDecimal amount;
    private LocalDate feeDate;
    private Integer status;
    private Long createBy;
    private LocalDateTime createTime;
    private Long updateBy;
    private LocalDateTime updateTime;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getClassId() { return classId; } public void setClassId(Long v) { this.classId = v; }
    public String getMaterial() { return material; } public void setMaterial(String v) { this.material = v; }
    public BigDecimal getAmount() { return amount; } public void setAmount(BigDecimal v) { this.amount = v; }
    public LocalDate getFeeDate() { return feeDate; } public void setFeeDate(LocalDate v) { this.feeDate = v; }
    public Integer getStatus() { return status; } public void setStatus(Integer v) { this.status = v; }
    public Long getCreateBy() { return createBy; } public void setCreateBy(Long v) { this.createBy = v; }
    public LocalDateTime getCreateTime() { return createTime; } public void setCreateTime(LocalDateTime v) { this.createTime = v; }
    public Long getUpdateBy() { return updateBy; } public void setUpdateBy(Long v) { this.updateBy = v; }
    public LocalDateTime getUpdateTime() { return updateTime; } public void setUpdateTime(LocalDateTime v) { this.updateTime = v; }
}
