package com.kg.application.service;

import com.kg.domain.model.SysUser;
import com.kg.domain.repository.SysUserRepository;
import com.kg.interfaces.dto.LoginResponse;
import com.kg.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证应用服务 —— 处理登录业务流程。
 */
@Service
public class AuthApplicationService {

    private static final Logger log = LoggerFactory.getLogger(AuthApplicationService.class);

    /** 正常状态 */
    private static final int STATUS_ACTIVE = 1;

    private final SysUserRepository sysUserRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public AuthApplicationService(SysUserRepository sysUserRepository, BCryptPasswordEncoder passwordEncoder) {
        this.sysUserRepository = sysUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 账号密码登录。
     * <p>
     * 从 sys_user 表按 account 查询用户，校验账号状态 + BCrypt 密码，
     * 成功后生成 JWT Token（payload 仅含 userId 和 role）。
     * </p>
     *
     * @param account  账号
     * @param password 明文密码
     * @return 登录响应（含 JWT Token 和用户基本信息）
     * @throws IllegalArgumentException 账号不存在、被禁用或密码错误
     */
    public LoginResponse login(String account, String password) {
        SysUser sysUser = sysUserRepository.findByAccount(account)
                .orElseThrow(() -> {
                    log.warn("登录失败 — 账号不存在: account={}", account);
                    return new IllegalArgumentException("账号或密码错误");
                });

        // 检查账号状态
        if (sysUser.getStatus() == null || sysUser.getStatus() != STATUS_ACTIVE) {
            log.warn("登录失败 — 账号已禁用: account={}, status={}", account, sysUser.getStatus());
            throw new IllegalArgumentException("账号已被禁用");
        }

        // BCrypt 密码校验
        if (!passwordEncoder.matches(password, sysUser.getPassword())) {
            log.warn("登录失败 — 密码错误: account={}", account);
            throw new IllegalArgumentException("账号或密码错误");
        }

        String token = JwtUtil.generateToken(sysUser.getId(), sysUser.getRole());
        log.info("登录成功: userId={}, account={}, role={}", sysUser.getId(), account, sysUser.getRole());

        return LoginResponse.of(token, sysUser.getId(), sysUser.getAccount(), sysUser.getRole());
    }
}
