package com.kg.enums;

/**
 * 模块名称枚举 —— 对应 sys_dict MODULE_NAME。
 */
public enum ModuleNameEnum {

    XC_CS("1", "行测-常识"),
    XC_YY("2", "行测-言语"),
    XC_SL("3", "行测-数量"),
    XC_PDTL("4", "行测-判断推理"),
    XC_ZLFX("5", "行测-资料分析"),
    SL_XT("6", "申论-小题"),
    SL_DZW("7", "申论-大作文");

    private final String code;
    private final String label;

    ModuleNameEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }
}
