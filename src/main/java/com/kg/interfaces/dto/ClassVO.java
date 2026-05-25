package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 班级列表项 VO
 */
@Schema(description = "班级信息")
public class ClassVO {

    /** 班级ID */
    @Schema(description = "班级ID")
    private Long id;

    /** 班级名称 */
    @Schema(description = "班级名称")
    private String className;

    /** 班级描述 */
    @Schema(description = "班级描述")
    private String description;

    /** 学生人数 */
    @Schema(description = "学生人数")
    private Long studentCount;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private String createTime;

    // ======================== getters / setters ========================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getStudentCount() { return studentCount; }
    public void setStudentCount(Long studentCount) { this.studentCount = studentCount; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}
