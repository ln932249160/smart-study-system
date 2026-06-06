# anatomy.md

> Auto-maintained by OpenWolf. Last scanned: 2026-06-06T09:01:00.710Z
> Files: 180 tracked | Anatomy hits: 0 | Misses: 0

## ./

- `.gitignore` — Git ignore rules (~3 tok)
- `business.md` — 业务流程文档 (~2455 tok)
- `CLAUDE.md` — CLAUDE.md (~1094 tok)
- `hello.html` — Hello World (~211 tok)
- `pom.xml` — Maven project configuration (~977 tok)
- `README.md` — Project documentation (~1800 tok)

## .claude/

- `settings.json` (~441 tok)
- `settings.local.json` (~115 tok)

## .claude/rules/

- `openwolf.md` (~313 tok)

## docs/

- `excel-import-api.md` — 用户 Excel 导入/模板下载 API (~485 tok)

## docs/superpowers/plans/

- `2026-05-22-lightweight-ddd-backend-plan.md` — Lightweight DDD Backend Implementation Plan (~4107 tok)

## docs/superpowers/specs/

- `2026-05-22-lightweight-ddd-backend-design.md` — Lightweight DDD Backend Design (~468 tok)

## src/main/java/com/kg/

- `DomainConfig.java` — BCrypt 密码编码器，用于密码加密与校验 (~190 tok)
- `KgApplication.java` — KgApplication: main (~137 tok)

## src/main/java/com/kg/application/service/

- `.gitkeep` (~0 tok)
- `AuthApplicationService.java` — 认证应用服务 —— 处理登录业务流程。 (~684 tok)
- `BatchTaskService.java` — 批处理任务服务 —— 定时任务的共享逻辑。 (~1859 tok)
- `ClassApplicationService.java` — 班级管理应用服务 —— teacher 全部操作，headmaster 仅看本班，student 无权限。 (~2430 tok)
- `DictApplicationService.java` — 字典管理应用服务 — 按 dict_code 分组管理 (~1260 tok)
- `HomeApplicationService.java` — 首页统计应用服务 —— 基于聚合 SQL 提供 teacher / headmaster / student 三种视角的统计数据。 (~1832 tok)
- `LeaveRequestApplicationService.java` — 请假申请应用服务 —— 学生请假、老师/班长审批。 (~4590 tok)
- `MyTaskApplicationService.java` — 我的任务应用服务 —— 个人任务列表 + 完成任务。 (~2257 tok)
- `NotificationApplicationService.java` — 通知消息应用服务 —— 查询列表 + 已读 + 未读数。 (~1594 tok)
- `NotificationService.java` — 通知消息生成服务 —— 封装消息生成逻辑，在任务创建/编辑时调用。 (~1412 tok)
- `ProfileApplicationService.java` — 个人中心应用服务 —— 查看/修改个人信息、修改密码。 (~1172 tok)
- `StudentApplicationService.java` — 学生管理应用服务 —— teacher 全部操作，headmaster 看本班，student 无权限。 (~3522 tok)
- `StudentCheckinApplicationService.java` — 学生打卡统计服务 —— 连续天数 + 本月次数 + 日历。 (~749 tok)
- `TaskApplicationService.java` — 任务管理应用服务 —— 角色权限：teacher 全部、headmaster 本班、student 本人。 (~4426 tok)
- `TaskTemplateApplicationService.java` — 模板任务应用服务 — 仅 teacher (~1168 tok)
- `UserApplicationService.java` — 应用层服务 — 编排业务流程。 (~150 tok)

## src/main/java/com/kg/config/

- `DataInitializer.java` — 数据初始化器 —— 启动时检查并创建默认管理员账号。 (~626 tok)
- `OpenApiConfig.java` — OpenAPI 配置 —— 声明 JWT Bearer Token 认证方式与标签展示顺序。 (~578 tok)
- `WebMvcConfig.java` — Web MVC 配置 —— 注册 JWT 认证拦截器。 (~310 tok)

## src/main/java/com/kg/context/

- `UserContext.java` — 用户上下文 —— 基于 ThreadLocal 存储当前请求的用户信息。 (~249 tok)

## src/main/java/com/kg/domain/model/

- `.gitkeep` (~0 tok)
- `ClassInfo.java` — 班级领域模型 —— 字段来源于 class_info 表。 (~468 tok)
- `DictItem.java` — 字典项领域模型 (~439 tok)
- `LeaveRequest.java` — 请假申请领域模型 —— 字段来源于 leave_request 表。 (~933 tok)
- `SysUser.java` — 系统用户领域模型 —— 纯 POJO，字段严格来源于 sys_user 表。 (~893 tok)
- `Task.java` — 任务领域模型 —— 字段来源于 task 表（15 列，含 template_id）。 (~803 tok)
- `TaskScore.java` — 任务成绩明细领域模型 —— 字段来源于 task_score 表。 (~475 tok)
- `TaskTemplate.java` — 模板任务领域模型 (~492 tok)
- `TaskUser.java` — 任务分配领域模型 —— 字段来源于 task_user 表。 (~803 tok)
- `User.java` — 领域模型 — 纯 POJO，不依赖任何框架 (~374 tok)

## src/main/java/com/kg/domain/repository/

- `.gitkeep` (~0 tok)
- `ClassInfoRepository.java` — 班级仓储接口 —— 定义 class_info 的数据访问契约。 (~303 tok)
- `DictRepository.java` — sys_dict 仓储接口 (~157 tok)
- `LeaveRequestRepository.java` — 请假申请仓储接口 (~276 tok)
- `SysUserRepository.java` — 系统用户仓储接口 —— 定义 sys_user 的数据访问契约。 (~425 tok)
- `TaskRepository.java` — 任务仓储接口 —— 定义 task 表的数据访问契约。 (~338 tok)
- `TaskScoreRepository.java` — 任务成绩明细仓储接口 —— 定义 task_score 表的数据访问契约。 (~112 tok)
- `TaskTemplateRepository.java` — 模板任务仓储接口 (~172 tok)
- `TaskUserRepository.java` — 任务分配仓储接口 —— 定义 task_user 表的数据访问契约。 (~187 tok)
- `UserRepository.java` — 仓储接口 — 只定义契约，不依赖数据库实现。 (~66 tok)

## src/main/java/com/kg/domain/service/

- `.gitkeep` (~0 tok)
- `UserDomainService.java` — 领域服务 — 封装与 User 聚合相关的业务规则。 (~151 tok)

## src/main/java/com/kg/enums/

- `ModuleNameEnum.java` — 模块名称枚举 —— 对应 sys_dict MODULE_NAME。 (~158 tok)
- `RoleEnum.java` — 角色枚举 —— 对应 sys_dict ROLE。 (~223 tok)
- `TaskStatusEnum.java` — 任务状态枚举 —— 对应 sys_dict TASK_STATUS。 (~121 tok)

## src/main/java/com/kg/exception/

- `BusinessException.java` — 业务异常 —— 用于向调用方返回可读的错误信息。 (~120 tok)
- `GlobalExceptionHandler.java` — 全局异常处理器 —— 所有异常统一返回 {code, message}。 (~1325 tok)

## src/main/java/com/kg/infrastructure/converter/

- `.gitkeep` (~0 tok)
- `ClassInfoConverter.java` — 防腐层转换器 —— domain.ClassInfo ↔ infra.ClassInfoEntity 双向映射。 (~456 tok)
- `DictConverter.java` — DictItem ↔ DictEntity (~336 tok)
- `LeaveRequestConverter.java` — LeaveRequest ↔ LeaveRequestEntity (~510 tok)
- `SysUserConverter.java` — 防腐层转换器 —— domain.SysUser ↔ infra.SysUserEntity 双向映射。 (~659 tok)
- `TaskConverter.java` — 防腐层转换器 —— domain.Task ↔ infra.TaskEntity（14 列）。 (~517 tok)
- `TaskScoreConverter.java` — 防腐层转换器 —— domain.TaskScore ↔ infra.TaskScoreEntity (~342 tok)
- `TaskTemplateConverter.java` — domain.TaskTemplate ↔ infra.TaskTemplateEntity (~390 tok)
- `TaskUserConverter.java` — 防腐层转换器 —— domain.TaskUser ↔ infra.TaskUserEntity (~481 tok)
- `UserConverter.java` — 防腐层转换器 — domain.User ↔ infra.UserEntity 双向映射。 (~252 tok)

## src/main/java/com/kg/infrastructure/entity/

- `.gitkeep` (~0 tok)
- `ClassInfoEntity.java` — 班级持久化实体，映射 class_info 表。 (~532 tok)
- `DictEntity.java` — 字典持久化实体，映射 sys_dict 表。 (~545 tok)
- `LeaveRequestEntity.java` — 请假申请持久化实体，映射 leave_request 表。 (~994 tok)
- `NotificationMessageEntity.java` — 通知消息持久化实体，映射 notification_message 表。 (~636 tok)
- `SysUserEntity.java` — 系统用户持久化实体，严格映射 sys_user 表（16 列全部覆盖）。 (~912 tok)
- `TaskEntity.java` — 任务持久化实体，映射 task 表（15 列）。 (~858 tok)
- `TaskScoreEntity.java` — 任务成绩明细持久化实体，映射 task_score 表。 (~478 tok)
- `TaskTemplateEntity.java` — 模板任务持久化实体，映射 task_template 表 (~502 tok)
- `TaskUserEntity.java` — 任务分配持久化实体，映射 task_user 表。 (~773 tok)
- `UserEntity.java` — 持久化实体 — 仅在 infrastructure 层可见。 (~269 tok)

## src/main/java/com/kg/infrastructure/mapper/

- `.gitkeep` (~0 tok)
- `ClassInfoMapper.java` — 班级 Mapper，操作 class_info 表。 (~90 tok)
- `DictMapper.java` — 字典 Mapper (~80 tok)
- `HomeMapper.java` — 首页统计 Mapper —— 聚合 SQL，一次查询完成统计，禁止 N+1。 (~1104 tok)
- `LeaveRequestMapper.java` — 请假申请 Mapper，操作 leave_request 表。 (~1332 tok)
- `MyTaskMapper.java` — 我的任务 Mapper —— 以 task_user 为核心 JOIN task，一次 SQL 查完。 (~1055 tok)
- `NotificationMessageMapper.java` — 通知消息 Mapper，操作 notification_message 表。 (~102 tok)
- `StudentCheckinMapper.java` — 学生打卡 Mapper —— 查询当前学生所有已完成打卡日期。 (~190 tok)
- `SysUserMapper.java` — 系统用户 Mapper，操作 sys_user 表。 (~88 tok)
- `TaskMapper.java` — 任务 Mapper (~80 tok)
- `TaskScoreMapper.java` — 任务成绩明细 Mapper (~85 tok)
- `TaskTemplateMapper.java` — 模板任务 Mapper (~81 tok)
- `TaskUserMapper.java` — 任务分配 Mapper (~84 tok)
- `UserMapper.java` — Class: UserMapper (~75 tok)

## src/main/java/com/kg/infrastructure/repository/

- `.gitkeep` (~0 tok)
- `ClassInfoRepositoryImpl.java` — 班级仓储实现 —— 使用 MyBatis-Plus 操作 MySQL。 (~1414 tok)
- `DictRepositoryImpl.java` — 字典仓储实现 (~1165 tok)
- `LeaveRequestRepositoryImpl.java` — 请假申请仓储实现 (~1539 tok)
- `SysUserRepositoryImpl.java` — 系统用户仓储实现 —— 使用 MyBatis-Plus 操作 MySQL。 (~2056 tok)
- `TaskRepositoryImpl.java` — 任务仓储实现。 (~1877 tok)
- `TaskScoreRepositoryImpl.java` — 任务成绩明细仓储实现。 (~456 tok)
- `TaskTemplateRepositoryImpl.java` — 模板任务仓储实现 (~1016 tok)
- `TaskUserRepositoryImpl.java` — 任务分配仓储实现。 (~1064 tok)
- `UserRepositoryImpl.java` — 仓储实现 — 使用 MyBatis-Plus 操作 MySQL。 (~324 tok)

## src/main/java/com/kg/interceptor/

- `JwtInterceptor.java` — JWT 认证拦截器。 (~995 tok)

## src/main/java/com/kg/interfaces/controller/

- `.gitkeep` (~0 tok)
- `AuthController.java` — 认证接口控制器 (~396 tok)
- `BatchTaskController.java` — 批处理任务测试入口。 (~366 tok)
- `ClassController.java` — 班级管理控制器 —— 班级的增删改查及学生分配。 (~1222 tok)
- `DictController.java` — 字典管理控制器 (~1231 tok)
- `HelloController.java` — RestController: HelloController (1 endpoints) (~198 tok)
- `HomeController.java` — 首页统计控制器。 (~468 tok)
- `LeaveRequestController.java` — 请假申请控制器 —— 学生请假、老师/班长审批。 (~812 tok)
- `MyTaskController.java` — 我的任务控制器 —— 个人任务列表 + 完成任务。 (~646 tok)
- `NotificationController.java` — 通知消息控制器。 (~626 tok)
- `ProfileController.java` — 个人中心控制器。 (~657 tok)
- `StudentCheckinController.java` — 学生打卡统计控制器。 (~424 tok)
- `StudentController.java` — 用户管理控制器 —— 管理 student / headmaster 角色用户。 (~1856 tok)
- `TaskController.java` — 任务管理控制器。 (~792 tok)
- `TaskTemplateController.java` — 模板任务管理控制器 (~858 tok)
- `UserController.java` — RestController: UserController (1 endpoints) (~230 tok)

## src/main/java/com/kg/interfaces/dto/

- `.gitkeep` (~0 tok)
- `CheckinCalendarVO.java` — 打卡日历 VO (~101 tok)
- `CheckinStatVO.java` — 学生打卡统计 VO (~227 tok)
- `ClassCreateRequest.java` — 新增班级请求 DTO (~314 tok)
- `ClassOptionVO.java` — 班级下拉选项 VO (~211 tok)
- `ClassPageRequest.java` — 班级分页查询请求 DTO (~245 tok)
- `ClassUpdateRequest.java` — 编辑班级请求 DTO (~276 tok)
- `ClassVO.java` — 班级列表项 VO (~371 tok)
- `DictGroupSaveRequest.java` — 字典组保存请求（新增/编辑通用） (~450 tok)
- `DictGroupVO.java` — 字典分组 VO (~402 tok)
- `DictItemCreateRequest.java` — DictItemCreateRequest: getDictCode, setDictCode, getDictName, setDictName (~288 tok)
- `DictItemUpdateRequest.java` — DictItemUpdateRequest: getDictCode, setDictCode, getDictName, setDictName (~333 tok)
- `DictVO.java` — 字典项 VO (~194 tok)
- `HomeStatVO.java` — 首页统计 VO（teacher / headmaster） (~644 tok)
- `LeaveApproveRequest.java` — 请假审批请求（支持批量） (~249 tok)
- `LeaveCreateRequest.java` — 请假申请新增请求 (~434 tok)
- `LeavePageRequest.java` — 请假申请分页查询请求 (~392 tok)
- `LeaveVO.java` — 请假申请列表项 VO (~907 tok)
- `LoginRequest.java` — 登录请求 DTO (~246 tok)
- `LoginResponse.java` — 登录响应 DTO (~399 tok)
- `LowCompletedTaskVO.java` — 完成率最低任务 VO (~505 tok)
- `MyTaskCompleteRequest.java` — 我的任务完成请求 DTO (~529 tok)
- `MyTaskPageRequest.java` — 我的任务分页请求 DTO (~659 tok)
- `MyTaskVO.java` — 我的任务列表项 VO (~628 tok)
- `NearEndTaskVO.java` — 即将截止任务 VO (~227 tok)
- `NotificationPageRequest.java` — 通知消息分页查询请求 (~570 tok)
- `NotificationVO.java` — 通知消息列表项 VO (~518 tok)
- `PasswordChangeRequest.java` — 修改密码请求 DTO (~231 tok)
- `ProfileUpdateRequest.java` — 修改个人资料请求 DTO (~302 tok)
- `ProfileVO.java` — 个人信息 VO (~589 tok)
- `StudentCreateRequest.java` — 新增用户请求 DTO (~695 tok)
- `StudentHomeStatVO.java` — 学生首页统计 VO (~305 tok)
- `StudentOptionVO.java` — 学生选项 VO —— 用于班级管理中的学生复选框列表。 (~285 tok)
- `StudentPageRequest.java` — 用户分页查询请求 DTO (~653 tok)
- `StudentUpdateRequest.java` — 编辑学生请求 DTO (~614 tok)
- `StudentVO.java` — 学生列表项 VO (~565 tok)
- `TaskCompleteRequest.java` — 完成任务请求 DTO (~528 tok)
- `TaskCreateRequest.java` — 新增任务请求 (~684 tok)
- `TaskPageRequest.java` — 任务分页查询请求 (~535 tok)
- `TaskTemplateCreateRequest.java` — TaskTemplateCreateRequest: getTemplateName, setTemplateName, getTaskType, setTaskType (~331 tok)
- `TaskTemplatePageRequest.java` — 模板任务分页请求 (~204 tok)
- `TaskTemplateUpdateRequest.java` — TaskTemplateUpdateRequest: getTemplateName, setTemplateName, getTaskType, setTaskType (~331 tok)
- `TaskTemplateVO.java` — TaskTemplateVO: getId, setId, getTemplateName, setTemplateName (~378 tok)
- `TaskUpdateRequest.java` — 编辑任务请求 DTO (~657 tok)
- `TaskVO.java` — 任务列表项 VO (~1158 tok)
- `UserImportDTO.java` — 用户导入 Excel 行模型 —— EasyExcel 读写用。 (~624 tok)
- `UserResponse.java` — 对外响应 DTO — 隔离领域模型。 (~290 tok)

## src/main/java/com/kg/scheduler/

- `DailyCheckInScheduler.java` — 每日打卡任务定时器 —— 每天 06:00 创建打卡任务并分配。 (~278 tok)
- `DailyReviewScheduler.java` — 每日复盘任务定时器 —— 每天 08:00 创建复盘任务并分配。 (~277 tok)
- `NotificationScheduler.java` — 消息提醒定时任务 —— 每分钟扫描 task 表生成通知。 (~274 tok)

## src/main/java/com/kg/util/

- `JwtUtil.java` — JWT 工具类 —— 负责 Token 的生成、解析与校验。 (~753 tok)

## src/main/resources/

- `1.json` (~58 tok)
- `2.json` (~91 tok)
- `application.yml` (~207 tok)
- `schema.sql` — Database schema (~688 tok)

## src/main/resources/static/

- `hello.html` — Hello World (~211 tok)

## src/test/java/com/kg/

- `.gitkeep` (~0 tok)
