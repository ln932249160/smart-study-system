package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/** 字典分组 VO */
@Schema(description = "字典分组")
public class DictGroupVO {
    @Schema(description = "字典编码") private String dictCode;
    @Schema(description = "字典中文名") private String dictName;
    @Schema(description = "字典项列表") private List<DictGroupItemVO> items;

    public String getDictCode() { return dictCode; } public void setDictCode(String s) { this.dictCode = s; }
    public String getDictName() { return dictName; } public void setDictName(String s) { this.dictName = s; }
    public List<DictGroupItemVO> getItems() { return items; } public void setItems(List<DictGroupItemVO> l) { this.items = l; }

    @Schema(description = "字典项")
    public static class DictGroupItemVO {
        @Schema(description = "字典值") private String dictValue;
        @Schema(description = "字典值中文名") private String dictLabel;
        @Schema(description = "排序") private Integer sortNo;
        @Schema(description = "状态") private Integer status;
        @Schema(description = "展示颜色") private String displayColor;

        public String getDictValue() { return dictValue; } public void setDictValue(String s) { this.dictValue = s; }
        public String getDictLabel() { return dictLabel; } public void setDictLabel(String s) { this.dictLabel = s; }
        public Integer getSortNo() { return sortNo; } public void setSortNo(Integer i) { this.sortNo = i; }
        public Integer getStatus() { return status; } public void setStatus(Integer i) { this.status = i; }
        public String getDisplayColor() { return displayColor; } public void setDisplayColor(String s) { this.displayColor = s; }
    }
}
