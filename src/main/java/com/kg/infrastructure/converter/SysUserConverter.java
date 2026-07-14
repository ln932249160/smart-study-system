package com.kg.infrastructure.converter;

import com.kg.domain.model.SysUser;
import com.kg.infrastructure.entity.SysUserEntity;

/**
 * 防腐层转换器 —— domain.SysUser ↔ infra.SysUserEntity 双向映射。
 * 16 个字段全部映射，不省略任何列。
 */
public class SysUserConverter {

    private SysUserConverter() {
        // 工具类不可实例化
    }

    /**
     * Entity → Domain
     */
    public static SysUser toDomain(SysUserEntity entity) {
        if (entity == null) {
            return null;
        }
        SysUser domain = new SysUser();
        domain.setId(entity.getId());
        domain.setAccount(entity.getAccount());
        domain.setName(entity.getName());
        domain.setPassword(entity.getPassword());
        domain.setRole(entity.getRole());
        domain.setGender(entity.getGender());
        domain.setEmail(entity.getEmail());
        domain.setPhone(entity.getPhone());
        domain.setOpenid(entity.getOpenid());
        domain.setDescription(entity.getDescription());
        domain.setTeacherRemark(entity.getTeacherRemark());
        domain.setPasswordUpdateTime(entity.getPasswordUpdateTime());
        domain.setClassId(entity.getClassId());
        domain.setStatus(entity.getStatus());
        domain.setCreateBy(entity.getCreateBy());
        domain.setCreateTime(entity.getCreateTime());
        domain.setUpdateBy(entity.getUpdateBy());
        domain.setUpdateTime(entity.getUpdateTime());
        return domain;
    }

    /**
     * Domain → Entity
     */
    public static SysUserEntity toEntity(SysUser domain) {
        if (domain == null) {
            return null;
        }
        SysUserEntity entity = new SysUserEntity();
        entity.setId(domain.getId());
        entity.setAccount(domain.getAccount());
        entity.setName(domain.getName());
        entity.setPassword(domain.getPassword());
        entity.setRole(domain.getRole());
        entity.setGender(domain.getGender());
        entity.setEmail(domain.getEmail());
        entity.setPhone(domain.getPhone());
        entity.setOpenid(domain.getOpenid());
        entity.setDescription(domain.getDescription());
        entity.setTeacherRemark(domain.getTeacherRemark());
        entity.setPasswordUpdateTime(domain.getPasswordUpdateTime());
        entity.setClassId(domain.getClassId());
        entity.setStatus(domain.getStatus());
        entity.setCreateBy(domain.getCreateBy());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateBy(domain.getUpdateBy());
        entity.setUpdateTime(domain.getUpdateTime());
        return entity;
    }
}
