package com.kg.interfaces.dto;

import com.kg.domain.model.User;

/**
 * 对外响应 DTO — 隔离领域模型。
 */
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String createAt;

    public static UserResponse from(User user) {
        UserResponse dto = new UserResponse();
        dto.id = user.getId();
        dto.username = user.getUsername();
        dto.email = user.getEmail();
        dto.createAt = user.getCreateAt() != null ? user.getCreateAt().toString() : null;
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCreateAt() { return createAt; }
    public void setCreateAt(String createAt) { this.createAt = createAt; }
}
