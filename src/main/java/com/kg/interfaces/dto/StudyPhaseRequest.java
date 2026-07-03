package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/** 学习阶段新增/编辑请求 */
@Schema(description = "学习阶段新增/编辑请求")
public class StudyPhaseRequest {
    private Long id;
    @NotBlank @Schema(description = "阶段标题", requiredMode = Schema.RequiredMode.REQUIRED) private String phaseTitle;
    @Schema(description = "阶段内容") private String phaseContent;
    @NotBlank @Schema(description = "开始日期", requiredMode = Schema.RequiredMode.REQUIRED) private String startDate;
    @NotBlank @Schema(description = "结束日期", requiredMode = Schema.RequiredMode.REQUIRED) private String endDate;
    @Schema(description = "展示颜色") private String displayColor;
    @NotEmpty @Schema(description = "班级ID列表", requiredMode = Schema.RequiredMode.REQUIRED) private List<Long> classIds;

    public Long getId() { return id; } public void setId(Long v) { this.id = v; }
    public String getPhaseTitle() { return phaseTitle; } public void setPhaseTitle(String v) { this.phaseTitle = v; }
    public String getPhaseContent() { return phaseContent; } public void setPhaseContent(String v) { this.phaseContent = v; }
    public String getStartDate() { return startDate; } public void setStartDate(String v) { this.startDate = v; }
    public String getEndDate() { return endDate; } public void setEndDate(String v) { this.endDate = v; }
    public String getDisplayColor() { return displayColor; } public void setDisplayColor(String v) { this.displayColor = v; }
    public List<Long> getClassIds() { return classIds; } public void setClassIds(List<Long> v) { this.classIds = v; }
}
