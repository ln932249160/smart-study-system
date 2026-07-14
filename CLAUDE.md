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
mvn spring-boot:run
# Swagger: http://localhost:8080/swagger-ui/index.html
# Default login: account=root, password=000000
mvn compile     # Compile check
```

## Tech Stack

- **Java 8** — no `var`, no `List.of()`, no text blocks.
- **Spring Boot 2.7.18** — no 3.x (requires Java 17).
- **MyBatis-Plus 3.5.3.1** — LambdaQueryWrapper/LambdaUpdateWrapper for all queries.
- **springdoc-openapi 1.6.15** — not 2.x. Use `@Tag`, `@Operation`, `@Schema`.
- **jjwt 0.11.5** — JWT auth (24h expiration, HS256).
- **spring-security-crypto 5.7.11** — BCrypt only (no full Spring Security).
- **EasyExcel 3.3.2** — Alibaba, for Excel import/export.

## Architecture: DDD 4-Layer

```
interfaces/   → Controller + DTO         (HTTP, no business logic)
application/  → Service                   (orchestration, @Transactional)
domain/       → Model + Repository interface  (pure POJOs, zero framework deps)
infrastructure/ → Entity + Mapper + Converter + RepositoryImpl  (MyBatis-Plus)
```

Cross-cutting:
- `enums/` — RoleEnum(1/2/3), TaskStatusEnum(0/1), TaskUserStatusEnum(0/1/2), TaskTypeEnum(0/1/2/3), ModuleNameEnum
- `interceptor/` — TraceIdFilter(HIGHEST_PRECEDENCE, MDC), JwtInterceptor(UserContext)
- `context/` — UserContext(ThreadLocal)
- `exception/` — GlobalExceptionHandler → `{code, message}`, 5xx 含 traceId
- `scheduler/` — DailyCheckInScheduler(06:00), DailyReviewScheduler(08:00), NotificationScheduler(every min), DailyTaskStartupRunner(启动补偿)

**Rule:** Controller never calls Mapper directly. Always Controller → Service → Repository interface → RepositoryImpl → Mapper.

## Database

13 tables in `kg_db`: sys_user, task, task_user, task_score, task_template, task_plan, class_info, leave_request, class_fee, study_phase, study_phase_class, notification_message, sys_dict.

## Auth Flow

```
POST /auth/login → BCrypt verify → JwtUtil.generateToken(userId, role)
→ Header: Authorization: Bearer <token>
→ TraceIdFilter → MDC.put(traceId)
→ JwtInterceptor → UserContext.setUser() + MDC.put(userId, role)
→ afterCompletion → UserContext.clear() + MDC.clear()
```

## Key Conventions

- **Unified response:** `{ "code": 200, "message": "...", "data": {...} }`. Pagination: `{ "total": N, "list": [...] }`.
- **Role checks:** Use `RoleEnum` helpers (`isTeacher()`, `isHeadmaster()`, `isStudent()`).
- **Task status:** `task.status` = overall (0/1), `task_user.status` = per-student (0/1/2). Completion always `IN ('1','2')`.
- **Task assignment:** `target_type` 1=class 2=student. `target_ids` = comma-separated. Headmaster query uses `FIND_IN_SET + create_by`.
- **BusinessException:** Throw with code+message. Never catch silently.
- **DDD converter:** Every entity has Converter class. Use converters, never map manually.
- **New field rule:** When adding a DB field, sync Entity → Domain Model → Converter (both directions) → DTO/VO → all manual `new DomainModel()` sites → Repository update methods.
- **User-given SQL:** When the user provides reference SQL, implement it directly. Don't substitute with a different approach.
- **Query interfaces:** All page/list endpoints must expose meaningful DB fields as filter params.
- **Enum values:** Mirror `sys_dict` entries as Java enums. Never scatter magic strings.
- **Notification visibility:** `notify_time <= NOW()`. Status: 0=active, 1=invalidated (logical delete).
- **Learning phases:** Max 2 per class per day. Validated server-side.
- **Leave approval:** Teacher approval invalidates notifications for all teachers. Headmaster approval does not.
- **Repeat tasks:** `task_plan` → date generation → `task` per date → `task_user` per student.
- **File upload logging:** Skip binary/multipart body. `CachedBodyRequestWrapper` for JSON body.
