package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/** Excel 导入结果 VO */
@Schema(description = "导入结果")
public class ImportResultVO {
    @Schema(description = "成功数") private int success;
    @Schema(description = "失败数") private int fail;
    @Schema(description = "错误信息列表") private List<String> errors;

    public int getSuccess() { return success; } public void setSuccess(int v) { this.success = v; }
    public int getFail() { return fail; } public void setFail(int v) { this.fail = v; }
    public List<String> getErrors() { return errors; } public void setErrors(List<String> v) { this.errors = v; }
}
