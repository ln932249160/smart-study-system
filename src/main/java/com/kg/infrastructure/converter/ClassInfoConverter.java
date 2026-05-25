package com.kg.infrastructure.converter;

import com.kg.domain.model.ClassInfo;
import com.kg.infrastructure.entity.ClassInfoEntity;

/**
 * 防腐层转换器 —— domain.ClassInfo ↔ infra.ClassInfoEntity 双向映射。
 */
public class ClassInfoConverter {

    private ClassInfoConverter() {
        // 工具类不可实例化
    }

    /**
     * Entity → Domain
     */
    public static ClassInfo toDomain(ClassInfoEntity entity) {
        if (entity == null) {
            return null;
        }
        ClassInfo domain = new ClassInfo();
        domain.setId(entity.getId());
        domain.setClassName(entity.getClassName());
        domain.setDescription(entity.getDescription());
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
    public static ClassInfoEntity toEntity(ClassInfo domain) {
        if (domain == null) {
            return null;
        }
        ClassInfoEntity entity = new ClassInfoEntity();
        entity.setId(domain.getId());
        entity.setClassName(domain.getClassName());
        entity.setDescription(domain.getDescription());
        entity.setStatus(domain.getStatus());
        entity.setCreateBy(domain.getCreateBy());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateBy(domain.getUpdateBy());
        entity.setUpdateTime(domain.getUpdateTime());
        return entity;
    }
}
