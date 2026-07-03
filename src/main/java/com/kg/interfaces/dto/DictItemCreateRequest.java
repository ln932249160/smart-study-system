package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

@Schema(description = "新增字典项")
public class DictItemCreateRequest {
    @NotBlank @Schema(description = "字典编码") private String dictCode;
    @NotBlank @Schema(description = "字典名称") private String dictName;
    @NotBlank @Schema(description = "字典值") private String dictValue;
    @Schema(description = "字典值中文名称") private String dictLabel;
    @Schema(description = "排序") private Integer sortNo;

    public String getDictCode() { return dictCode; } public void setDictCode(String s) { this.dictCode = s; }
    public String getDictName() { return dictName; } public void setDictName(String s) { this.dictName = s; }
    public String getDictValue() { return dictValue; } public void setDictValue(String s) { this.dictValue = s; }
    public String getDictLabel() { return dictLabel; } public void setDictLabel(String s) { this.dictLabel = s; }
    public Integer getSortNo() { return sortNo; } public void setSortNo(Integer i) { this.sortNo = i; }
}
