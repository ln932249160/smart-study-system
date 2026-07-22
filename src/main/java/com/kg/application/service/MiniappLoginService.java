package com.kg.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kg.domain.model.SysUser;
import com.kg.domain.repository.SysUserRepository;
import com.kg.enums.RoleEnum;
import com.kg.exception.BusinessException;
import com.kg.infrastructure.entity.SysUserEntity;
import com.kg.infrastructure.mapper.SysUserMapper;
import com.kg.interfaces.dto.MiniappLoginVO;
import com.kg.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/** 小程序登录服务 */
@Service
public class MiniappLoginService {
    private static final Logger log = LoggerFactory.getLogger(MiniappLoginService.class);

    private final WechatMiniappService wechatService;
    private final SysUserMapper sysUserMapper;
    private final SysUserRepository sysUserRepo;
    private final JwtUtil jwtUtil;

    public MiniappLoginService(WechatMiniappService wechatService, SysUserMapper sysUserMapper,
                                SysUserRepository sysUserRepo, JwtUtil jwtUtil) {
        this.wechatService = wechatService;
        this.sysUserMapper = sysUserMapper;
        this.sysUserRepo = sysUserRepo;
        this.jwtUtil = jwtUtil;
    }

    /** 快捷登录：openid 已绑定则直接登录，否则返回 needPhoneAuth */
    public MiniappLoginVO loginByOpenid(String loginCode) {
        String openid = wechatService.code2Session(loginCode);
        SysUserEntity user = findByOpenid(openid);
        if (user == null) {
            MiniappLoginVO vo = new MiniappLoginVO();
            vo.setNeedPhoneAuth(true);
            return vo;
        }
        checkCanLogin(user);
        return buildLoginVO(user);
    }

    /** 手机号登录：phoneCode 换手机号 → 匹配用户 → 绑定 openid → 登录 */
    public MiniappLoginVO loginByPhone(String loginCode, String phoneCode) {
        String openid = wechatService.code2Session(loginCode);
        String phone = wechatService.getPhoneNumber(phoneCode);

        // 查该 openid 是否已绑定其他用户
        SysUserEntity openidUser = findByOpenid(openid);
        if (openidUser != null && !phone.equals(openidUser.getPhone())) {
            throw new BusinessException("当前微信已绑定其他账号，请联系老师处理");
        }

        // 按手机号查用户
        LambdaQueryWrapper<SysUserEntity> q = new LambdaQueryWrapper<>();
        q.eq(SysUserEntity::getPhone, phone);
        List<SysUserEntity> list = sysUserMapper.selectList(q);
        if (list.isEmpty()) {
            throw new BusinessException("当前手机号未绑定学员账号，请联系老师添加或修改手机号");
        }
        if (list.size() > 1) {
            throw new BusinessException("当前手机号绑定多个账号，请联系管理员处理");
        }

        SysUserEntity user = list.get(0);
        checkCanLogin(user);

        // 绑定 openid
        if (user.getOpenid() == null || user.getOpenid().isEmpty()) {
            user.setOpenid(openid);
            sysUserMapper.updateById(user);
            log.info("openid绑定成功: userId={}", user.getId());
        } else if (!openid.equals(user.getOpenid())) {
            throw new BusinessException("该手机号已绑定其他微信，请联系老师处理");
        }

        return buildLoginVO(user);
    }

    private SysUserEntity findByOpenid(String openid) {
        LambdaQueryWrapper<SysUserEntity> q = new LambdaQueryWrapper<>();
        q.eq(SysUserEntity::getOpenid, openid);
        return sysUserMapper.selectOne(q);
    }

    /** 校验角色和状态 */
    private void checkCanLogin(SysUserEntity user) {
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("账号已禁用，请联系老师");
        }
        if (RoleEnum.TEACHER.getCode().equals(user.getRole())) {
            throw new BusinessException("老师请使用 PC 端登录");
        }
        // student / headmaster 允许登录
    }

    private MiniappLoginVO buildLoginVO(SysUserEntity user) {
        String token = jwtUtil.generateToken(user.getId(), user.getRole());

        MiniappLoginVO vo = new MiniappLoginVO();
        vo.setNeedPhoneAuth(false);
        vo.setToken(token);

        MiniappLoginVO.UserInfo ui = new MiniappLoginVO.UserInfo();
        ui.setId(user.getId()); ui.setAccount(user.getAccount());
        ui.setName(user.getName()); ui.setRole(user.getRole());
        ui.setClassId(user.getClassId()); ui.setPhone(user.getPhone());
        vo.setUserInfo(ui);
        return vo;
    }
}
