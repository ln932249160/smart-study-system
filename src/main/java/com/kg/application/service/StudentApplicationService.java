package com.kg.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kg.context.UserContext;
import com.kg.domain.model.ClassInfo;
import com.kg.domain.model.SysUser;
import com.kg.domain.repository.ClassInfoRepository;
import com.kg.domain.repository.SysUserRepository;
import com.kg.enums.RoleEnum;
import com.kg.infrastructure.entity.NotificationMessageEntity;
import com.kg.infrastructure.entity.TaskEntity;
import com.kg.infrastructure.entity.TaskUserEntity;
import com.kg.infrastructure.mapper.NotificationMessageMapper;
import com.kg.infrastructure.mapper.TaskMapper;
import com.kg.infrastructure.mapper.TaskUserMapper;
import com.kg.interfaces.dto.ClassOptionVO;
import com.kg.interfaces.dto.StudentCreateRequest;
import com.kg.interfaces.dto.StudentUpdateRequest;
import com.kg.interfaces.dto.StudentVO;
import com.kg.interfaces.dto.UserImportDTO;
import com.kg.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学生管理应用服务 —— teacher 全部操作，headmaster/student 可看列表+详情。
 */
@Service
public class StudentApplicationService {

    private static final Logger log = LoggerFactory.getLogger(StudentApplicationService.class);
    /** headmaster / student */
    private static final List<String> MANAGEABLE_ROLES = Arrays.asList(
            RoleEnum.HEADMASTER.getCode(), RoleEnum.STUDENT.getCode());
    /** 全部角色 */
    private static final List<String> ALL_ROLES = Arrays.asList(
            RoleEnum.TEACHER.getCode(), RoleEnum.HEADMASTER.getCode(), RoleEnum.STUDENT.getCode());
    private static final String DEFAULT_PASSWORD = "123456";
    private static final int STATUS_ACTIVE = 1;

    private final SysUserRepository sysUserRepository;
    private final ClassInfoRepository classInfoRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TaskMapper taskMapper;
    private final TaskUserMapper taskUserMapper;
    private final NotificationMessageMapper notificationMessageMapper;

    public StudentApplicationService(SysUserRepository sysUserRepository,
                                     ClassInfoRepository classInfoRepository,
                                     BCryptPasswordEncoder passwordEncoder,
                                     TaskMapper taskMapper,
                                     TaskUserMapper taskUserMapper,
                                     NotificationMessageMapper notificationMessageMapper) {
        this.sysUserRepository = sysUserRepository;
        this.classInfoRepository = classInfoRepository;
        this.passwordEncoder = passwordEncoder;
        this.taskMapper = taskMapper;
        this.taskUserMapper = taskUserMapper;
        this.notificationMessageMapper = notificationMessageMapper;
    }

    // ======================== 分页查询 ========================

    /**
     * teacher → 全部角色 | headmaster → 本班学生+班长 | student → 无权限
     */
    public Map<String, Object> page(String name, String role, String phone, Long userId,
                                     Long classId, String className, int pageNum, int pageSize) {
        SysUser currentUser = requireCurrentUser();
        String currentRole = currentUser.getRole();
        int offset = (pageNum - 1) * pageSize;

        // 班级名模糊匹配 → 查 class_info 获取 classIds
        List<Long> classIds = null;
        if (className != null && !className.trim().isEmpty()) {
            classIds = classInfoRepository.findIdsByClassNameLike(className.trim());
            if (classIds.isEmpty()) {
                return emptyPageResult();
            }
        } else if (classId != null) {
            classIds = Collections.singletonList(classId);
        }

        List<SysUser> users;
        long total;
        List<String> searchRoles;

                    searchRoles = ALL_ROLES;


//        if (RoleEnum.isHeadmaster(currentRole)) {
//            Long currentClassId = currentUser.getClassId();
//            if (currentClassId == null) return emptyPageResult();
//            if (classIds != null && !classIds.contains(currentClassId)) return emptyPageResult();
//            classIds = Collections.singletonList(currentClassId);
//            searchRoles = MANAGEABLE_ROLES;
//        } else if (RoleEnum.isStudent(currentRole)) {
//            Long currentClassId = currentUser.getClassId();
//            if (currentClassId == null) return emptyPageResult();
//            classIds = Collections.singletonList(currentClassId);
//            searchRoles = ALL_ROLES;
//        } else {
//            searchRoles = ALL_ROLES;
//        }

        total = sysUserRepository.countByFilters(name, role, phone, userId,
                classIds, searchRoles);
        users = sysUserRepository.pageByFilters(name, role, phone, userId,
                classIds, searchRoles, offset, pageSize);

        List<StudentVO> list = users.stream().map(this::toVO).collect(Collectors.toList());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", total);
        result.put("list", list);
        return result;
    }

    // ======================== 详情 ========================

    /** 查看用户详情 */
    public StudentVO getById(Long id) {
        SysUser user = sysUserRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return toVO(user);
    }

    // ======================== 新增（仅 teacher） ========================

    public void create(StudentCreateRequest request) {
        Long currentUserId = requireTeacher();

        String account = (request.getAccount() != null && !request.getAccount().trim().isEmpty())
                ? request.getAccount().trim()
                : request.getPhone();

        if (sysUserRepository.findByAccount(account).isPresent()) {
            throw new BusinessException("该账号已存在：" + account);
        }

        SysUser sysUser = new SysUser();
        sysUser.setAccount(account);
        sysUser.setName(request.getName());
        sysUser.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        sysUser.setRole(request.getRole());
        sysUser.setGender(request.getGender());
        sysUser.setEmail(request.getEmail());
        sysUser.setPhone(request.getPhone());
        sysUser.setDescription(request.getDescription());
        sysUser.setTeacherRemark(request.getTeacherRemark());
        sysUser.setClassId(request.getClassId());
        sysUser.setStatus(STATUS_ACTIVE);
        sysUser.setCreateBy(currentUserId);
        sysUser.setCreateTime(LocalDateTime.now());

        sysUserRepository.save(sysUser);
        log.info("新增学生: id={}, account={}, name={}, role={}", sysUser.getId(), account, request.getName(), request.getRole());

        // 补发班级任务
        if (sysUser.getClassId() != null) {
            assignClassTasksToNewStudent(sysUser.getId(), sysUser.getClassId());
        }
    }

    // ======================== 编辑（仅 teacher） ========================

    public void update(Long id, StudentUpdateRequest request) {
        Long currentUserId = requireTeacher();
        SysUser sysUser = new SysUser();
        sysUser.setId(id);
        if (request.getName() != null) sysUser.setName(request.getName().trim());

        String account = (request.getAccount() != null && !request.getAccount().trim().isEmpty())
                ? request.getAccount().trim()
                : request.getPhone();

        Optional<SysUser> byAccount = sysUserRepository.findByAccount(account);
        if (byAccount.isPresent()) {
            SysUser user = byAccount.get();
//            数据库里的account和现在的account不是一个用户的
            if(!Objects.equals(user.getId(), id)){
                throw new BusinessException("该账号已存在：" + account);
            }
        }

        if (request.getAccount() != null) sysUser.setAccount(account);
        if (request.getPhone() != null) sysUser.setPhone(request.getPhone().trim());
        if (request.getRole() != null) sysUser.setRole(request.getRole());
        if (request.getGender() != null) sysUser.setGender(request.getGender());
        if (request.getEmail() != null) sysUser.setEmail(request.getEmail());
        if (request.getDescription() != null) sysUser.setDescription(request.getDescription());
        if (request.getTeacherRemark() != null) sysUser.setTeacherRemark(request.getTeacherRemark());
        if (request.getClassId() != null) sysUser.setClassId(request.getClassId());
        if (request.getStatus() != null) sysUser.setStatus(request.getStatus());
        sysUser.setUpdateBy(currentUserId);
        sysUserRepository.update(sysUser);
        log.info("编辑学生成功: id={}", id);
    }

    // ======================== 删除（仅 teacher） ========================

    public void delete(Long id) {
        requireTeacher();
        sysUserRepository.deleteById(id);
        log.info("删除学生成功: id={}", id);
    }

    // ======================== 重置密码（仅老师） ========================

    /** 老师将指定用户密码重置为默认密码 123456 */
    public void resetPassword(Long userId) {
        Long currentUserId = requireTeacher();
        SysUser existing = sysUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        SysUser update = new SysUser();
        update.setId(userId);
        update.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        update.setPasswordUpdateTime(LocalDateTime.now());
        update.setUpdateBy(currentUserId);
        sysUserRepository.update(update);
        log.info("重置密码成功: targetUserId={}, operatorId={}", userId, currentUserId);
    }

    // ======================== 班级下拉 ========================

    public List<ClassOptionVO> listClassOptions() {
        return classInfoRepository.listActiveClasses().stream()
                .map(c -> ClassOptionVO.of(c.getId(), c.getClassName()))
                .collect(Collectors.toList());
    }

    // ======================== Excel 批量导入 ========================

    /**
     * 批量导入用户。
     *
     * @param list Excel 解析后的用户列表
     * @return { success: 成功数, fail: 失败数, errors: ["行2: 手机号为空", ...] }
     */
    public Map<String, Object> importUsers(List<UserImportDTO> list) {
        Long currentUserId = requireTeacher();
        int success = 0;
        int fail = 0;
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            UserImportDTO dto = list.get(i);
            int row = i + 2; // Excel 行号（第1行是表头）
            try {
                // 基本校验
                if (dto.getPhone() == null || dto.getPhone().trim().isEmpty()) {
                    errors.add("行" + row + ": 手机号为空，跳过");
                    fail++;
                    continue;
                }
                if (dto.getName() == null || dto.getName().trim().isEmpty()) {
                    errors.add("行" + row + ": 姓名为空，跳过");
                    fail++;
                    continue;
                }

                String account = (dto.getAccount() != null && !dto.getAccount().trim().isEmpty())
                        ? dto.getAccount().trim()
                        : dto.getPhone().trim();

                // 账号去重
                if (sysUserRepository.findByAccount(account).isPresent()) {
                    errors.add("行" + row + ": 账号已存在 " + account + "，跳过");
                    fail++;
                    continue;
                }

                SysUser sysUser = new SysUser();
                sysUser.setAccount(account);
                sysUser.setName(dto.getName().trim());
                sysUser.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
                sysUser.setRole(dto.getRole() != null ? dto.getRole() : RoleEnum.STUDENT.getCode());
                sysUser.setGender(dto.getGender() != null ? dto.getGender() : 0);
                sysUser.setPhone(dto.getPhone().trim());
                sysUser.setEmail(dto.getEmail());
                sysUser.setDescription(dto.getDescription());
                // 班级名称 → 班级ID
                if (dto.getClassName() != null && !dto.getClassName().trim().isEmpty()) {
                    ClassInfo cls = classInfoRepository.findByClassName(dto.getClassName().trim())
                            .orElse(null);
                    if (cls == null) {
                        errors.add("行" + row + ": 班级不存在 " + dto.getClassName() + "，跳过");
                        fail++;
                        continue;
                    }
                    sysUser.setClassId(cls.getId());
                }
                sysUser.setStatus(STATUS_ACTIVE);
                sysUser.setCreateBy(currentUserId);
                sysUser.setCreateTime(LocalDateTime.now());

                sysUserRepository.save(sysUser);
                success++;
                // 补发班级任务
                if (sysUser.getClassId() != null) {
                    assignClassTasksToNewStudent(sysUser.getId(), sysUser.getClassId());
                }
            } catch (Exception e) {
                log.warn("导入行 {} 失败: {}", row, e.getMessage());
                errors.add("行" + row + ": " + e.getMessage());
                fail++;
            }
        }

        log.info("批量导入完成: 成功{}人, 失败{}人", success, fail);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", success);
        result.put("fail", fail);
        result.put("errors", errors);
        return result;
    }

    // ======================== 新学生补发班级任务 ========================

    /**
     * 新学生加入班级后，将该班级所有未结束的班级任务补发给他。
     * 校验 task_id + user_id 避免重复分配。
     */
    private void assignClassTasksToNewStudent(Long studentId, Long classId) {
        List<TaskEntity> tasks = taskMapper.findUnfinishedClassTasks(classId);
        if (tasks == null || tasks.isEmpty()) return;

        LocalDateTime now = LocalDateTime.now();
        int assigned = 0;

        for (TaskEntity task : tasks) {
            // 幂等：检查是否已存在
            LambdaQueryWrapper<TaskUserEntity> check = new LambdaQueryWrapper<>();
            check.eq(TaskUserEntity::getTaskId, task.getId());
            check.eq(TaskUserEntity::getUserId, studentId);
            if (taskUserMapper.selectCount(check) > 0) continue;

            // 插入 task_user
            TaskUserEntity tu = new TaskUserEntity();
            tu.setTaskId(task.getId());
            tu.setUserId(studentId);
            tu.setStatus("0");
            tu.setCreateBy(0L);
            taskUserMapper.insert(tu);

            // 生成通知消息
            NotificationMessageEntity msg = new NotificationMessageEntity();
            msg.setUserId(studentId);
            msg.setTitle(task.getTaskName() + "开始了");
            msg.setContent(task.getTaskDescription() != null ? task.getTaskDescription() : "");
            msg.setPriority(1);
            msg.setType("TASK_START");
            msg.setRelatedId(task.getId());
            msg.setIsRead(0);
            msg.setStatus("0");
            msg.setCreatedAt(now);
            msg.setNotifyTime(now);
            notificationMessageMapper.insert(msg);

            assigned++;
        }

        if (assigned > 0) {
            log.info("新学生 {} 补发 {} 个班级任务 (classId={})", studentId, assigned, classId);
        }
    }

    // ======================== 权限 ========================

    private SysUser requireCurrentUser() {
        SysUser user = UserContext.getUser();
        if (user == null) throw new BusinessException(401, "未登录");
        return user;
    }

    private Long requireTeacher() {
        SysUser user = requireCurrentUser();
        if (!RoleEnum.isTeacher(user.getRole())) {
            throw new BusinessException(403, "仅老师可操作");
        }
        return user.getId();
    }

    // ======================== 私有 ========================

    private Map<String, Object> emptyPageResult() {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("total", 0);
        r.put("list", Collections.emptyList());
        return r;
    }

    private StudentVO toVO(SysUser user) {
        StudentVO vo = new StudentVO();
        vo.setId(user.getId());
        vo.setName(user.getName());
        vo.setRole(user.getRole());
        vo.setPhone(user.getPhone());
        vo.setClassId(user.getClassId());
        vo.setGender(user.getGender());
        vo.setEmail(user.getEmail());
        vo.setStatus(user.getStatus());
        if (RoleEnum.isTeacher(requireCurrentUser().getRole())) {
            vo.setTeacherRemark(user.getTeacherRemark());
        }
        if (user.getCreateTime() != null) {
            vo.setCreateTime(user.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        return vo;
    }
}
