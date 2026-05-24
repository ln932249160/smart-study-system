package com.kg.infrastructure.repository;

import com.kg.domain.model.User;
import com.kg.domain.repository.UserRepository;
import com.kg.infrastructure.converter.UserConverter;
import com.kg.infrastructure.entity.UserEntity;
import com.kg.infrastructure.mapper.UserMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 仓储实现 — 使用 MyBatis-Plus 操作 MySQL。
 * 切换数据库时替换此类即可，domain 层不受影响。
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    public UserRepositoryImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public Optional<User> findById(Long id) {
        UserEntity entity = userMapper.selectById(id);
        return Optional.ofNullable(UserConverter.toDomain(entity));
    }

    @Override
    public void save(User user) {
        UserEntity entity = UserConverter.toEntity(user);
        if (user.getId() == null) {
            userMapper.insert(entity);
            user.setId(entity.getId());
        } else {
            userMapper.updateById(entity);
        }
    }
}
