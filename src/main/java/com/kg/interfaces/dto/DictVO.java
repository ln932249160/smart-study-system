package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 字典项 VO
 */
@Schema(description = "字典项")
public class DictVO {

    @Schema(description = "字典值（存储值）")
    private String value;

    @Schema(description = "字典名称（显示名）")
    private String label;

    public static DictVO of(String value, String label) {
        DictVO vo = new DictVO();
        vo.value = value;
        vo.label = label;
        return vo;
    }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}
