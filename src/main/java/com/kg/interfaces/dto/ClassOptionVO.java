package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 班级下拉选项 VO
 */
@Schema(description = "班级下拉选项")
public class ClassOptionVO {

    /** 班级ID */
    @Schema(description = "班级ID")
    private Long id;

    /** 班级名称 */
    @Schema(description = "班级名称")
    private String className;

    public static ClassOptionVO of(Long id, String className) {
        ClassOptionVO vo = new ClassOptionVO();
        vo.id = id;
        vo.className = className;
        return vo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}
