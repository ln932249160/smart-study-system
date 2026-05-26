package com.kg.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kg.enums.RoleEnum;
import com.kg.infrastructure.entity.SysUserEntity;
import com.kg.infrastructure.mapper.SysUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器 —— 启动时检查并创建默认管理员账号。
 * <p>
 * 仅当 sys_user 表中不存在 account='root' 的记录时，使用 BCrypt 加密默认密码后插入。
 * </p>
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    /** 默认管理员账号 */
    private static final String DEFAULT_ACCOUNT = "root";

    /** 默认管理员密码（明文） */
    private static final String DEFAULT_PASSWORD = "000000";

    /** 默认管理员角色 */
    private static final String DEFAULT_ROLE = RoleEnum.TEACHER.getCode();

    /** 正常状态 */
    private static final int STATUS_ACTIVE = 1;

    private final SysUserMapper sysUserMapper;

    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(SysUserMapper sysUserMapper, BCryptPasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 应用启动后执行，若默认管理员不存在则自动创建。
     */
    @Override
    public void run(String... args) {
        LambdaQueryWrapper<SysUserEntity> query = new LambdaQueryWrapper<>();
        query.eq(SysUserEntity::getAccount, DEFAULT_ACCOUNT);
        SysUserEntity existing = sysUserMapper.selectOne(query);

        if (existing != null) {
            log.info("默认管理员账号已存在：account={}", DEFAULT_ACCOUNT);
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
