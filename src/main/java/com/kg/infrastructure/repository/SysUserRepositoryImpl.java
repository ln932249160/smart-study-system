package com.kg.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kg.domain.model.SysUser;
import com.kg.domain.repository.SysUserRepository;
import com.kg.infrastructure.converter.SysUserConverter;
import com.kg.infrastructure.entity.SysUserEntity;
import com.kg.infrastructure.mapper.SysUserMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 系统用户仓储实现 —— 使用 MyBatis-Plus 操作 MySQL。
 */
@Repository
public class SysUserRepositoryImpl implements SysUserRepository {

    private final SysUserMapper sysUserMapper;

    public SysUserRepositoryImpl(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public Optional<SysUser> findByAccount(String account) {
        LambdaQueryWrapper<SysUserEntity> query = new LambdaQueryWrapper<>();
        query.eq(SysUserEntity::getAccount, account);
        SysUserEntity entity = sysUserMapper.selectOne(query);
        return Optional.ofNullable(SysUserConverter.toDomain(entity));
    }
}
