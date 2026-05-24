package com.kg.domain.service;

import com.kg.domain.model.User;
import com.kg.domain.repository.UserRepository;

/**
 * 领域服务 — 封装与 User 聚合相关的业务规则。
 */
public class UserDomainService {

    private final UserRepository userRepository;

    public UserDomainService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: id=" + id));
    }
}
