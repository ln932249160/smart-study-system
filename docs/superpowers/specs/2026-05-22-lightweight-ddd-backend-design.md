# Lightweight DDD Backend Design

## Stack
- Java 8, Spring Boot 2.7.18, MyBatis-Plus 3.5.3.1, MySQL 8.x, Maven

## Architecture Layers

```
interfaces/        → Controller + DTO (HTTP层)
application/       → ApplicationService (编排层)
domain/            → Model + Repository(接口) + DomainService (业务核心)
infrastructure/    → Entity + Mapper + RepositoryImpl + Converter (持久化实现)
```

## Dependency Direction
```
interfaces → application → domain ← infrastructure
```
Domain 层零外部依赖，infrastructure 依赖 domain 实现其接口。

## Anti-Corruption Layer
- domain/UserRepository 是接口（定义在 domain 层）
- infrastructure/UserRepositoryImpl 实现该接口
- UserConverter 做 User(domain) ↔ UserEntity(infra) 双向转换
- 切换数据库 = 换掉 RepositoryImpl + Mapper，domain 层不变

## Object Flow
```
Controller(DTO) → AppService → DomainService(Domain Model) → Repository(Domain Model)
                                                                  ↓
                                                            Converter
                                                                  ↓
                                                           Mapper(Entity) → DB
```

## Endpoints
- GET /hello → "Hello, World!"
- GET /user/{id} → UserResponse JSON

## Files (~14 files)
1. pom.xml
2. KgApplication.java
3. application.yml, schema.sql
4. interfaces/controller/HelloController, UserController
5. interfaces/dto/UserResponse
6. application/service/UserApplicationService
7. domain/model/User
8. domain/repository/UserRepository
9. domain/service/UserDomainService
10. infrastructure/entity/UserEntity
11. infrastructure/mapper/UserMapper
12. infrastructure/repository/UserRepositoryImpl
13. infrastructure/converter/UserConverter

## YAGNI Decisions
- No abstract base classes
- No generic Repository<T> interface
- No event bus, no CQRS
- No DTO assembler pattern (direct construction for now)
- Single User module only
