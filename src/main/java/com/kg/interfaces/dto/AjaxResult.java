package com.kg.interfaces.dto;

import java.util.LinkedHashMap;

/** 统一响应体。继承 LinkedHashMap 保持现有 JSON 结构 {code, message, data}。 */
public class AjaxResult extends LinkedHashMap<String, Object> {
    public AjaxResult() {}

    public static AjaxResult success() {
        AjaxResult r = new AjaxResult();
        r.put("code", 200);
        r.put("message", "操作成功");
        return r;
    }

    public static AjaxResult success(String message) {
        AjaxResult r = new AjaxResult();
        r.put("code", 200);
        r.put("message", message);
        return r;
    }

    public static AjaxResult success(String message, Object data) {
        AjaxResult r = new AjaxResult();
        r.put("code", 200);
        r.put("message", message);
        r.put("data", data);
        return r;
    }

    public static AjaxResult error(int code, String message) {
        AjaxResult r = new AjaxResult();
        r.put("code", code);
        r.put("message", message);
        return r;
    }
}
