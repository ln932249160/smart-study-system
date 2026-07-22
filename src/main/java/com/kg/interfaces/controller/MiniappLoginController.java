package com.kg.interfaces.controller;

import com.kg.application.service.MiniappLoginService;
import com.kg.interfaces.dto.MiniappLoginRequest;
import com.kg.interfaces.dto.MiniappLoginVO;
import com.kg.interfaces.dto.MiniappPhoneLoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

/** 小程序登录控制器 */
@Tag(name = "认证", description = "登录相关接口")
@RestController
public class MiniappLoginController {
    private final MiniappLoginService service;
    public MiniappLoginController(MiniappLoginService service) { this.service = service; }

    @Operation(summary = "小程序快捷登录（openid）")
    @PostMapping("/miniapp/login")
    public Map<String, Object> login(@Valid @RequestBody MiniappLoginRequest req) {
        MiniappLoginVO data = service.loginByOpenid(req.getLoginCode());
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200); r.put("message", data.isNeedPhoneAuth() ? "请授权手机号完成绑定" : "登录成功");
        r.put("data", data);
        return r;
    }

    @Operation(summary = "小程序手机号登录并绑定openid")
    @PostMapping("/miniapp/phone-login")
    public Map<String, Object> phoneLogin(@Valid @RequestBody MiniappPhoneLoginRequest req) {
        MiniappLoginVO data = service.loginByPhone(req.getLoginCode(), req.getPhoneCode());
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", 200); r.put("message", "登录成功"); r.put("data", data);
        return r;
    }
}
