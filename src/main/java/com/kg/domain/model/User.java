package com.kg.domain.model;

import java.time.LocalDateTime;

/**
 * 领域模型 — 纯 POJO，不依赖任何框架
 */
public class User {
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createAt;

    public static User create(String username, String email) {
        User user = new User();
        user.username = username;
        user.email = email;
        user.createAt = LocalDateTime.now();
        return user;
    }

    public void changeEmail(String newEmail) {
        if (newEmail == null || !newEmail.contains("@")) {
            throw new IllegalArgumentException("Invalid email: " + newEmail);
        }
        this.email = newEmail;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getCreateAt() { return createAt; }
    public void setCreateAt(LocalDateTime createAt) { this.createAt = createAt; }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', email='" + email + "'}";
    }
}
