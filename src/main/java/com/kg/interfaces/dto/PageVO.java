package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collections;
import java.util.List;

/** 统一分页响应 VO */
@Schema(description = "分页结果")
public class PageVO<T> {
    @Schema(description = "总条数") private long total;
    @Schema(description = "数据列表") private List<T> list;

    public PageVO() {}
    public PageVO(long total, List<T> list) { this.total = total; this.list = list; }

    public static <T> PageVO<T> of(long total, List<T> list) { return new PageVO<>(total, list); }
    public static <T> PageVO<T> empty() { return new PageVO<>(0L, Collections.emptyList()); }

    public long getTotal() { return total; } public void setTotal(long v) { this.total = v; }
    public List<T> getList() { return list; } public void setList(List<T> v) { this.list = v; }
}
