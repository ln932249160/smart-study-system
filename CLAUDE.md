# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## OpenWolf

This project uses OpenWolf for context management.
- Check `.wolf/anatomy.md` before reading any file (has 2-3 line descriptions for every file).
- Check `.wolf/cerebrum.md` **Do-Not-Repeat** section before generating code.
- After writing/editing files, update `.wolf/anatomy.md` and append to `.wolf/memory.md`.
- After receiving a user correction, update `.wolf/cerebrum.md` immediately.
- BEFORE fixing any bug: read `.wolf/buglog.json` for known fixes.
- AFTER fixing any bug: log to `.wolf/buglog.json` with error_message, root_cause, fix, tags.

Full protocol at `.wolf/OPENWOLF.md`.

## Build & Run

```bash
# Start (requires MySQL kg_db running on localhost:3306)
mvn spring-boot:run

# Swagger UI
# http://localhost:8080/swagger-ui/index.html

# Default login: account=root, password=000000

# Compile check (no tests yet)
mvn compile
```

## Tech Stack

- **Java 8** — non-negotiable. No `var`, no `List.of()`, no text blocks.
- **Spring Boot 2.7.18** — no 3.x (requires Java 17).
- **MyBatis-Plus 3.5.3.1** — LambdaQueryWrapper/LambdaUpdateWrapper for all queries.
- **springdoc-openapi 1.6.15** — not 2.x (2.x requires Java 17). Use `@Tag`, `@Operation`, `@Schema`.
- **jjwt 0.11.5** — JWT auth.
- **spring-security-crypto 5.7.11** — BCrypt only (no full Spring Security).
- **EasyExcel 3.3.2** — Alibaba, for Excel import/export.

## Architecture: DDD 4-Layer

```
interfaces/   → Controller + DTO         (HTTP layer, no business logic)
application/  → Service                   (orchestration, transactions)
domain/       → Model + Repository interface  (pure POJOs, zero framework deps)
infrastructure/ → Entity + Mapper + Converter + RepositoryImpl  (MyBatis-Plus, DB mapping)
```

Cross-cutting:
- `enums/` — RoleEnum (1=teacher, 2=headmaster, 3=student), TaskStatusEnum, ModuleNameEnum
- `interceptor/` — JwtInterceptor (extracts Bearer token, sets UserContext, clears after)
- `context/` — UserContext (ThreadLocal holding current user)
- `exception/` — GlobalExceptionHandler returns `{code, message}` for all exceptions
- `scheduler/` — @Scheduled tasks (daily check-in 06:00, daily review 08:00, notifications every minute)

**Rule:** Controller never calls Mapper directly. Always Controller → Service → Repository interface → RepositoryImpl → Mapper.

## Database

MySQL `kg_db` on `localhost:3306`. Tables: `sys_user`, `task`, `task_user`, `task_score`, `task_template`, `class_info`, `notification_message`, `sys_dict`.

Schema at `src/main/resources/schema.sql` (reference only, `spring.sql.init.mode=never`).

MyBatis-Plus config: `map-underscore-to-camel-case: true`, `id-type: auto`.

## Auth Flow

```
POST /auth/login → BCrypt verify → JwtUtil.generateToken(userId, role)
→ Client sends: Authorization: Bearer <token>
→ JwtInterceptor parses token, stores user in UserContext
→ Controller/Service reads UserContext.getUser()
→ afterCompletion → UserContext.clear()
```

JWT expiration: 24 hours. Key field: `role` (stored as String "1"/"2"/"3").

## Key Conventions

- **Unified response:** `{ "code": 200, "message": "...", "data": {...} }`. Pagination: `{ "total": N, "list": [...] }`.
- **Role checks:** Use `RoleEnum` helpers — `user.isTeacher()`, `user.isHeadmaster()`, `user.isStudent()`. Never compare raw strings.
- **Account vs Name:** `account` = login (unique, defaults to phone). `name` = display name (non-unique).
- **Single-class:** Each student belongs to at most one class (`class_id` in sys_user).
- **Task completion stats:** Always computed live from `task_user` table via `GROUP BY`, never stored redundantly.
- **Notification visibility:** Controlled by `notify_time` field — query filters `notify_time <= NOW()`.
- **Template tasks:** `task_template` table. Creating task from template auto-fills fields and auto-increments `round_no`.
- **Task.target_type:** 1=class, 2=student. `target_ids` = comma-separated IDs.
- **BusinessException:** Throw with code+message. `GlobalExceptionHandler` catches and formats.
- **Enum values live in sys_dict** but are also mirrored as Java enums in `com.kg.enums` for type safety.
- **DDD converter pattern:** Every entity has a matching Converter class (e.g., `SysUserConverter.toDomain()`, `.toEntity()`). Always use converters, never map manually.
