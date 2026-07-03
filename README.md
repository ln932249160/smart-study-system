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
| Excel | EasyExcel 3.3.2 |

## 快速启动

```bash
# 1. MySQL 创建 kg_db 库（utf8mb4）
# 2. 修改 application.yml 数据库连接信息
# 3. 启动
mvn spring-boot:run

# Swagger: http://localhost:8080/swagger-ui/index.html
# 默认账号: account=root, password=000000
```

## 架构

DDD 四层 + 横切关注点：

```
interfaces/     → Controller + DTO           HTTP 层，不含业务逻辑
application/    → Service                    编排、事务
domain/         → Model + Repository 接口     纯 POJO，零框架依赖
infrastructure/ → Entity + Mapper + Converter + RepositoryImpl  数据库映射
```

横切：
- `enums/` — RoleEnum / TaskStatusEnum / ModuleNameEnum
- `interceptor/` — JwtInterceptor（解析 Bearer Token → UserContext）
- `context/` — UserContext（ThreadLocal 存当前用户）
- `exception/` — GlobalExceptionHandler → 统一 `{code, message}`
- `scheduler/` — @Scheduled 定时任务
- `config/` — OpenApi / WebMvc / DataInitializer
- `util/` — JwtUtil

**规则：** Controller 不直接调 Mapper。始终 Controller → Service → Repository 接口 → RepositoryImpl → Mapper。

## 数据库

`localhost:3306/kg_db`，8 张业务表：

| 表 | 说明 |
|------|------|
| `sys_user` | 系统用户（16 列） |
| `task` | 任务主表（15 列，含 template_id / target_type / target_ids） |
| `task_user` | 任务分配状态表 |
| `task_score` | 成绩明细表 |
| `task_template` | 模板任务表 |
| `class_info` | 班级表 |
| `notification_message` | 通知消息表 |
| `sys_dict` | 字典表 |

MyBatis-Plus 配置：`map-underscore-to-camel-case: true`，`id-type: auto`。

## 接口总览（42 个端点）

### 认证

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/auth/login` | 账号密码登录 → JWT |

### 首页统计

| 方法 | 路径 | 角色 |
|------|------|------|
| GET | `/home/stat` | teacher / headmaster |
| GET | `/home/student/stat` | student |

### 我的任务

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/my-task/list` | 分页列表，筛选：status / taskName / taskType / forceFlag / 时间范围 |
| GET | `/my-task/stats` | 待办/已办计数 `{unfinished, finished}` |
| POST | `/my-task/complete` | 完成任务（含模块成绩明细） |

### 个人中心

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/profile` | 查看个人信息 |
| PUT | `/profile` | 修改资料（name/gender/email/phone/description） |
| PUT | `/profile/password` | 修改密码（验原密码） |

### 通知消息

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/notification/list` | 分页列表，筛选：title / type / isRead / priority / relatedId / notifyTime 范围 |
| POST | `/notification/read` | 单条已读 `{"id": 1}` |
| GET | `/notification/unread-count` | 未读消息数 |

### 用户管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/student/page` | 分页，筛选：name / role / phone / userId / classId / className |
| GET | `/student/{id}` | 用户详情 |
| POST | `/student` | 新增用户（student / headmaster / teacher） |
| PUT | `/student/{id}` | 编辑用户 |
| DELETE | `/student/{id}` | 物理删除 |
| GET | `/class/options` | 班级下拉选项 |
| GET | `/user/template` | 下载导入模板（Excel） |
| POST | `/user/import` | 批量导入（multipart/form-data） |

### 班级管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/class/page` | 分页查询（含学生人数） |
| GET | `/class/{id}` | 班级详情 |
| POST | `/class` | 新增 + 分配学生 |
| PUT | `/class/{id}` | 编辑 + 重分配学生 |
| DELETE | `/class/{id}` | 删除，学生回归未分班 |
| GET | `/class/student-options` | 学生复选框列表 |

### 任务列表

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/task/page` | 分页，筛选：taskType / taskName / isMandatory / 时间范围 |
| GET | `/task/{id}` | 任务详情（含 selectedClassList / selectedStudentList） |
| POST | `/task` | 新增（支持从模板创建 + 指定 target_type / target_ids） |
| PUT | `/task/{id}` | 编辑 + 重建分配 + 重生成通知 |
| DELETE | `/task/{id}` | 级联删除（task_user + task_score + notification） |

### 模板任务管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/task-template/page` | 分页查询 |
| GET | `/task-template/{id}` | 模板详情 |
| POST | `/task-template` | 新增模板 |
| PUT | `/task-template/{id}` | 编辑模板 |
| DELETE | `/task-template/{id}` | 删除模板 |
| GET | `/task-template/options` | 模板下拉选项 |

### 系统管理（字典）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/dict/{dictCode}` | 字典下拉（ROLE / TASK_STATUS / MODULE_NAME） |
| GET | `/dict/group/page` | 字典分组列表，筛选 dictName |
| GET | `/dict/group/{dictCode}` | 字典组详情 |
| POST | `/dict/group` | 新增字典组（整组保存） |
| PUT | `/dict/group/{dictCode}` | 编辑字典组（整组替换） |
| DELETE | `/dict/group/{dictCode}` | 删除整组 |

## 角色权限矩阵

### 数据范围

| | teacher | headmaster | student |
|------|-----|-----|-----|
| 看用户 | 全校 | 本班 | ❌ |
| 看班级 | 全部 | 自己班 | ❌ |
| 看任务 | 全部 | 本班任务 | 分配给我的 |
| 完成任务 | ❌ | ✅ 我的任务 | ✅ 我的任务 |
| 增删改 | 全部权限 | ❌ | ❌ |

### 接口权限

| 接口 | teacher | headmaster | student |
|------|:--:|:--:|:--:|
| `/auth/login` | ✅ | ✅ | ✅ |
| `/home/stat` | ✅ | ✅ | ❌ |
| `/home/student/stat` | ❌ | ❌ | ✅ |
| `/my-task/*` | ❌ | ✅ | ✅ |
| `/profile` `/profile/password` | ✅ | ✅ | ✅ |
| `/notification/*` | ✅ | ✅ | ✅ |
| `/student` CRUD | ✅ | ❌ | ❌ |
| `/student/page` | ✅ | 本班 | ❌ |
| `/student/{id}` | ✅ | 本班 | ❌ |
| `/class` CRUD | ✅ | ❌ | ❌ |
| `/class/page` `/class/{id}` | ✅ | 本班 | ❌ |
| `/task` CRUD | ✅ | ❌ | ❌ |
| `/task/page` `/task/{id}` | ✅ | 本班 | 本人 |
| `/task-template` CRUD | ✅ | ❌ | ❌ |
| `/task-template/options` | ✅ | ✅ | ✅ |
| `/dict/*` | ✅ | ✅ | ✅ |
| `/class/options` `/class/student-options` | ✅ | ✅ | ✅ |

## 统一响应格式

```json
{ "code": 200, "message": "成功", "data": { ... } }
```

| code | 含义 |
|------|------|
| 200 | 成功 |
| 400 | 参数错误 / 业务异常 |
| 401 | 未登录 |
| 403 | 无权限 |
| 404 | 接口不存在 |
| 500 | 服务端错误 |

分页：`{ "total": N, "list": [...] }`，入参 pageNum / pageSize。

## 认证链路

```
POST /auth/login → BCrypt 验密 → JwtUtil.generateToken(userId, role)
→ 后续请求 Header: Authorization: Bearer <token>
→ JwtInterceptor 验签 → 解析 userId+role → UserContext.setUser()
→ Controller/Service 读 UserContext.getUser()
→ afterCompletion → UserContext.clear()
```

JWT 24 小时过期，HS256 签名。role 存为 String "1"/"2"/"3"。

## 关键设计

### 账号与姓名
- `account` — 登录账号（唯一），不传则默认 = 手机号
- `name` — 显示姓名（非唯一）

### 单班制
学生只属于一个班级。新增/编辑班级时通过 `batchUpdateClassId` 批量调整 `class_id`。删除班级时 `class_id` 置 NULL。

### 任务完成统计
所有完成人数/百分比从 `task_user` 实时计算（`GROUP BY task_id + COUNT`），task 表不存冗余统计字段。

### 模板任务
`task_template` 表存储模板。创建任务时指定 `useTemplate=1` + `templateId` → 自动填充字段 + `round_no` 自增 + `task_name` 格式为 "模板名 第N轮"。

### 任务分配
`task.target_type`：1=班级 2=学生。`target_ids` = 逗号分隔 ID。创建时解析 target_ids 批量写入 `task_user` 表。

### 通知消息
`notify_time` 控制可见时间 — 查询时过滤 `notify_time <= NOW()`。TASK_START 类型 notify_time = 任务开始时间，TASK_DEADLINE = 任务 90% 时间点。编辑任务时先删未到期的旧消息再生成新消息。

### 定时任务

| 任务 | cron | 功能 |
|------|------|------|
| NotificationScheduler | `0 * * * * ?` | 每分钟扫描 task 生成通知 |
| DailyCheckInScheduler | `0 0 6 * * ?` | 每天 06:00 创建"每日打卡"（type=1, mandatory） |
| DailyReviewScheduler | `0 0 8 * * ?` | 每天 08:00 创建"每日复盘"（type=0, mandatory） |

防重复：通知 `user_id + related_id + type` 三字段唯一，每日任务同 type + 同日期只创建一次。

## 项目文件统计

```
interfaces/controller/   10 个 Controller
application/service/     11 个 Service
domain/model/            7 个 Model
domain/repository/       7 个 Repository 接口
infrastructure/entity/    9 个 Entity
infrastructure/mapper/   11 个 Mapper
infrastructure/repository/ 7 个 RepositoryImpl
infrastructure/converter/  7 个 Converter
enums/                   3 个
scheduler/               3 个
dto/                     31 个 DTO/VO
总计                     106 个 Java 文件
```
