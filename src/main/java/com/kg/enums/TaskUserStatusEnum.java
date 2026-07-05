package com.kg.enums;

import java.util.Arrays;
import java.util.List;

/** 学生任务完成状态枚举 —— 对应 sys_dict task_user_status */
public enum TaskUserStatusEnum {
    UNFINISHED("0", "未完成"),
    FINISHED("1", "已完成"),
    OVERDUE_FINISHED("2", "逾期完成");

    private final String code;
    private final String label;

    TaskUserStatusEnum(String code, String label) { this.code = code; this.label = label; }
    public String getCode() { return code; }
    public String getLabel() { return label; }

    /** 所有完成状态（含正常完成和逾期完成） */
    public static final List<String> FINISHED_CODES = Arrays.asList(
            FINISHED.getCode(), OVERDUE_FINISHED.getCode());

    public static boolean isFinished(String code) {
        return FINISHED.getCode().equals(code) || OVERDUE_FINISHED.getCode().equals(code);
    }
}
