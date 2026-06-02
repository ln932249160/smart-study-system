package com.kg.domain.model;

import java.time.LocalDateTime;

/** 字典项领域模型 */
public class DictItem {
    private Long id;
    private String dictCode;
    private String dictName;
    private String dictLabel;
    private String dictValue;
    private Integer sortNo;
    private Integer status;
    private Long createBy;
    private LocalDateTime createTime;
    private Long updateBy;
    private LocalDateTime updateTime;

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getDictCode() { return dictCode; } public void setDictCode(String s) { this.dictCode = s; }
    public String getDictName() { return dictName; } public void setDictName(String s) { this.dictName = s; }
    public String getDictLabel() { return dictLabel; } public void setDictLabel(String s) { this.dictLabel = s; }
    public String getDictValue() { return dictValue; } public void setDictValue(String s) { this.dictValue = s; }
    public Integer getSortNo() { return sortNo; } public void setSortNo(Integer i) { this.sortNo = i; }
    public Integer getStatus() { return status; } public void setStatus(Integer i) { this.status = i; }
    public Long getCreateBy() { return createBy; } public void setCreateBy(Long l) { this.createBy = l; }
    public LocalDateTime getCreateTime() { return createTime; } public void setCreateTime(LocalDateTime t) { this.createTime = t; }
    public Long getUpdateBy() { return updateBy; } public void setUpdateBy(Long l) { this.updateBy = l; }
    public LocalDateTime getUpdateTime() { return updateTime; } public void setUpdateTime(LocalDateTime t) { this.updateTime = t; }
}
