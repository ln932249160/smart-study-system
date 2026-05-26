package com.kg.application.service;

import com.kg.context.UserContext;
import com.kg.domain.model.ClassInfo;
import com.kg.domain.model.SysUser;
import com.kg.domain.repository.ClassInfoRepository;
import com.kg.domain.repository.SysUserRepository;
import com.kg.enums.RoleEnum;
import com.kg.interfaces.dto.ClassOptionVO;
import com.kg.interfaces.dto.StudentCreateRequest;
import com.kg.interfaces.dto.StudentUpdateRequest;
import com.kg.interfaces.dto.StudentVO;
import com.kg.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学生管理应用服务 —— teacher 全部操作，headmaster 看本班，student 无权限。
 */
@Service
public class StudentApplicationService {

    private static final Logger log = LoggerFactory.getLogger(StudentApplicationService.class);
    private static final List<String> MANAGED_ROLES = Arrays.asList(
            RoleEnum.HEADMASTER.getCode(), RoleEnum.STUDENT.getCode());
    private static final String DEFAULT_PASSWORD = "123456";
    private static final int STATUS_ACTIVE = 1;

    private final SysUserRepository sysUserRepository;
    private final ClassInfoRepository classInfoRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public StudentApplicationService(SysUserRepository sysUserRepository,
                                     ClassInfoRepository classInfoRepository,
                                     BCryptPasswordEncoder passwordEncoder) {
        this.sysUserRepository = sysUserRepository;
        this.classInfoRepository = classInfoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ======================== 分页查询 ========================

    /**
     * teacher → 全部 | headmaster → 仅本班 | student → 无权限
     */
    public Map<String, Object> page(String name, int pageNum, int pageSize) {
        SysUser currentUser = requireCurrentUser();
        String role = currentUser.getRole();
        int offset = (pageNum - 1) * pageSize;

        if (RoleEnum.isStudent(role)) {
            throw new BusinessException(403, "无权查看学生列表");
        }

        List<SysUser> users;
        long total;

        if (RoleEnum.isHeadmaster(role)) {
            Long classId = currentUser.getClassId();
            if (classId == null) {
                return emptyPageResult();
            }
            // 查本班用户，内存过滤角色 + 模糊名 + 分页
            List<SysUser> allInClass = sysUserRepository.findByClassId(classId);
            List<SysUser> filtered = allInClass.stream()
                    .filter(u -> MANAGED_ROLES.contains(u.getRole()))
                    .filter(u -> name == null || name.isEmpty() || (u.getName() != null && u.getName().contains(name)))
                    .collect(Collectors.toList());
            total = filtered.size();
            int to = Math.min(offset + pageSize, filtered.size());
            users = offset < filtered.size() ? filtered.subList(offset, to) : Collections.emptyList();
        } else {
            // teacher
            total = sysUserRepository.countByNameAndRoles(name, MANAGED_ROLES);
            users = sysUserRepository.pageByNameAndRoles(name, MANAGED_ROLES, offset, pageSize);
        }

        List<StudentVO> list = users.stream().map(this::toVO).collect(Collectors.toList());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", total);
        result.put("list", list);
        return result;
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
        sysUser.setClassId(request.getClassId());
        sysUser.setStatus(STATUS_ACTIVE);
        sysUser.setCreateBy(currentUserId);
        sysUser.setCreateTime(LocalDateTime.now());

        sysUserRepository.save(sysUser);
        log.info("新增学生: id={}, account={}, name={}, role={}", sysUser.getId(), account, request.getName(), request.getRole());
    }

    // ======================== 编辑（仅 teacher） ========================

    public void update(Long id, StudentUpdateRequest request) {
        Long currentUserId = requireTeacher();
        SysUser sysUser = new SysUser();
        sysUser.setId(id);
        if (request.getName() != null) sysUser.setName(request.getName().trim());
        if (request.getAccount() != null) sysUser.setAccount(request.getAccount().trim());
        if (request.getPhone() != null) sysUser.setPhone(request.getPhone().trim());
        if (request.getRole() != null) sysUser.setRole(request.getRole());
        if (request.getGender() != null) sysUser.setGender(request.getGender());
        if (request.getEmail() != null) sysUser.setEmail(request.getEmail());
        if (request.getDescription() != null) sysUser.setDescription(request.getDescription());
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

    // ======================== 班级下拉 ========================

    public List<ClassOptionVO> listClassOptions() {
        return classInfoRepository.listActiveClasses().stream()
                .map(c -> ClassOptionVO.of(c.getId(), c.getClassName()))
                .collect(Collectors.toList());
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
        if (user.getCreateTime() != null) {
            vo.setCreateTime(user.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        return vo;
    }
}
