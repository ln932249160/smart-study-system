package com.kg.application.service;

import com.kg.context.UserContext;
import com.kg.domain.model.ClassInfo;
import com.kg.domain.model.SysUser;
import com.kg.domain.repository.ClassInfoRepository;
import com.kg.domain.repository.SysUserRepository;
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
 * 班级管理应用服务 —— 编排班级 CRUD 及学生分配业务流程。
 * <p>
 * 单班制：一个学生只能属于一个班级。新增/编辑班级时通过 class_id 关联学生。
 * </p>
 */
@Service
public class ClassApplicationService {

    private static final Logger log = LoggerFactory.getLogger(ClassApplicationService.class);

    /** 学生角色标识 */
    private static final String ROLE_STUDENT = "student";

    /** 启用状态 */
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
     * 分页查询班级，附带各班学生人数。
     */
    public Map<String, Object> page(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        long total = classInfoRepository.count();
        List<ClassInfo> classes = classInfoRepository.page(offset, pageSize);

        List<ClassVO> list = classes.stream().map(c -> {
            ClassVO vo = new ClassVO();
            vo.setId(c.getId());
            vo.setClassName(c.getClassName());
            vo.setDescription(c.getDescription());
            // 统计该班级下的学生人数
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

    // ======================== 新增班级 ========================

    /**
     * 新增班级并将选中学生分配到该班级。
     * <p>
     * 1. 插入 class_info 记录（create_by = 当前用户）<br>
     * 2. 批量更新选中学生的 class_id 为新增班级的 ID
     * </p>
     */
    @Transactional(rollbackFor = Exception.class)
    public void create(ClassCreateRequest request) {
        SysUser currentUser = UserContext.getUser();
        Long currentUserId = currentUser != null ? currentUser.getId() : null;

        // 1. 插入班级
        ClassInfo classInfo = new ClassInfo();
        classInfo.setClassName(request.getClassName());
        classInfo.setDescription(request.getDescription());
        classInfo.setStatus(STATUS_ACTIVE);
        classInfo.setCreateBy(currentUserId);
        classInfo.setCreateTime(LocalDateTime.now());
        classInfoRepository.save(classInfo);

        Long newClassId = classInfo.getId();
        log.info("新增班级成功: id={}, className={}, createdBy={}", newClassId, request.getClassName(), currentUserId);

        // 2. 将选中学生分配到新班级
        List<Long> studentIds = request.getStudentIds();
        if (studentIds != null && !studentIds.isEmpty()) {
            // 先解除这些学生的原班级关联，再绑定新班级
            sysUserRepository.batchUpdateClassId(studentIds, null);
            sysUserRepository.batchUpdateClassId(studentIds, newClassId);
            log.info("分配 {} 名学生到班级 {}", studentIds.size(), newClassId);
        }
    }

    // ======================== 编辑班级 ========================

    /**
     * 编辑班级信息并重新分配学生。
     * <p>
     * 1. 更新 class_info<br>
     * 2. 将原属该班级的所有学生 class_id 置为 NULL<br>
     * 3. 将新选中的学生 class_id 更新为当前班级 ID
     * </p>
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, ClassUpdateRequest request) {
        SysUser currentUser = UserContext.getUser();
        Long currentUserId = currentUser != null ? currentUser.getId() : null;

        // 校验班级存在
        classInfoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("班级不存在"));

        // 1. 更新班级信息
        ClassInfo classInfo = new ClassInfo();
        classInfo.setId(id);
        classInfo.setClassName(request.getClassName());
        classInfo.setDescription(request.getDescription());
        classInfo.setUpdateBy(currentUserId);
        classInfoRepository.update(classInfo);
        log.info("编辑班级信息: id={}", id);

        // 2. 解除原有学生关联
        sysUserRepository.clearClassId(id);

        // 3. 绑定新选中的学生
        List<Long> studentIds = request.getStudentIds();
        if (studentIds != null && !studentIds.isEmpty()) {
            // 先解除这些学生与其原班级的关联，再绑定当前班级
            sysUserRepository.batchUpdateClassId(studentIds, null);
            sysUserRepository.batchUpdateClassId(studentIds, id);
        }
        log.info("重新分配学生到班级 {}: {} 人", id, studentIds != null ? studentIds.size() : 0);
    }

    // ======================== 删除班级 ========================

    /**
     * 删除班级，并将该班级下所有学生的 class_id 置为 NULL。
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 校验班级存在
        classInfoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("班级不存在"));

        // 1. 解除学生关联
        sysUserRepository.clearClassId(id);

        // 2. 删除班级记录
        classInfoRepository.deleteById(id);
        log.info("删除班级成功: id={}", id);
    }

    // ======================== 学生选项列表 ========================

    /**
     * 获取所有启用状态的学生列表，用于班级新增/编辑时的复选框。
     * <p>
     * 返回字段：id、name（姓名）、classId（当前所属班级）。
     * 前端可根据 classId 判断学生已归属哪个班级。
     * </p>
     */
    public List<StudentOptionVO> listStudentOptions() {
        List<SysUser> students = sysUserRepository.listStudentsByRole(ROLE_STUDENT);
        if (students.isEmpty()) {
            return Collections.emptyList();
        }
        return students.stream()
                .map(s -> StudentOptionVO.of(s.getId(), s.getName(), s.getClassId()))
                .collect(Collectors.toList());
    }
}
