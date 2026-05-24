package com.kg.domain.repository;

import com.kg.domain.model.User;
import java.util.Optional;

/**
 * 仓储接口 — 只定义契约，不依赖数据库实现。
 */
public interface UserRepository {
    Optional<User> findById(Long id);
    void save(User user);
}
