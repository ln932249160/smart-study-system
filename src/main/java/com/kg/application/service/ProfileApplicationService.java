package com.kg.application.service;

import com.kg.context.UserContext;
import com.kg.domain.model.ClassInfo;
import com.kg.domain.model.SysUser;
import com.kg.domain.repository.ClassInfoRepository;
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
    private final ClassInfoRepository classInfoRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public ProfileApplicationService(SysUserRepository sysUserRepository,
                                     ClassInfoRepository classInfoRepository,
                                     BCryptPasswordEncoder passwordEncoder) {
        this.sysUserRepository = sysUserRepository;
        this.classInfoRepository = classInfoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 获取当前用户个人信息。
     */
    public ProfileVO getProfile() {
        SysUser fullUser = loadFullUser();
        return toVO(fullUser);
    }

    /**
     * 修改个人信息（name / gender / email / phone / description）。
     */
    public void updateProfile(ProfileUpdateRequest request) {
        Long userId = getCurrentUserId();
        SysUser update = new SysUser();
        update.setId(userId);
        if (request.getName() != null) update.setName(request.getName().trim());
        if (request.getGender() != null) update.setGender(request.getGender());
        if (request.getEmail() != null) update.setEmail(request.getEmail());
        if (request.getPhone() != null) update.setPhone(request.getPhone().trim());
        if (request.getDescription() != null) update.setDescription(request.getDescription());
        update.setUpdateBy(userId);
        sysUserRepository.update(update);
        log.info("修改个人信息成功: userId={}", userId);
    }

    /**
     * 修改密码：从 DB 查出完整用户（含 BCrypt 密码）→ 验原密码 → 加密新密码 → 更新。
     */
    public void changePassword(PasswordChangeRequest request) {
        Long userId = getCurrentUserId();
        // 从 DB 加载完整用户，ThreadLocal 里只有 id + role，没有 password
        SysUser fullUser = sysUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // BCrypt 验证原密码
        if (!passwordEncoder.matches(request.getOldPassword(), fullUser.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // BCrypt 加密新密码
        SysUser update = new SysUser();
        update.setId(userId);
        update.setPassword(passwordEncoder.encode(request.getNewPassword()));
        update.setUpdateBy(userId);
        sysUserRepository.update(update);
        log.info("修改密码成功: userId={}", userId);
    }

    // ======================== 工具 ========================

    private Long getCurrentUserId() {
        SysUser user = UserContext.getUser();
        if (user == null) throw new BusinessException(401, "未登录");
        return user.getId();
    }

    /** 从 DB 加载完整用户信息 */
    private SysUser loadFullUser() {
        return sysUserRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new BusinessException("用户不存在"));
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
        if (user.getClassId() != null) {
            classInfoRepository.findById(user.getClassId())
                    .ifPresent(c -> vo.setClassName(c.getClassName()));
        }
        vo.setStatus(user.getStatus());
        if (user.getCreateTime() != null) {
            vo.setCreateTime(user.getCreateTime().format(FMT));
        }
        return vo;
    }
}
