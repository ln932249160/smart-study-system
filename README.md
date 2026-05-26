# 智能学习系统 - 后端

考公培训机构教学管理系统后端，Spring Boot 2.7 + MyBatis-Plus + JWT。

## 技术栈

| 组件 | 版本 |
|------|------|
| JDK | 1.8 |
| Spring Boot | 2.7.18 |
| MyBatis-Plus | 3.5.3.1 |
| MySQL | 8.0 |
| JWT | jjwt 0.11.5 |
| BCrypt | spring-security-crypto 5.7.11 |
| API 文档 | springdoc-openapi 1.6.15 |

## 数据库

`localhost:3306/kg_db`，共 7 张业务表：

| 表 | 说明 | 记录数 |
|------|------|--------|
| `sys_user` | 系统用户（16 列，含 account/name/role/class_id） | 10 |
| `task` | 任务主表（15 列） | 4 非模板 |
| `task_user` | 任务分配状态表 | 17 |
| `task_score` | 成绩明细表 | 10 |
| `class_info` | 班级表 | 3 |
| `notification_message` | 通知消息表 | 0（定时任务生成） |
| `sys_dict` | 字典表 | 12 |

## 架构

DDD 四层 + 枚举 + 横切：

```
interfaces/   → Controller + DTO（对外接口）
application/  → Service（编排业务流程）
domain/       → Model + Repository 接口（纯 POJO）
infrastructure/ → Entity + Mapper + Converter + RepositoryImpl
enums/        → RoleEnum / TaskStatusEnum / ModuleNameEnum
scheduler/    → @Scheduled 定时任务
config/       → OpenApi / WebMvc / DataInitializer
interceptor/  → JwtInterceptor（认证）
context/      → UserContext（ThreadLocal）
exception/    → GlobalExceptionHandler（统一异常）
util/         → JwtUtil
```

## 启动

```bash
# 1. MySQL 中创建 kg_db 库
# 2. 修改 application.yml 中数据库连接信息
# 3. 启动
mvn spring-boot:run

# 4. Swagger 文档
http://localhost:8080/swagger-ui/index.html

# 5. 默认账号
account: root
password: 000000
```

## 接口总览

### 认证

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/auth/login` | 账号密码登录，返回 JWT |

### 首页统计

| 方法 | 路径 | 角色 |
|------|------|------|
| GET | `/home/stat` | teacher + headmaster |
| GET | `/home/student/stat` | student |

### 我的任务

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/my-task/list` | 个人任务列表，按 force_flag DESC + create_time DESC |
| POST | `/my-task/complete` | 完成任务（含成绩明细） |

### 个人中心

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/profile` | 查看个人信息 |
| PUT | `/profile` | 修改资料（name/gender/email/phone/description） |
| PUT | `/profile/password` | 修改密码（验原密码） |

### 通知消息

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/notification/list` | 消息列表，created_at 倒序 |
| POST | `/notification/read` | 单条已读 `{ "messageId": 1 }` |

### 学生管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/student/page` | 分页查询 student/headmaster |
| POST | `/student` | 新增 |
| PUT | `/student/{id}` | 编辑 |
| DELETE | `/student/{id}` | 物理删除 |

### 班级管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/class/page` | 分页查询（含学生人数） |
| POST | `/class` | 新增 + 分配学生 |
| PUT | `/class/{id}` | 编辑 + 重分配学生 |
| DELETE | `/class/{id}` | 删除，学生回归未分班 |
| GET | `/class/student-options` | 学生复选框列表 |
| GET | `/class/options` | 班级下拉 |

### 任务管理（仅 teacher）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/task/page` | 分页查询（含完成人数/百分比） |
| POST | `/task` | 新增 + 分配 |
| PUT | `/task/{id}` | 编辑 + 重建分配 |
| DELETE | `/task/{id}` | 级联删除 |
| GET | `/task/templates` | 模板任务下拉 |

### 字典

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/dict/{dictCode}` | ROLE / TASK_STATUS / MODULE_NAME |

## 角色权限矩阵

### 数据范围

| | teacher | headmaster | student |
|------|------|------|------|
| 看学生 | 全校 | 本班 | ❌ |
| 看班级 | 全部 | 自己班 | ❌ |
| 看任务 | 全部 | 自己创建的 | 分配给我的 |
| 完成任务 | ❌ | ✅ 我的任务 | ✅ 我的任务 |
| 增删改 | 全部权限 | ❌ | ❌ |

### 接口权限

| 接口 | teacher | headmaster | student |
|------|:--:|:--:|:--:|
| `/auth/login` | ✅ | ✅ | ✅ |
| `/home/stat` | ✅ | ✅ | ❌ |
| `/home/student/stat` | ❌ | ❌ | ✅ |
| `/my-task/*` | ❌ | ✅ | ✅ |
| `/profile`, `/profile/password` | ✅ | ✅ | ✅ |
| `/notification/*` | ✅ | ✅ | ✅ |
| `/student` CRUD | ✅ | ❌ | ❌ |
| `/student/page` | ✅ | 本班 | ❌ |
| `/class` CRUD | ✅ | ❌ | ❌ |
| `/class/page` | ✅ | 本班 | ❌ |
| `/task` CRUD | ✅ | ❌ | ❌ |
| `/task/page` | ✅ | 本班 | 本人 |
| `/task/templates` | ✅ | ✅ | ✅ |
| `/dict/*` | ✅ | ✅ | ✅ |
| `/class/options` | ✅ | ✅ | ✅ |

## 统一响应格式

```json
{ "code": 200, "message": "成功", "data": { ... } }
```

| code | 含义 |
|------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录 |
| 403 | 无权限 |
| 404 | 接口不存在 |
| 500 | 服务端错误 |

分页结构：`{ "total": 4, "list": [...] }`，入参 `{ "pageNum": 1, "pageSize": 10 }`。

## 定时任务

| 任务 | cron | 功能 |
|------|------|------|
| NotificationScheduler | `0 * * * * ?` | 每分钟：扫描 task 生成 TASK_START/TASK_WARNING/TASK_END 通知 |
| DailyCheckInScheduler | `0 0 6 * * ?` | 每天 06:00：创建"每日打卡"（type=1, mandatory） |
| DailyReviewScheduler | `0 0 8 * * ?` | 每天 08:00：创建"每日复盘"（type=0, mandatory） |

防重复机制：
- 通知消息：`user_id + related_id + type` 三字段唯一
- 每日任务：同 type + 同 create_time 日期只创建一次

## 关键设计

### 认证链路

```
POST /auth/login → BCrypt 验密 → JwtUtil.generateToken(userId, role)
→ 后续请求 Header: Authorization: Bearer <token>
→ JwtInterceptor 验签 → 解析 userId+role → UserContext.setUser()
→ Controller/Service 通过 UserContext.getUser() 获取当前用户
→ afterCompletion → UserContext.clear()
```

### 姓名与账号

- `account` — 登录账号（唯一），默认 = 手机号
- `name` — 显示姓名（非唯一），查询/展示用它

### 单班制

一个学生只属于一个班级。新增/编辑班级时通过 `batchUpdateClassId` 批量调整 `class_id`。删除班级时将学生 `class_id` 置 NULL。

### 任务完成统计

所有完成人数/百分比均从 `task_user` 实时计算（`GROUP BY task_id + COUNT`），task 表不存储冗余统计字段。

## 项目文件统计

```
interfaces/controller/  11 个
application/service/    10 个
domain/model/           6 个
domain/repository/      6 个
infrastructure/entity/   8 个
infrastructure/mapper/   9 个
infrastructure/repository/ 6 个
infrastructure/converter/  6 个
enums/                  3 个
scheduler/              3 个
dto/                    23 个
总计                    96 个 Java 文件
```
