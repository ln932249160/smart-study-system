package com.kg.interfaces.controller;

import com.kg.application.service.AuthApplicationService;
import com.kg.interfaces.dto.LoginRequest;
import com.kg.interfaces.dto.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 认证接口控制器
 */
@Tag(name = "认证", description = "登录相关接口")
@RestController
public class AuthController {

    private final AuthApplicationService authApplicationService;

    public AuthController(AuthApplicationService authApplicationService) {
        this.authApplicationService = authApplicationService;
    }

    /**
     * 账号密码登录。
     * <p>
     * 使用 sys_user 表中的 account + password 进行认证，成功返回 JWT Token。
     * Token payload 包含 userId 和 role。
     * </p>
     *
     * @param request 登录请求（account + password）
     * @return 统一响应体，code=200 时 data 为 LoginResponse
     */
    @Operation(summary = "账号密码登录")
    @PostMapping("/auth/login")
    public Map<String, Object> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse loginResponse = authApplicationService.login(
                    request.getAccount(), request.getPassword());

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("code", 200);
            result.put("message", "登录成功");
            result.put("data", loginResponse);
            return result;
        } catch (IllegalArgumentException e) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("code", 401);
            result.put("message", e.getMessage());
            return result;
        }
    }
}
