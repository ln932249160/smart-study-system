package com.kg.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kg.domain.model.SysUser;
import com.kg.domain.repository.SysUserRepository;
import com.kg.infrastructure.converter.SysUserConverter;
import com.kg.infrastructure.entity.SysUserEntity;
import com.kg.infrastructure.mapper.SysUserMapper;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public void save(SysUser user) {
        SysUserEntity entity = SysUserConverter.toEntity(user);
        sysUserMapper.insert(entity);
        user.setId(entity.getId());
    }

    @Override
    public Optional<SysUser> findById(Long id) {
        return Optional.ofNullable(SysUserConverter.toDomain(sysUserMapper.selectById(id)));
    }

    @Override
    public Optional<SysUser> findByAccount(String account) {
        LambdaQueryWrapper<SysUserEntity> query = new LambdaQueryWrapper<>();
        query.eq(SysUserEntity::getAccount, account);
        SysUserEntity entity = sysUserMapper.selectOne(query);
        return Optional.ofNullable(SysUserConverter.toDomain(entity));
    }

    @Override
    public List<SysUser> pageByFilters(String name, String role, String phone, Long userId,
                                        List<Long> classIds, List<String> managedRoles,
                                        int offset, int limit) {
        LambdaQueryWrapper<SysUserEntity> q = buildFilterQuery(name, role, phone, userId, classIds, managedRoles);
        q.orderByDesc(SysUserEntity::getId);
        q.last("LIMIT " + offset + "," + limit);
        List<SysUserEntity> entities = sysUserMapper.selectList(q);
        if (entities == null || entities.isEmpty()) return Collections.emptyList();
        return entities.stream().map(SysUserConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countByFilters(String name, String role, String phone, Long userId,
                                List<Long> classIds, List<String> managedRoles) {
        return sysUserMapper.selectCount(buildFilterQuery(name, role, phone, userId, classIds, managedRoles));
    }

    /** 构建多条件过滤查询 */
    private LambdaQueryWrapper<SysUserEntity> buildFilterQuery(String name, String role, String phone,
                                                                Long userId, List<Long> classIds,
                                                                List<String> managedRoles) {
        LambdaQueryWrapper<SysUserEntity> q = new LambdaQueryWrapper<>();
        q.in(managedRoles != null && !managedRoles.isEmpty(), SysUserEntity::getRole, managedRoles);
        q.eq(role != null && !role.isEmpty(), SysUserEntity::getRole, role);
        q.like(name != null && !name.isEmpty(), SysUserEntity::getName, name);
        q.like(phone != null && !phone.isEmpty(), SysUserEntity::getPhone, phone);
        q.eq(userId != null, SysUserEntity::getId, userId);
        q.in(classIds != null && !classIds.isEmpty(), SysUserEntity::getClassId, classIds);
        return q;
    }

    @Override
    public void update(SysUser user) {
        LambdaUpdateWrapper<SysUserEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysUserEntity::getId, user.getId());
        if (user.getAccount() != null) {
            wrapper.set(SysUserEntity::getAccount, user.getAccount());
        }
        if (user.getName() != null) {
            wrapper.set(SysUserEntity::getName, user.getName());
        }
        if (user.getPassword() != null) {
            wrapper.set(SysUserEntity::getPassword, user.getPassword());
        }
        if (user.getRole() != null) {
            wrapper.set(SysUserEntity::getRole, user.getRole());
        }
        if (user.getGender() != null) {
            wrapper.set(SysUserEntity::getGender, user.getGender());
        }
        if (user.getEmail() != null) {
            wrapper.set(SysUserEntity::getEmail, user.getEmail());
        }
        if (user.getPhone() != null) {
            wrapper.set(SysUserEntity::getPhone, user.getPhone());
        }
        if (user.getDescription() != null) {
            wrapper.set(SysUserEntity::getDescription, user.getDescription());
        }
        if (user.getClassId() != null) {
            wrapper.set(SysUserEntity::getClassId, user.getClassId());
        }
        if (user.getStatus() != null) {
            wrapper.set(SysUserEntity::getStatus, user.getStatus());
        }
        if (user.getUpdateBy() != null) {
            wrapper.set(SysUserEntity::getUpdateBy, user.getUpdateBy());
        }
        sysUserMapper.update(null, wrapper);
    }

    @Override
    public void deleteById(Long id) {
        sysUserMapper.deleteById(id);
    }

    @Override
    public List<SysUser> findByClassId(Long classId) {
        LambdaQueryWrapper<SysUserEntity> query = new LambdaQueryWrapper<>();
        query.eq(SysUserEntity::getClassId, classId);
        List<SysUserEntity> entities = sysUserMapper.selectList(query);
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream().map(SysUserConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countByClassId(Long classId) {
        LambdaQueryWrapper<SysUserEntity> query = new LambdaQueryWrapper<>();
        query.eq(SysUserEntity::getClassId, classId);
        return sysUserMapper.selectCount(query);
    }

    @Override
    public void clearClassId(Long classId) {
        LambdaUpdateWrapper<SysUserEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysUserEntity::getClassId, classId);
        wrapper.set(SysUserEntity::getClassId, null);
        sysUserMapper.update(null, wrapper);
    }

    @Override
    public void batchUpdateClassId(List<Long> userIds, Long classId) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        LambdaUpdateWrapper<SysUserEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysUserEntity::getId, userIds);
        wrapper.set(SysUserEntity::getClassId, classId);
        sysUserMapper.update(null, wrapper);
    }

    @Override
    public List<SysUser> listByRoles(List<String> roles) {
        LambdaQueryWrapper<SysUserEntity> q = new LambdaQueryWrapper<>();
        q.in(SysUserEntity::getRole, roles);
        q.eq(SysUserEntity::getStatus, 1);
        List<SysUserEntity> entities = sysUserMapper.selectList(q);
        if (entities == null || entities.isEmpty()) return Collections.emptyList();
        return entities.stream().map(SysUserConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<SysUser> listStudentsByRole(String role) {
        LambdaQueryWrapper<SysUserEntity> query = new LambdaQueryWrapper<>();
        query.eq(SysUserEntity::getRole, role);
        query.eq(SysUserEntity::getStatus, 1);
        query.orderByAsc(SysUserEntity::getAccount);
        List<SysUserEntity> entities = sysUserMapper.selectList(query);
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream().map(SysUserConverter::toDomain).collect(Collectors.toList());
    }
}
