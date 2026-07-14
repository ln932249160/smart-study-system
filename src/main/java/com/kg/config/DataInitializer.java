package com.kg.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kg.enums.RoleEnum;
import com.kg.infrastructure.entity.SysUserEntity;
import com.kg.infrastructure.mapper.SysUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器 —— 启动时检查并创建默认管理员账号。
 * dev 环境：root 不存在则创建，已存在则跳过（不重置密码）。
 * prod 环境：直接跳过。
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private static final String DEFAULT_ACCOUNT = "root";
    private static final String DEFAULT_PASSWORD = "000000";
    private static final String DEFAULT_ROLE = RoleEnum.TEACHER.getCode();
    private static final int STATUS_ACTIVE = 1;

    @Value("${system.init-root-enabled:true}")
    private boolean initRootEnabled;

    private final SysUserMapper sysUserMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(SysUserMapper sysUserMapper, BCryptPasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!initRootEnabled) {
            log.info("默认管理员初始化已关闭（system.init-root-enabled=false），跳过");
            return;
        }

        LambdaQueryWrapper<SysUserEntity> query = new LambdaQueryWrapper<>();
        query.eq(SysUserEntity::getAccount, DEFAULT_ACCOUNT);
        SysUserEntity existing = sysUserMapper.selectOne(query);

        if (existing != null) {
            log.info("默认管理员账号已存在，跳过初始化：account={}", DEFAULT_ACCOUNT);
            return;
        }

        SysUserEntity admin = new SysUserEntity();
        admin.setAccount(DEFAULT_ACCOUNT);
        admin.setName("管理员");
        admin.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        admin.setRole(DEFAULT_ROLE);
        admin.setStatus(STATUS_ACTIVE);
        sysUserMapper.insert(admin);
        log.info("默认管理员账号创建成功：account={}, role={}", DEFAULT_ACCOUNT, DEFAULT_ROLE);
    }
}
