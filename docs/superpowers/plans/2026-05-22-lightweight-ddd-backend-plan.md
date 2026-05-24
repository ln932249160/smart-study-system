# Lightweight DDD Backend Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 创建一个基于 Java8 + Spring Boot 2.7 + MyBatis-Plus 的轻量 DDD 后端项目，包含用户模块和两个 REST 端点。

**Architecture:** 四层分离 — interfaces(Controller+DTO) → application(AppService) → domain(Model+Repository接口+DomainService) ← infrastructure(Entity+Mapper+RepositoryImpl+Converter)。Domain 层零框架依赖，infrastructure 实现 domain 接口实现防腐。

**Tech Stack:** Java 8, Spring Boot 2.7.18, MyBatis-Plus 3.5.3.1, MySQL 8.0.33, Maven

---

### Task 1: Maven 项目配置

**Files:**
- Create: `pom.xml`

- [ ] **Step 1: 创建 pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.18</version>
        <relativePath/>
    </parent>

    <groupId>com.kg</groupId>
    <artifactId>kg-backend</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    <name>kg-backend</name>

    <properties>
        <java.version>1.8</java.version>
        <mybatis-plus.version>3.5.3.1</mybatis-plus.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- MyBatis-Plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>

        <!-- MySQL Driver -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.33</version>
        </dependency>

        <!-- Spring Boot Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: 验证配置**

```bash
cd /c/Users/86198/Desktop/kg && mvn validate
```
Expected: BUILD SUCCESS

---

### Task 2: 配置文件与数据库初始化

**Files:**
- Create: `src/main/resources/application.yml`
- Create: `src/main/resources/schema.sql`

- [ ] **Step 1: 创建 application.yml**

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/kg_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&createDatabaseIfNotExist=true
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  sql:
    init:
      mode: always
      schema-locations: classpath:schema.sql

# MyBatis-Plus
mybatis-plus:
  mapper-locations: classpath:mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

# Logging
logging:
  level:
    com.kg: debug
```

- [ ] **Step 2: 创建 schema.sql**

```sql
CREATE TABLE IF NOT EXISTS `kg_user` (
    `id`        BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`  VARCHAR(64) NOT NULL COMMENT '用户名',
    `email`     VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `create_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

- [ ] **Step 3: 创建目录结构**

```bash
cd /c/Users/86198/Desktop/kg && mkdir -p src/main/java/com/kg/{interfaces/{controller,dto},application/service,domain/{model,repository,service},infrastructure/{entity,mapper,repository,converter}} && mkdir -p src/main/resources && mkdir -p src/test/java/com/kg
```

---

### Task 3: 启动类

**Files:**
- Create: `src/main/java/com/kg/KgApplication.java`

- [ ] **Step 1: 创建启动类**

```java
package com.kg;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.kg.infrastructure.mapper")
public class KgApplication {
    public static void main(String[] args) {
        SpringApplication.run(KgApplication.class, args);
    }
}
```

---

### Task 4: Domain 层 - 领域模型与仓储接口

**Files:**
- Create: `src/main/java/com/kg/domain/model/User.java`
- Create: `src/main/java/com/kg/domain/repository/UserRepository.java`
- Create: `src/main/java/com/kg/domain/service/UserDomainService.java`

- [ ] **Step 1: 创建领域模型 User**

```java
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

    // 工厂方法：创建一个新用户
    public static User create(String username, String email) {
        User user = new User();
        user.username = username;
        user.email = email;
        user.createAt = LocalDateTime.now();
        return user;
    }

    // 业务方法示例
    public void changeEmail(String newEmail) {
        if (newEmail == null || !newEmail.contains("@")) {
            throw new IllegalArgumentException("Invalid email: " + newEmail);
        }
        this.email = newEmail;
    }

    // --- getters ---

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
```

- [ ] **Step 2: 创建仓储接口 UserRepository（定义在 domain 层）**

```java
package com.kg.domain.repository;

import com.kg.domain.model.User;
import java.util.Optional;

/**
 * 仓储接口 — 只定义契约，不依赖数据库实现。
 * infrastructure 层的 UserRepositoryImpl 实现此接口。
 */
public interface UserRepository {
    Optional<User> findById(Long id);
    void save(User user);
}
```

- [ ] **Step 3: 创建领域服务 UserDomainService**

```java
package com.kg.domain.service;

import com.kg.domain.model.User;
import com.kg.domain.repository.UserRepository;

/**
 * 领域服务 — 封装与 User 聚合相关的业务规则，不涉及数据库操作。
 */
public class UserDomainService {

    private final UserRepository userRepository;

    public UserDomainService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 按 ID 查找用户，找不到抛业务异常
     */
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: id=" + id));
    }
}
```

---

### Task 5: Infrastructure 层 - 持久化实体与 Mapper

**Files:**
- Create: `src/main/java/com/kg/infrastructure/entity/UserEntity.java`
- Create: `src/main/java/com/kg/infrastructure/mapper/UserMapper.java`

- [ ] **Step 1: 创建持久化实体 UserEntity**

```java
package com.kg.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 持久化实体 — 与数据库表映射，仅在 infrastructure 层可见。
 * 与 domain/User 完全分离，通过 UserConverter 互转。
 */
@TableName("kg_user")
public class UserEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createAt;

    // --- getters & setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getCreateAt() { return createAt; }
    public void setCreateAt(LocalDateTime createAt) { this.createAt = createAt; }
}
```

- [ ] **Step 2: 创建 MyBatis-Plus Mapper**

```java
package com.kg.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.infrastructure.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
```

---

### Task 6: Infrastructure 层 - 转换器与仓储实现

**Files:**
- Create: `src/main/java/com/kg/infrastructure/converter/UserConverter.java`
- Create: `src/main/java/com/kg/infrastructure/repository/UserRepositoryImpl.java`

- [ ] **Step 1: 创建转换器 UserConverter**

```java
package com.kg.infrastructure.converter;

import com.kg.domain.model.User;
import com.kg.infrastructure.entity.UserEntity;

/**
 * 领域模型 ↔ 持久化实体 转换器。
 *
 * 防腐层核心：domain.User 与 infra.UserEntity 是完全独立的两个对象，
 * 通过此类做双向映射，保证 domain 层零污染。
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
```

- [ ] **Step 2: 创建仓储实现 UserRepositoryImpl**

```java
package com.kg.infrastructure.repository;

import com.kg.domain.model.User;
import com.kg.domain.repository.UserRepository;
import com.kg.infrastructure.converter.UserConverter;
import com.kg.infrastructure.entity.UserEntity;
import com.kg.infrastructure.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 仓储实现 — 使用 MyBatis-Plus 操作 MySQL。
 *
 * Domain 层只知道 UserRepository 接口，不知道这个类。
 * 如果要换 PostgreSQL，只需新建一个类实现 UserRepository 并注册为 Bean 即可。
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
            user.setId(entity.getId()); // 回填自增 ID
        } else {
            userMapper.updateById(entity);
        }
    }
}
```

---

### Task 7: Application 层 - 应用服务

**Files:**
- Create: `src/main/java/com/kg/application/service/UserApplicationService.java`

- [ ] **Step 1: 创建应用服务 UserApplicationService**

```java
package com.kg.application.service;

import com.kg.domain.model.User;
import com.kg.domain.service.UserDomainService;
import org.springframework.stereotype.Service;

/**
 * 应用层服务 — 编排业务流程。
 * 当前很简单，未来可在此加事务、调用多个领域服务等。
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
```

---

### Task 8: Interfaces 层 - DTO 与 Controller

**Files:**
- Create: `src/main/java/com/kg/interfaces/dto/UserResponse.java`
- Create: `src/main/java/com/kg/interfaces/controller/HelloController.java`
- Create: `src/main/java/com/kg/interfaces/controller/UserController.java`

- [ ] **Step 1: 创建响应 DTO**

```java
package com.kg.interfaces.dto;

import com.kg.domain.model.User;

/**
 * 对外响应 DTO — 隔离领域模型，不暴露内部结构。
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

    // --- getters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCreateAt() { return createAt; }
    public void setCreateAt(String createAt) { this.createAt = createAt; }
}
```

- [ ] **Step 2: 创建 HelloController**

```java
package com.kg.interfaces.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }
}
```

- [ ] **Step 3: 创建 UserController**

```java
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
```

---

### Task 9: Spring Bean 配置（依赖注入）

**Files:**
- Modify: `src/main/java/com/kg/KgApplication.java`（或新建配置类）

- [ ] **Step 1: 创建配置类，注解读 DomainService 和 Repository 的 Bean**

在 `KgApplication.java` 同目录创建 `DomainConfig.java`：

```java
package com.kg;

import com.kg.domain.repository.UserRepository;
import com.kg.domain.service.UserDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public UserDomainService userDomainService(UserRepository userRepository) {
        return new UserDomainService(userRepository);
    }
}
```

---

### Task 10: 构建与验证

- [ ] **Step 1: 编译项目**

```bash
cd /c/Users/86198/Desktop/kg && mvn compile
```
Expected: BUILD SUCCESS

- [ ] **Step 2: 启动应用**

```bash
cd /c/Users/86198/Desktop/kg && mvn spring-boot:run
```
Expected: 应用在 8080 端口启动

- [ ] **Step 3: 测试 GET /hello**

```bash
curl http://localhost:8080/hello
```
Expected: "Hello, World!"

- [ ] **Step 4: 插入测试数据，测试 GET /user/{id}**

先插入测试数据：
```sql
INSERT INTO kg_user (username, email) VALUES ('alice', 'alice@example.com');
```

然后测试：
```bash
curl http://localhost:8080/user/1
```
Expected: `{"id":1,"username":"alice","email":"alice@example.com","createAt":"2026-05-22..."}`
