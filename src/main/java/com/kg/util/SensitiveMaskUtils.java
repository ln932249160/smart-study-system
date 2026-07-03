package com.kg.util;

/**
 * 敏感数据脱敏工具。
 * 用于日志中隐藏密码/手机号/邮箱/token 等字段。
 */
public class SensitiveMaskUtils {
    private static final String MASK = "******";
    private static final String MASK_MOBILE_REPLACE = "****";
    private static final String[] SENSITIVE_KEYS = {
            "password", "oldPassword", "newPassword", "confirmPassword",
            "token", "accessToken", "refreshToken", "authorization",
            "jwt", "secret", "idCard", "身份证", "phone", "mobile", "手机号"
    };

    /** 对 JSON/query string 中的敏感字段值脱敏 */
    public static String mask(String content) {
        if (content == null || content.isEmpty()) return content;
        // JSON 格式: "key":"value"
        for (String key : SENSITIVE_KEYS) {
            content = content.replaceAll("(?i)\"(" + key + ")\":\\s*\"[^\"]*\"", "\"$1\":\"" + MASK + "\"");
        }
        // query string: key=value
        for (String key : SENSITIVE_KEYS) {
            content = content.replaceAll("(?i)([?&]" + key + ")=([^&]*)", "$1=" + MASK);
        }
        // 手机号：连续11位数字，保留前三后四
        content = content.replaceAll("(?<![0-9])(1[3-9]\\d)(\\d{4})(\\d{4})(?![0-9])", "$1" + MASK_MOBILE_REPLACE + "$3");
        // 邮箱
        content = content.replaceAll("([a-zA-Z0-9])[^@]*(@[a-zA-Z0-9.]+)", "$1***$2");
        return content;
    }

    /** 截断过长内容 */
    public static String truncate(String content, int maxLength) {
        if (content == null || content.isEmpty() || content.length() <= maxLength) return content;
        return content.substring(0, maxLength) + "...[TRUNCATED length=" + content.length() + "]";
    }
}
