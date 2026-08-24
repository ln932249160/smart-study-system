package com.kg.interfaces.controller;

import com.kg.interfaces.dto.AjaxResult;
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
     */
    @Operation(summary = "账号密码登录")
    @PostMapping("/auth/login")
    public Map<String, Object> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authApplicationService.login(
                request.getAccount(), request.getPassword());

        return AjaxResult.success("登录成功", loginResponse);
    }
}
