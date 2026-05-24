package com.kg.interfaces.controller;

import com.kg.application.service.UserApplicationService;
import com.kg.domain.model.User;
import com.kg.interfaces.dto.UserResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserApplicationService userApplicationService;

    public UserController(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    @GetMapping("/user/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        User user = userApplicationService.getUser(id);
        return UserResponse.from(user);
    }
}
