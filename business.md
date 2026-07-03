# 业务流程文档

## 一、认证登录流程

```
客户端                    服务端                     数据库
  │                        │                         │
  │  POST /auth/login      │                         │
  │  {account, password}   │                         │
  │ ──────────────────────→│                         │
  │                        │  findByAccount(account) │
  │                        │ ───────────────────────→│ sys_user
  │                        │ ←───────────────────────│
  │                        │                         │
  │                        │  BCrypt.matches(password, hash)
  │                        │  check status = 1       │
  │                        │                         │
  │                        │  JwtUtil.generateToken( │
  │                        │    userId, role)        │
  │                        │                         │
  │  {code:200, data:{     │                         │
  │   token, userId,       │                         │
  │   account, role}}      │                         │
  │ ←──────────────────────│                         │
  │                        │                         │
  │  后续请求 Header:       │                         │
  │  Authorization:        │                         │
  │  Bearer <token>        │                         │
  │ ──────────────────────→│                         │
  │                        │  JwtInterceptor:        │
  │                        │  验签 → 解析 userId+role│
  │                        │  → UserContext.setUser()│
  │                        │  → Controller 处理      │
  │                        │  → afterCompletion:     │
  │                        │    UserContext.clear()   │
```

### BCrypt 密码说明

- 密码存储为 BCrypt 密文（`$2a$10$...`）
- 登录时用 `BCryptPasswordEncoder.matches(明文, 密文)` 校验
- 修改密码时验原密码 → `encode(新密码)` → 写入
- 新增用户默认密码 `123456`，BCrypt 加密后存储

### JWT 结构

```
Header:  { "alg": "HS256" }
Payload: { "sub": "6", "role": "1", "iat": ..., "exp": ... }
Secret:  硬编码 HMAC-SHA256 密钥（24h 过期）
```

---

## 二、学生管理流程

### 2.1 新增学生

```
teacher 登录
  │
  │  POST /student
  │  { name, role(2/3), gender, phone, email?, description?, classId, account? }
  │
  ├─→ account = account ?? phone          // 不填默认手机号
  ├─→ 检查 account 是否已存在（唯一）
  ├─→ BCrypt.encode("123456")             // 默认密码
  ├─→ create_by = 当前登录用户 ID
  ├─→ status = 1（正常）
  └─→ INSERT sys_user
```

### 2.2 查询学生列表

```
POST /student/page  { name?, pageNum, pageSize }
  │
  ├─ teacher:   查全部 role IN (2,3)
  ├─ headmaster: 只查本班 (class_id = 当前用户.class_id)
  └─ student:   返回 403
```

### 2.3 编辑学生

```
PUT /student/{id}  { name?, role?, gender?, phone?, email?, description?, classId?, status? }
  │
  ├─ teacher 专属
  ├─ update_by = 当前用户 ID
  └─→ UPDATE sys_user（只更新非 null 字段）
```

### 2.4 删除学生

```
DELETE /student/{id}
  │
  ├─ teacher 专属
  └─→ DELETE FROM sys_user WHERE id = {id}
```

---

## 三、班级管理流程

### 3.1 新增班级 + 分配学生

```
POST /class  { className, description?, studentIds[] }
  │
  ├─ teacher 专属
  │
  ├─ 1. INSERT class_info
  │     className, status=1, create_by=当前用户, create_time=NOW
  │
  └─ 2. 如果 studentIds 不为空:
         ├─ batchUpdateClassId(studentIds, NULL)  // 先解除原班级
         └─ batchUpdateClassId(studentIds, newClassId) // 绑定新班级
```

### 3.2 编辑班级

```
PUT /class/{id}  { className?, description?, studentIds[] }
  │
  ├─ teacher 专属
  │
  ├─ 1. UPDATE class_info
  │
  ├─ 2. clearClassId(id)            // 该班所有学生 → class_id = NULL
  │
  └─ 3. 新学生列表:
         ├─ batchUpdateClassId(newIds, NULL)  // 解除原班级
         └─ batchUpdateClassId(newIds, id)    // 绑定当前班级
```

### 3.3 删除班级

```
DELETE /class/{id}
  │
  ├─ teacher 专属
  │
  ├─ 1. clearClassId(id)            // 学生回归未分班
  └─ 2. DELETE FROM class_info
```

### 3.4 学生复选框

```
GET /class/student-options
  │
  ├─ teacher:   查全部 role=student, status=1
  ├─ headmaster: 只查本班 role=student
  └─ 返回: [{ id, name, classId }]
```

---

## 四、任务管理流程（teacher 专属）

### 4.1 新增任务 + 分配

```
POST /task  { taskName, taskType, isTemplate?, ... }
  │
  ├─ teacher 专属
  │
  ├─ 1. INSERT task
  │     status 默认 unfinished
  │
  ├─ 2. 如果 is_template=1 → 跳过分配（模板任务）
  │
  └─ 3. 确定分配对象:
         ├─ 选了 classId → 查 sys_user WHERE class_id = ?
         ├─ 选了 studentIds → 用指定 ID 列表
         │
         └─ 批量 INSERT task_user:
              { task_id, user_id, status='0' }
```

### 4.2 任务列表查询

```
POST /task/page
  │
  ├─ teacher:    全部非模板 task
  ├─ headmaster: task WHERE create_by = 本人
  ├─ student:    查 task_user WHERE user_id = 本人 → 获取 taskIds → 查 task
  │
  └─ 每条 task 附: completedCount / uncompletedCount / completionPercent
     (来自 task_user 实时 GROUP BY)
```

### 4.3 编辑任务

```
PUT /task/{id}
  │
  ├─ 1. UPDATE task
  ├─ 2. DELETE task_score WHERE task_id = id
  ├─ 3. DELETE task_user WHERE task_id = id
  └─ 4. 按新规则重新生成 task_user
```

### 4.4 删除任务

```
DELETE /task/{id}
  │
  ├─ 1. DELETE task_score WHERE task_id = id  (子查询关联 task_user)
  ├─ 2. DELETE task_user WHERE task_id = id
  └─ 3. DELETE task WHERE id = id
```

---

## 五、我的任务流程（student + headmaster）

### 5.1 任务列表

```
GET /my-task/list?pageNum=1&pageSize=10&status=0
  │
  ├─ 以 task_user 为核心表，JOIN task 获取任务信息
  │
  ├─ SQL:
  │   SELECT t.id, t.task_name, t.task_type, t.is_mandatory,
  │          t.task_start_time, t.task_end_time,
  │          tu.status, tu.total_score, tu.submit_time
  │   FROM task_user tu JOIN task t ON t.id = tu.task_id
  │   WHERE tu.user_id = #{userId}
  │   ORDER BY t.is_mandatory DESC, t.create_time DESC
  │
  └─ status: 0=未完成 / 1=已完成 / 不传=全部
```

### 5.2 完成任务

```
POST /my-task/complete  { taskId, durationMinutes?, remark?, totalScore?, scores[] }
  │
  ├─ 1. 查 task_user WHERE task_id = ? AND user_id = 当前用户
  │     → 不存在报 "未找到您的任务分配记录"
  │
  ├─ 2. 更新 task_user:
  │     status = '1' (已完成)
  │     finish_time = NOW
  │     submit_time = NOW
  │     duration_minutes = ?
  │     remark = ?
  │     total_score = ?
  │
  └─ 3. 如果有成绩明细:
          ├─ 批量 INSERT task_score
          └─ 如果未传 totalScore → 自动求和更新
```

---

## 六、首页统计流程

### 6.1 teacher / headmaster 首页

```
GET /home/stat
  │
  │  teacher: 全表 / headmaster: create_by = 本人
  │
  ├─ 进行中任务数:
  │   COUNT(*) FROM task WHERE start_time <= NOW AND end_time >= NOW AND is_template=0
  │
  ├─ 强制任务数:
  │   COUNT(*) FROM task WHERE is_mandatory=1 AND is_template=0
  │
  ├─ 总任务数:
  │   COUNT(*) FROM task WHERE is_template=0
  │
  ├─ 完成统计:
  │   task_user JOIN task GROUP BY:
  │   total / completed(status='1') / uncompleted(status='0')
  │
  └─ 完成率最低 TOP3:
      task JOIN task_user GROUP BY task_id
      ORDER BY completed_rate ASC LIMIT 3
```

### 6.2 student 首页

```
GET /home/student/stat
  │
  ├─ 待完成: COUNT task_user WHERE user_id=me AND status='0'
  ├─ 已完成: COUNT task_user WHERE user_id=me AND status='1'
  └─ 即将截止 TOP3:
      task_user JOIN task WHERE user_id=me AND status='0'
      ORDER BY task.task_end_time ASC LIMIT 3
```

---

## 七、个人中心流程

```
GET /profile
  → findById(当前用户ID) → 返回完整 ProfileVO

PUT /profile
  → 只更新 name/gender/email/phone/description
  → update_by = 当前用户 ID

PUT /profile/password
  → findById 查完整用户（含密码）
  → BCrypt.matches(oldPassword, 密码哈希)
  → 不匹配 → "原密码错误"
  → BCrypt.encode(newPassword) → UPDATE
```

---

## 八、通知消息流程

### 8.1 消息生成（定时任务）

```
每分钟: NotificationScheduler
  │
  ├─ 1. 查全部 is_template=0 的 task
  │
  ├─ 2. 对每个 task 判断:
  │     ├─ NOW 分钟 = task_start_time 分钟 → TASK_START
  │     ├─ NOW 分钟 = task_end_time - 1小时  → TASK_WARNING
  │     └─ NOW 分钟 = task_end_time 分钟 → TASK_END
  │
  ├─ 3. 查 task_user 获取该任务分配的所有用户
  │
  ├─ 4. 防重检查:
  │     SELECT COUNT(*) FROM notification_message
  │     WHERE user_id=? AND related_id=? AND type=?
  │     → >0 则跳过
  │
  └─ 5. 批量 INSERT notification_message
```

### 8.2 消息查看

```
GET /notification/list
  → WHERE user_id = 当前用户
  → ORDER BY created_at DESC
  → 分页
```

### 8.3 已读

```
POST /notification/read  { messageId }
  → 校验 user_id 归属
  → UPDATE is_read = 1
```

---

## 九、每日自动任务流程

```
┌─────────────────────────────────────────────────────┐
│                   每天 06:00                         │
│  DailyCheckInScheduler                              │
│  ├─ 检查: task(type=1, create_time 为今天) 已存在?     │
│  │   → 已存在则跳过                                   │
│  ├─ INSERT task:                                     │
│  │   name="每日打卡", type="1", is_mandatory=1        │
│  │   start=06:00, end=23:59, create_by=0             │
│  ├─ 查 sys_user WHERE role IN (2,3) AND status=1    │
│  └─ 批量 INSERT task_user                            │
│                                                      │
│                   每天 08:00                         │
│  DailyReviewScheduler                                │
│  └─ 同上，name="每日复盘", type="0"                    │
└─────────────────────────────────────────────────────┘
```

---

## 十、异常处理流程

```
Controller 抛出异常
  │
  ├─ BusinessException    → {code: 自定义, message: "..."}
  ├─ @Valid 校验失败       → {code: 400, message: "字段xxx"}
  ├─ 路径参数类型错误       → {code: 400, message: "参数格式错误"}
  ├─ 请求体 JSON 错误      → {code: 400, message: "请求体格式错误"}
  ├─ 404                  → {code: 404, message: "接口不存在"}
  ├─ 405                  → {code: 405, message: "不支持的请求方法"}
  ├─ DuplicateKeyException → {code: 400, message: "数据已存在"}
  └─ 其他 Exception        → {code: 500, message: "系统内部错误"}
```

---

## 十一、角色取值对照

| 角色 | dict_code | value | 枚举常量 |
|------|-----------|-------|----------|
| 老师 | ROLE | 1 | `RoleEnum.TEACHER` |
| 班长 | ROLE | 2 | `RoleEnum.HEADMASTER` |
| 学生 | ROLE | 3 | `RoleEnum.STUDENT` |

| 任务状态 | dict_code | value | 枚举常量 |
|---------|-----------|-------|----------|
| 未完成 | TASK_STATUS | 0 | `TaskStatusEnum.UNFINISHED` |
| 已完成 | TASK_STATUS | 1 | `TaskStatusEnum.FINISHED` |

| 任务类型 | value |
|---------|-------|
| 每日复盘 | 0 |
| 打卡 | 1 |
| 学习 | 2 |
| 刷题 | 3 |

| 消息类型 | 触发时机 |
|---------|---------|
| TASK_START | 任务开始时间到达 |
| TASK_WARNING | 距截止还有 1 小时 |
| TASK_END | 任务结束时间到达 |
| SYSTEM | 系统通知（预留） |
