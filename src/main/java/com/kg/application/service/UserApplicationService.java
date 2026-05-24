package com.kg.application.service;

import com.kg.domain.model.User;
import com.kg.domain.service.UserDomainService;
import org.springframework.stereotype.Service;

/**
 * 应用层服务 — 编排业务流程。
 */
@Service
public class UserApplicationService {

    private final UserDomainService userDomainService;

    public UserApplicationService(UserDomainService userDomainService) {
        this.userDomainService = userDomainService;
    }

    public User getUser(Long id) {
        return userDomainService.findUserById(id);
    }
}
