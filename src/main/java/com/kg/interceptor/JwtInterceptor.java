package com.kg.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kg.context.UserContext;
import com.kg.domain.model.SysUser;
import com.kg.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JWT 认证拦截器。
 * <p>
 * 从 Authorization 请求头提取 Bearer Token，校验有效性，解析出 userId 和 role
 * 构建 SysUser 后存入 {@link com.kg.context.UserContext}。
 * 请求结束后在 afterCompletion 中清理 ThreadLocal。
 * </p>
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtInterceptor.class);

    /** Authorization 请求头 */
    private static final String HEADER_AUTH = "Authorization";

    /** Bearer Token 前缀 */
    private static final String BEARER_PREFIX = "Bearer ";

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 请求前置处理：提取 Token → 校验 → 解析 → 存入 UserContext。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String authHeader = request.getHeader(HEADER_AUTH);

        // 1. 检查 Authorization 头
        if (authHeader == null || authHeader.isEmpty()) {
            writeUnauthorized(response, "缺少 Authorization 请求头");
            return false;
        }

        // 2. 检查 Bearer 前缀
        if (!authHeader.startsWith(BEARER_PREFIX)) {
            writeUnauthorized(response, "Authorization 格式错误，需要 Bearer Token");
            return false;
        }

        // 3. 提取 Token
        String token = authHeader.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            writeUnauthorized(response, "Token 不能为空");
            return false;
        }

        // 4. 校验 Token 有效性
        if (!JwtUtil.validateToken(token)) {
            writeUnauthorized(response, "Token 无效或已过期");
            return false;
        }

        // 5. 解析 Token 中的 userId 和 role
        Long userId;
        String role;
        try {
            userId = JwtUtil.getUserId(token);
            role = JwtUtil.getRole(token);
        } catch (Exception e) {
            log.warn("JWT 解析失败: {}", e.getMessage());
            writeUnauthorized(response, "Token 解析失败");
            return false;
        }

        // 6. 构建 SysUser 存入 ThreadLocal
        SysUser sysUser = new SysUser();
        sysUser.setId(userId);
        sysUser.setRole(role);
        UserContext.setUser(sysUser);

        return true;
    }

    /**
     * 请求完成回调 —— 清理 ThreadLocal，防止内存泄漏。
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }

    /**
     * 输出 401 JSON 响应。
     */
    private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> body = new LinkedHashMap<>(2);
        body.put("code", 401);
        body.put("message", message);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
