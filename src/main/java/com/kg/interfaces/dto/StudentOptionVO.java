package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 学生选项 VO —— 用于班级管理中的学生复选框列表。
 */
@Schema(description = "学生选项（复选框用）")
public class StudentOptionVO {

    /** 学生ID */
    @Schema(description = "学生ID")
    private Long id;

    /** 学生姓名（account） */
    @Schema(description = "学生姓名")
    private String name;

    /** 当前所属班级ID（null 表示未分班） */
    @Schema(description = "当前班级ID，null 表示未分班")
    private Long classId;

    public static StudentOptionVO of(Long id, String name, Long classId) {
        StudentOptionVO vo = new StudentOptionVO();
        vo.id = id;
        vo.name = name;
        vo.classId = classId;
        return vo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }
}
