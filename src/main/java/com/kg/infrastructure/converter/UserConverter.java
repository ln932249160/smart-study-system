package com.kg.infrastructure.converter;

import com.kg.domain.model.User;
import com.kg.infrastructure.entity.UserEntity;

/**
 * 防腐层转换器 — domain.User ↔ infra.UserEntity 双向映射。
 */
public class UserConverter {

    public static User toDomain(UserEntity entity) {
        if (entity == null) return null;
        User user = new User();
        user.setId(entity.getId());
        user.setUsername(entity.getUsername());
        user.setEmail(entity.getEmail());
        user.setCreateAt(entity.getCreateAt());
        return user;
    }

    public static UserEntity toEntity(User user) {
        if (user == null) return null;
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setCreateAt(user.getCreateAt());
        return entity;
    }
}
