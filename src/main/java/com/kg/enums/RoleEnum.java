package com.kg.enums;

/**
 * 角色枚举 —— 对应 sys_dict ROLE。
 */
public enum RoleEnum {

    TEACHER("1", "老师"),
    HEADMASTER("2", "班长"),
    STUDENT("3", "学生");

    private final String code;
    private final String label;

    RoleEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }

    public boolean eq(String code) { return this.code.equals(code); }

    /** 是否为指定角色 */
    public static boolean isTeacher(String role) { return TEACHER.code.equals(role); }
    public static boolean isHeadmaster(String role) { return HEADMASTER.code.equals(role); }
    public static boolean isStudent(String role) { return STUDENT.code.equals(role); }
}
