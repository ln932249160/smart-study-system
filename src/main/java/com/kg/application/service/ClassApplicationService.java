package com.kg.application.service;

import com.kg.context.UserContext;
import com.kg.domain.model.ClassInfo;
import com.kg.domain.model.SysUser;
import com.kg.domain.repository.ClassInfoRepository;
import com.kg.domain.repository.SysUserRepository;
import com.kg.enums.RoleEnum;
import com.kg.exception.BusinessException;
import com.kg.interfaces.dto.ClassCreateRequest;
import com.kg.interfaces.dto.ClassUpdateRequest;
import com.kg.interfaces.dto.ClassVO;
import com.kg.interfaces.dto.StudentOptionVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 班级管理应用服务 —— teacher 全部操作，headmaster 仅看本班，student 无权限。
 */
@Service
public class ClassApplicationService {

    private static final Logger log = LoggerFactory.getLogger(ClassApplicationService.class);
    private static final int STATUS_ACTIVE = 1;

    private final ClassInfoRepository classInfoRepository;
    private final SysUserRepository sysUserRepository;

    public ClassApplicationService(ClassInfoRepository classInfoRepository,
                                   SysUserRepository sysUserRepository) {
        this.classInfoRepository = classInfoRepository;
        this.sysUserRepository = sysUserRepository;
    }

    // ======================== 分页查询 ========================

    /**
     * teacher → 全部班级 | headmaster → 仅本班 | student → 无权限
     */
    public Map<String, Object> page(int pageNum, int pageSize) {
        SysUser currentUser = requireCurrentUser();
        String role = currentUser.getRole();

        if (RoleEnum.isStudent(role)) {
            throw new BusinessException(403, "无权查看班级列表");
        }

        List<ClassInfo> classes;
        long total;

        if (RoleEnum.isHeadmaster(role)) {
            Long classId = currentUser.getClassId();
            if (classId == null) return emptyPageResult();
            ClassInfo c = classInfoRepository.findById(classId).orElse(null);
            classes = c != null ? Collections.singletonList(c) : Collections.emptyList();
            total = classes.size();
        } else {
            int offset = (pageNum - 1) * pageSize;
            total = classInfoRepository.count();
            classes = classInfoRepository.page(offset, pageSize);
        }

        List<ClassVO> list = classes.stream().map(c -> {
            ClassVO vo = new ClassVO();
            vo.setId(c.getId());
            vo.setClassName(c.getClassName());
            vo.setDescription(c.getDescription());
            vo.setStudentCount(sysUserRepository.countByClassId(c.getId()));
            if (c.getCreateTime() != null) {
                vo.setCreateTime(c.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
            return vo;
        }).collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", total);
        result.put("list", list);
        return result;
    }

    // ======================== 新增（仅 teacher） ========================

    @Transactional(rollbackFor = Exception.class)
    public void create(ClassCreateRequest request) {
        Long currentUserId = requireTeacher();

        ClassInfo classInfo = new ClassInfo();
        classInfo.setClassName(request.getClassName());
        classInfo.setDescription(request.getDescription());
        classInfo.setStatus(STATUS_ACTIVE);
        classInfo.setCreateBy(currentUserId);
        classInfo.setCreateTime(LocalDateTime.now());
        classInfoRepository.save(classInfo);

        Long newClassId = classInfo.getId();
        log.info("新增班级: id={}, className={}", newClassId, request.getClassName());

        List<Long> studentIds = request.getStudentIds();
        if (studentIds != null && !studentIds.isEmpty()) {
            sysUserRepository.batchUpdateClassId(studentIds, null);
            sysUserRepository.batchUpdateClassId(studentIds, newClassId);
            log.info("分配 {} 名学生到班级 {}", studentIds.size(), newClassId);
        }
    }

    // ======================== 编辑（仅 teacher） ========================

    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, ClassUpdateRequest request) {
        Long currentUserId = requireTeacher();

        classInfoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("班级不存在"));

        ClassInfo classInfo = new ClassInfo();
        classInfo.setId(id);
        classInfo.setClassName(request.getClassName());
        classInfo.setDescription(request.getDescription());
        classInfo.setUpdateBy(currentUserId);
        classInfoRepository.update(classInfo);

        sysUserRepository.clearClassId(id);

        List<Long> studentIds = request.getStudentIds();
        if (studentIds != null && !studentIds.isEmpty()) {
            sysUserRepository.batchUpdateClassId(studentIds, null);
            sysUserRepository.batchUpdateClassId(studentIds, id);
        }
        log.info("编辑班级: id={}", id);
    }

    // ======================== 删除（仅 teacher） ========================

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        requireTeacher();
        classInfoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("班级不存在"));
        sysUserRepository.clearClassId(id);
        classInfoRepository.deleteById(id);
        log.info("删除班级: id={}", id);
    }

    // ======================== 学生选项 ========================

    /**
     * teacher → 全部学生 | headmaster → 本班学生
     */
    public List<StudentOptionVO> listStudentOptions() {
        SysUser currentUser = requireCurrentUser();
        List<SysUser> students;

        if (RoleEnum.isHeadmaster(currentUser.getRole())) {
            Long classId = currentUser.getClassId();
            if (classId == null) return Collections.emptyList();
            students = sysUserRepository.findByClassId(classId).stream()
                    .filter(u -> RoleEnum.isStudent(u.getRole()))
                    .collect(Collectors.toList());
        } else {
            students = sysUserRepository.listStudentsByRole(RoleEnum.STUDENT.getCode());
        }

        if (students.isEmpty()) return Collections.emptyList();
        return students.stream()
                .map(s -> StudentOptionVO.of(s.getId(), s.getName(), s.getClassId()))
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

    private Map<String, Object> emptyPageResult() {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("total", 0);
        r.put("list", Collections.emptyList());
        return r;
    }
}
