package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/** 学习阶段列表/详情 VO */
@Schema(description = "学习阶段")
public class StudyPhaseVO {
    @Schema(description = "主键") private Long id;
    @Schema(description = "阶段标题") private String phaseTitle;
    @Schema(description = "阶段内容") private String phaseContent;
    @Schema(description = "开始日期") private String startDate;
    @Schema(description = "结束日期") private String endDate;
    @Schema(description = "展示颜色") private String displayColor;
    @Schema(description = "状态") private Integer status;
    @Schema(description = "班级ID列表") private List<Long> classIds;
    @Schema(description = "班级名称列表") private List<String> classNames;
    @Schema(description = "创建时间") private String createTime;

    public Long getId() { return id; } public void setId(Long v) { this.id = v; }
    public String getPhaseTitle() { return phaseTitle; } public void setPhaseTitle(String v) { this.phaseTitle = v; }
    public String getPhaseContent() { return phaseContent; } public void setPhaseContent(String v) { this.phaseContent = v; }
    public String getStartDate() { return startDate; } public void setStartDate(String v) { this.startDate = v; }
    public String getEndDate() { return endDate; } public void setEndDate(String v) { this.endDate = v; }
    public String getDisplayColor() { return displayColor; } public void setDisplayColor(String v) { this.displayColor = v; }
    public Integer getStatus() { return status; } public void setStatus(Integer v) { this.status = v; }
    public List<Long> getClassIds() { return classIds; } public void setClassIds(List<Long> v) { this.classIds = v; }
    public List<String> getClassNames() { return classNames; } public void setClassNames(List<String> v) { this.classNames = v; }
    public String getCreateTime() { return createTime; } public void setCreateTime(String v) { this.createTime = v; }
}
