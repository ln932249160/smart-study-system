package com.kg.enums;

/**
 * 任务状态枚举 —— 对应 sys_dict TASK_STATUS。
 */
public enum TaskStatusEnum {

    UNFINISHED("0", "未完成"),
    FINISHED("1", "已完成");

    private final String code;
    private final String label;

    TaskStatusEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }
}
