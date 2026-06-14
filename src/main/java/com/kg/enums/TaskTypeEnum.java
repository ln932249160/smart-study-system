package com.kg.enums;

/** 任务类型枚举 —— 与 sys_dict task_type 严格对应，禁止通过接口修改 */
public enum TaskTypeEnum {
    BRUSH_QUESTIONS("0", "刷题", "#ff6f00"),
    STUDY("1", "学习", "#f7ff00"),
    CHECK_IN("2", "打卡", "#66ff00"),
    DAILY_REVIEW("3", "每日复盘", "#00bfff");

    private final String code;
    private final String label;
    private final String color;

    TaskTypeEnum(String code, String label, String color) {
        this.code = code;
        this.label = label;
        this.color = color;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }
    public String getColor() { return color; }
}
