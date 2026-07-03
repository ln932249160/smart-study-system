package com.kg.interfaces.controller;

import com.kg.application.service.ProfileApplicationService;
import com.kg.interfaces.dto.PasswordChangeRequest;
import com.kg.interfaces.dto.ProfileUpdateRequest;
import com.kg.interfaces.dto.ProfileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 个人中心控制器。
 */
@Tag(name = "个人中心", description = "个人信息查看与修改")
@RestController
public class ProfileController {

    private final ProfileApplicationService profileApplicationService;

    public ProfileController(ProfileApplicationService profileApplicationService) {
        this.profileApplicationService = profileApplicationService;
    }

    /**
     * 获取当前用户个人信息。
     */
    @Operation(summary = "查看个人信息")
    @GetMapping("/profile")
    public Map<String, Object> getProfile() {
        ProfileVO data = profileApplicationService.getProfile();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", data);
        return result;
    }

    /**
     * 修改个人信息（name/gender/email/phone/description）。
     */
    @Operation(summary = "修改个人信息")
    @PutMapping("/profile")
    public Map<String, Object> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        profileApplicationService.updateProfile(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200);
        result.put("message", "修改成功");
        return result;
    }

    /**
     * 修改密码（需验证原密码）。
     */
    @Operation(summary = "修改密码")
    @PutMapping("/profile/password")
    public Map<String, Object> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        profileApplicationService.changePassword(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200);
        result.put("message", "密码修改成功");
        return result;
    }
}
