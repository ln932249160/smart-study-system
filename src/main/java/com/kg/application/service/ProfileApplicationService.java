package com.kg.application.service;

import com.kg.context.UserContext;
import com.kg.domain.model.SysUser;
import com.kg.domain.repository.SysUserRepository;
import com.kg.exception.BusinessException;
import com.kg.interfaces.dto.PasswordChangeRequest;
import com.kg.interfaces.dto.ProfileUpdateRequest;
import com.kg.interfaces.dto.ProfileVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * 个人中心应用服务 —— 查看/修改个人信息、修改密码。
 */
@Service
public class ProfileApplicationService {

    private static final Logger log = LoggerFactory.getLogger(ProfileApplicationService.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SysUserRepository sysUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public ProfileApplicationService(SysUserRepository sysUserRepository,
                                     BCryptPasswordEncoder passwordEncoder) {
        this.sysUserRepository = sysUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 获取当前用户个人信息。
     */
    public ProfileVO getProfile() {
        SysUser user = requireUser();
        return toVO(user);
    }

    /**
     * 修改个人信息（name / gender / email / phone / description）。
     */
    public void updateProfile(ProfileUpdateRequest request) {
        SysUser user = requireUser();
        SysUser update = new SysUser();
        update.setId(user.getId());
        if (request.getName() != null) update.setName(request.getName().trim());
        if (request.getGender() != null) update.setGender(request.getGender());
        if (request.getEmail() != null) update.setEmail(request.getEmail());
        if (request.getPhone() != null) update.setPhone(request.getPhone().trim());
        if (request.getDescription() != null) update.setDescription(request.getDescription());
        update.setUpdateBy(user.getId());
        sysUserRepository.update(update);
        log.info("修改个人信息成功: userId={}", user.getId());
    }

    /**
     * 修改密码：验证原密码 → BCrypt 加密新密码 → 更新。
     */
    public void changePassword(PasswordChangeRequest request) {
        SysUser user = requireUser();

        // 验证原密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 更新为新密码
        SysUser update = new SysUser();
        update.setId(user.getId());
        update.setPassword(passwordEncoder.encode(request.getNewPassword()));
        update.setUpdateBy(user.getId());
        sysUserRepository.update(update);
        log.info("修改密码成功: userId={}", user.getId());
    }

    // ======================== 工具 ========================

    private SysUser requireUser() {
        SysUser user = UserContext.getUser();
        if (user == null) throw new BusinessException(401, "未登录");
        return user;
    }

    private ProfileVO toVO(SysUser user) {
        ProfileVO vo = new ProfileVO();
        vo.setId(user.getId());
        vo.setAccount(user.getAccount());
        vo.setName(user.getName());
        vo.setRole(user.getRole());
        vo.setGender(user.getGender());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setDescription(user.getDescription());
        vo.setClassId(user.getClassId());
        vo.setStatus(user.getStatus());
        if (user.getCreateTime() != null) {
            vo.setCreateTime(user.getCreateTime().format(FMT));
        }
        return vo;
    }
}
