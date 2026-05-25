package com.kg.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 全局异常处理器 —— 统一将异常转为 {code, message} JSON 响应。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Map<String, Object> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", e.getCode());
        result.put("message", e.getMessage());
        return result;
    }

    /**
     * 数据库唯一键冲突
     */
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> handleDuplicateKeyException(DuplicateKeyException e) {
        log.warn("数据唯一键冲突: {}", e.getMessage());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 400);
        // 从异常信息中提取友好的提示
        String message = e.getMessage();
        if (message != null && message.contains("account")) {
            result.put("message", "该账号已存在，请更换账号");
        } else if (message != null && message.contains("phone")) {
            result.put("message", "该手机号已被使用");
        } else {
            result.put("message", "数据已存在，请检查后重试");
        }
        return result;
    }

    /**
     * 参数校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> handleValidationException(MethodArgumentNotValidException e) {
        String defaultMessage = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "参数校验失败";
        log.warn("参数校验失败: {}", defaultMessage);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 400);
        result.put("message", defaultMessage);
        return result;
    }

    /**
     * 未预期异常兜底
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleException(Exception e) {
        log.error("系统内部错误", e);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 500);
        result.put("message", "系统内部错误，请联系管理员");
        return result;
    }
}
