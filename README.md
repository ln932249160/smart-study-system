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
- `enums/` — RoleEnum / TaskStatusEnum / TaskTypeEnum / TaskUserStatusEnum / ModuleNameEnum
- `interceptor/` — JwtInterceptor（解析 Bearer Token → UserContext）+ TraceIdFilter（日志链路）
- `context/` — UserContext（ThreadLocal 存当前用户）
- `exception/` — GlobalExceptionHandler → 统一 `{code, message}`
- `scheduler/` — @Scheduled 定时任务 + DailyTaskStartupRunner 启动补偿
- `config/` — OpenApi / WebMvc / DataInitializer / LogHttpProperties
- `util/` — JwtUtil / SensitiveMaskUtils

**规则：** Controller 不直接调 Mapper。始终 Controller → Service → Repository 接口 → RepositoryImpl → Mapper。

## 数据库

`localhost:3306/kg_db`，13 张业务表：

### 用户与班级
| 表 | 说明 |
|------|------|
| `sys_user` | 系统用户（含 teacher_remark） |
| `class_info` | 班级表 |

### 任务体系
| 表 | 说明 |
|------|------|
| `task` | 任务主表（含 plan_id / plan_date / is_repeat_task / status） |
| `task_user` | 任务分配+完成状态（0未完成/1已完成/2逾期完成） |
| `task_score` | 成绩明细表 |
| `task_template` | 模板任务表 |
| `task_plan` | 重复任务计划表（DAILY/WEEKLY 等） |

### 业务模块
| 表 | 说明 |
|------|------|
| `leave_request` | 请假申请表 |
| `class_fee` | 班费表 |
| `study_phase` | 学习阶段主表 |
| `study_phase_class` | 学习阶段-班级关联表 |

### 系统
| 表 | 说明 |
|------|------|
| `notification_message` | 通知消息表（含 status 逻辑删除） |
| `sys_dict` | 字典表（含 display_color） |

MyBatis-Plus 配置：`map-underscore-to-camel-case: true`，`id-type: auto`。

## 接口总览

### 认证

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/auth/login` | 账号密码登录 → JWT |

### 首页统计

| 方法 | 路径 | 角色 |
|------|------|------|
| GET | `/home/stat` | teacher / headmaster |
| GET | `/home/student/stat` | student |

### 打卡统计

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/student/checkin/stat` | 连续天数 + 本月次数 + 今日是否已打卡 |
| GET | `/student/checkin/calendar?month=` | 指定月份已打卡日期列表 |

### 我的任务

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/my-task/list` | 分页列表，筛选：status/taskName/taskType/forceFlag/isTemplate/finishTime/时间范围 |
| POST | `/my-task/stats` | 待办/已办计数 |
| GET | `/my-task/detail?taskUserId=&userId=` | 任务详情（含成绩明细） |
| POST | `/my-task/complete` | 完成任务（自动判定逾期） |
| PUT | `/my-task` | 修改已完成任务（remark/totalScore/scores） |
| POST | `/my-task/checkin` | 一键打卡（完成当日所有待办打卡） |
| POST | `/my-task/template/page` | 模板任务训练记录（汇总统计） |
| GET | `/my-task/template/detail?templateId=&userId=` | 模板任务详情（各轮次+成绩） |

### 个人中心

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/profile` | 查看个人信息（含 className） |
| PUT | `/profile` | 修改资料 |
| PUT | `/profile/password` | 修改密码（验原密码） |

### 通知消息

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/notification/list` | 分页列表，筛选：title/type/isRead/priority/relatedId/notifyTime |
| POST | `/notification/read` | 单条已读 |
| GET | `/notification/unread-count` | 未读消息数（仅 status=0） |

### 请假管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/leave` | 学生新增请假（自动计算天数+解析审批人+发通知） |
| POST | `/leave/page` | 我的请假列表，筛选：leaveType/status/日期 |
| GET | `/leave/approve/list?tab=pending\|done` | 审批列表 |
| POST | `/leave/approve` | 批量审批通过/拒绝 |

### 用户管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/student/page` | 分页，筛选：name/role/phone/userId/classId/className |
| GET | `/student/{id}` | 用户详情 |
| POST | `/student` | 新增用户（teacher/headmaster/student），新学生自动补发班级任务 |
| PUT | `/student/{id}` | 编辑用户 |
| DELETE | `/student/{id}` | 物理删除 |
| GET | `/class/options` | 班级下拉选项 |
| GET | `/user/template` | 下载导入模板（Excel） |
| POST | `/user/import` | 批量导入 |

### 班级管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/class/page` | 分页查询（含学生人数） |
| GET | `/class/{id}` | 班级详情 |
| POST | `/class` | 新增 + 分配学生 |
| PUT | `/class/{id}` | 编辑 + 重分配学生 |
| DELETE | `/class/{id}` | 删除，学生回归未分班 |
| GET | `/class/student-options` | 学生复选框列表 |

### 班费管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/class-fee/page` | 分页，筛选：classId/material |
| GET | `/class-fee/{id}` | 详情 |
| POST | `/class-fee` | 新增 |
| PUT | `/class-fee/{id}` | 编辑 |
| DELETE | `/class-fee/{id}` | 删除 |
| GET | `/class-fee/statistics` | 按班级汇总金额 |
| GET | `/class-fee/detail/{classId}` | 班级费用明细 |

### 任务列表

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/task/page` | 分页，筛选：taskType/taskName/isMandatory/时间范围 |
| GET | `/task/{id}` | 任务详情（含 selectedClassList/selectedStudentList） |
| POST | `/task` | 新增（支持 createMode=REPEAT 重复任务） |
| PUT | `/task/{id}` | 编辑 + 重建分配 + 重生成通知 |
| DELETE | `/task/{id}` | 级联删除 |

### 任务计划

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/task-plan/detail/{planId}` | 计划详情 + 已生成任务列表 |
| POST | `/task-plan/stop/{planId}` | 停止计划（禁用未来任务） |

### 模板任务管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/task-template/page` | 分页查询 |
| GET | `/task-template/{id}` | 模板详情 |
| POST | `/task-template` | 新增模板 |
| PUT | `/task-template/{id}` | 编辑模板 |
| DELETE | `/task-template/{id}` | 删除模板 |
| GET | `/task-template/options` | 模板下拉选项 |

### 学习阶段

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/study-phase/add` | 新增（仅老师） |
| PUT | `/study-phase/update` | 编辑 |
| DELETE | `/study-phase/delete/{id}` | 逻辑删除 |
| GET | `/study-phase/detail/{id}` | 详情 |
| GET | `/study-phase/page` | 分页列表，筛选：phaseTitle/classId/日期/status |
| GET | `/study-phase/calendar?startDate=&endDate=&classId=` | 日历查询（区间交集） |

### 系统管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/dict/{dictCode}` | 字典下拉 |
| GET | `/dict/group/page` | 字典分组列表 |
| GET | `/dict/group/{dictCode}` | 字典组详情 |
| POST | `/dict/group` | 新增字典组 |
| PUT | `/dict/group/{dictCode}` | 编辑字典组 |
| DELETE | `/dict/group/{dictCode}` | 删除整组（task_type 禁止删除） |

### 批处理（测试入口）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/batch/daily-checkin` | 手动触发每日打卡 |
| POST | `/batch/daily-review` | 手动触发每日复盘 |

## 角色权限矩阵

### 数据范围

| | teacher | headmaster | student |
|------|-----|-----|-----|
| 看用户 | 全校 | 本班 | 本班 |
| 看班级 | 全部 | 自己班 | 自己班 |
| 看任务 | 全部 | 本班+自己创建 | 分配给我的 |
| 完成任务 | ❌ | ✅ 我的任务 | ✅ 我的任务 |
| 任务 CRUD | ✅ | ✅ | ❌ |
| 模板任务管理 | ✅ | ✅ | ❌ |
| 请假审批 | ✅（老师角色） | ✅（指定审批人） | ❌ |
| 班费管理 | ✅ 全部 | ✅ 本班（只读） | ✅ 本班（只读） |
| 学习阶段 | ✅ CRUD+全部 | ✅ 本班（只读） | ✅ 本班（只读） |

### 接口权限速查

| 接口 | teacher | headmaster | student |
|------|:--:|:--:|:--:|
| `/auth/login` | ✅ | ✅ | ✅ |
| `/home/stat` | ✅ | ✅ | ❌ |
| `/home/student/stat` | ❌ | ❌ | ✅ |
| `/student/checkin/*` | ❌ | ❌ | ✅ |
| `/my-task/*` (查) | ✅ (传userId) | ✅ | ✅ |
| `/my-task/complete|checkin` | ❌ | ✅ | ✅ |
| `/profile` `/profile/password` | ✅ | ✅ | ✅ |
| `/notification/*` | ✅ | ✅ | ✅ |
| `/student` CRUD | ✅ | ❌ | ❌ |
| `/student/page` `/student/{id}` | ✅ | ✅ 本班 | ✅ 本班 |
| `/class` CRUD | ✅ | ❌ | ❌ |
| `/class/page` `/class/{id}` | ✅ | ✅ 本班 | ✅ 本班 |
| `/task` CRUD | ✅ | ✅ (本班校验) | ❌ |
| `/task/page` `/task/{id}` | ✅ | ✅ 本班 | ✅ 本人 |
| `/task-template` CRUD | ✅ | ✅ | ❌ |
| `/task-template/options` | ✅ | ✅ | ✅ |
| `/task-plan/*` | ✅ | ✅ | ❌ |
| `/leave` CRUD (自己) | ❌ | ✅ | ✅ |
| `/leave/approve` | ✅ (全部) | ✅ (审批人) | ❌ |
| `/class-fee` CRUD | ✅ | ❌ | ❌ |
| `/class-fee/page\|statistics\|detail` | ✅ | ✅ 本班 | ✅ 本班 |
| `/study-phase` CRUD | ✅ | ❌ | ❌ |
| `/study-phase/page\|calendar` | ✅ | ✅ 本班 | ✅ 本班 |
| `/dict/*` | ✅ | ✅ | ✅ |
| `/class/options` `/class/student-options` | ✅ | ✅ | ✅ |
| `/batch/*` | ✅ | ❌ | ❌ |

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
| 500 | 服务端错误（含 traceId） |

分页：`{ "total": N, "list": [...] }`，入参 pageNum / pageSize。

## 认证链路

```
POST /auth/login → BCrypt 验密 → JwtUtil.generateToken(userId, role)
→ 后续请求 Header: Authorization: Bearer <token>
→ TraceIdFilter → 生成 traceId，MDC.put
→ JwtInterceptor 验签 → 解析 userId+role → UserContext + MDC.put(userId,role)
→ Controller/Service 读 UserContext.getUser()
→ afterCompletion → UserContext.clear() + MDC.clear()
```

JWT 24 小时过期，HS256 签名。role 存为 String "1"/"2"/"3"。

## 日志

- TraceIdFilter：每个请求自动生成/复用 `X-Trace-Id`，START/END/ERROR 日志含 method/uri/requestBody/responseBody/cost
- logback-spring.xml：控制台 + 文件（按天+100MB滚动，保留30天），错误日志单独输出
- 格式：`[traceId=%X{traceId} userId=%X{userId} role=%X{role}]`
- 配置：`log.http.enabled/body-enabled/max-body-length` 可在 application.yml 控制

## 关键设计

### 任务状态体系
- `task.status`（TaskStatusEnum）：0 未完成 / 1 已完成（所有学生完成后自动更新）
- `task_user.status`（TaskUserStatusEnum）：0 未完成 / 1 已完成 / 2 逾期完成（根据 task_end_time 后端自动判定）
- 所有完成统计使用 `IN ('1','2')`

### 任务分配
`task.target_type`：1=班级 2=学生。`target_ids` = 逗号分隔 ID。班长查任务使用 `FIND_IN_SET + create_by` 双重条件。

### 重复任务
`task_plan` 存规则 → 按 DAILY/WEEKLY 生成日期列表 → 每个日期生成一条 task → 每条 task 分发 task_user。

### 通知消息
`notify_time` 控制可见时间。物理删除已改为 `status='1'` 逻辑删除。

### 定时任务 + 启动补偿

| 任务 | cron | 功能 |
|------|------|------|
| NotificationScheduler | `0 * * * * ?` | 每分钟扫描 task 生成通知 |
| DailyCheckInScheduler | `0 0 6 * * ?` | 每天 06:00 创建"每日打卡"（target_type=1, 全部班级） |
| DailyReviewScheduler | `0 0 8 * * ?` | 每天 08:00 创建"每日复盘" |
| DailyTaskStartupRunner | 启动时 | 补偿检查，缺失则补生成 |

### 学习阶段同一天限制
同一班级同一天最多 2 个学习阶段，后端 `validateDailyLimit()` 校验。

### 请假审批
学生请假 ≤2天 → 本班班长审批，>2天 → 全部老师审批。老师审批后通知消息失效。审批带 `WHERE status=0` 防并发。

### 新学生补发任务
新增学生时，自动将班级所有未结束任务补发（`FIND_IN_SET(classId, target_ids)`）。

## 项目文件统计

```
interfaces/controller/   14 个 Controller
application/service/     16 个 Service
domain/model/            10 个 Model
domain/repository/       9 个 Repository 接口
infrastructure/entity/    13 个 Entity
infrastructure/mapper/   15 个 Mapper
infrastructure/repository/ 9 个 RepositoryImpl
infrastructure/converter/  9 个 Converter
enums/                   6 个
scheduler/               4 个
interceptor/             2 个 Filter/Interceptor
dto/                     47 个 DTO/VO
总计                     154 个 Java 文件
```
