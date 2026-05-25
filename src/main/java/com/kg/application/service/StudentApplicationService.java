package com.kg.application.service;

import com.kg.context.UserContext;
import com.kg.domain.model.ClassInfo;
import com.kg.domain.model.SysUser;
import com.kg.domain.repository.ClassInfoRepository;
import com.kg.domain.repository.SysUserRepository;
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
 * 学生管理应用服务 —— 编排 student / headmaster 用户的 CRUD 业务流程。
 */
@Service
public class StudentApplicationService {

    private static final Logger log = LoggerFactory.getLogger(StudentApplicationService.class);

    /** 管理的角色范围 */
    private static final List<String> MANAGED_ROLES = Arrays.asList("student", "headmaster");

    /** 默认密码（明文） */
    private static final String DEFAULT_PASSWORD = "123456";

    /** 正常状态 */
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
     * 分页查询 student/headmaster 用户。
     *
     * @param name     姓名（模糊匹配 name 字段），可选
     * @param pageNum  页码（从 1 开始）
     * @param pageSize 每页条数
     * @return { "total": 总数, "list": [StudentVO] }
     */
    public Map<String, Object> page(String name, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        long total = sysUserRepository.countByNameAndRoles(name, MANAGED_ROLES);
        List<SysUser> users = sysUserRepository.pageByNameAndRoles(name, MANAGED_ROLES, offset, pageSize);

        List<StudentVO> list = users.stream().map(this::toVO).collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", total);
        result.put("list", list);
        return result;
    }

    // ======================== 新增 ========================

    /**
     * 新增 student/headmaster 用户。
     *
     * @param request 新增请求
     */
    public void create(StudentCreateRequest request) {
        // 获取当前登录用户
        SysUser currentUser = UserContext.getUser();
        Long currentUserId = currentUser != null ? currentUser.getId() : null;

        // account：有则用填写的，没填默认手机号
        String account = (request.getAccount() != null && !request.getAccount().trim().isEmpty())
                ? request.getAccount().trim()
                : request.getPhone();
        String name = request.getName();

        // 检查 account 是否已存在（手机号唯一）
        if (sysUserRepository.findByAccount(account).isPresent()) {
            throw new BusinessException("该手机号已被使用：" + account);
        }

        SysUser sysUser = new SysUser();
        sysUser.setAccount(account);
        sysUser.setName(name);
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
        log.info("新增学生成功: id={}, account={}, name={}, role={}, createdBy={}",
                sysUser.getId(), account, name, request.getRole(), currentUserId);
    }

    // ======================== 编辑 ========================

    /**
     * 编辑 student/headmaster 用户。
     *
     * @param id      用户主键
     * @param request 编辑请求
     */
    public void update(Long id, StudentUpdateRequest request) {
        SysUser currentUser = UserContext.getUser();
        Long currentUserId = currentUser != null ? currentUser.getId() : null;

        SysUser sysUser = new SysUser();
        sysUser.setId(id);
        // 姓名 → name 字段
        if (request.getName() != null) {
            sysUser.setName(request.getName().trim());
        }
        // 账号 → account 字段
        if (request.getAccount() != null) {
            sysUser.setAccount(request.getAccount().trim());
        }
        // 手机号 → phone 字段
        if (request.getPhone() != null) {
            sysUser.setPhone(request.getPhone().trim());
        }
        if (request.getRole() != null) {
            sysUser.setRole(request.getRole());
        }
        if (request.getGender() != null) {
            sysUser.setGender(request.getGender());
        }
        if (request.getEmail() != null) {
            sysUser.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            sysUser.setPhone(request.getPhone());
        }
        if (request.getDescription() != null) {
            sysUser.setDescription(request.getDescription());
        }
        if (request.getClassId() != null) {
            sysUser.setClassId(request.getClassId());
        }
        if (request.getStatus() != null) {
            sysUser.setStatus(request.getStatus());
        }
        sysUser.setUpdateBy(currentUserId);

        sysUserRepository.update(sysUser);
        log.info("编辑学生成功: id={}, updatedBy={}", id, currentUserId);
    }

    // ======================== 删除 ========================

    /**
     * 物理删除用户。
     *
     * @param id 用户主键
     */
    public void delete(Long id) {
        sysUserRepository.deleteById(id);
        log.info("删除学生成功: id={}", id);
    }

    // ======================== 班级下拉 ========================

    /**
     * 班级下拉选项查询。
     *
     * @return 启用的班级列表
     */
    public List<ClassOptionVO> listClassOptions() {
        List<ClassInfo> classes = classInfoRepository.listActiveClasses();
        return classes.stream()
                .map(c -> ClassOptionVO.of(c.getId(), c.getClassName()))
                .collect(Collectors.toList());
    }

    // ======================== 私有转换 ========================

    /**
     * SysUser → StudentVO
     */
    private StudentVO toVO(SysUser user) {
        StudentVO vo = new StudentVO();
        vo.setId(user.getId());
        vo.setName(user.getName());                   // name 字段作为姓名展示
        vo.setRole(user.getRole());
        vo.setPhone(user.getPhone());
        vo.setClassId(user.getClassId());
        vo.setGender(user.getGender());
        vo.setEmail(user.getEmail());
        vo.setStatus(user.getStatus());

        LocalDateTime createTime = user.getCreateTime();
        if (createTime != null) {
            vo.setCreateTime(createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        return vo;
    }
}
