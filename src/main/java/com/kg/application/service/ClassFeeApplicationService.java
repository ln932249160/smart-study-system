package com.kg.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kg.context.UserContext;
import com.kg.domain.model.ClassInfo;
import com.kg.domain.model.SysUser;
import com.kg.domain.repository.ClassInfoRepository;
import com.kg.enums.RoleEnum;
import com.kg.exception.BusinessException;
import com.kg.infrastructure.entity.ClassFeeEntity;
import com.kg.infrastructure.mapper.ClassFeeMapper;
import com.kg.interfaces.dto.ClassFeeRequest;
import com.kg.interfaces.dto.ClassFeeStatVO;
import com.kg.interfaces.dto.PageVO;
import com.kg.interfaces.dto.ClassFeeVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/** 班费管理应用服务 */
@Service
public class ClassFeeApplicationService {
    private static final Logger log = LoggerFactory.getLogger(ClassFeeApplicationService.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ClassFeeMapper mapper;
    private final ClassInfoRepository classInfoRepo;

    public ClassFeeApplicationService(ClassFeeMapper mapper, ClassInfoRepository classInfoRepo) {
        this.mapper = mapper;
        this.classInfoRepo = classInfoRepo;
    }

    /** 分页查询：老师看全部，班长/学生看自己班。支持按班级ID和物料模糊筛选 */
    public PageVO<ClassFeeVO> page(int pageNum, int pageSize, Long classId, String material) {
        SysUser user = requireLogin();
        Long filterClassId = resolveClassId(user);
        // 如果传了classId且角色不允许跨班，以传入的为准但需校验
        if (classId != null) filterClassId = classId;
        int offset = (pageNum - 1) * pageSize;

        LambdaQueryWrapper<ClassFeeEntity> q = new LambdaQueryWrapper<>();
        q.eq(ClassFeeEntity::getStatus, 1);
        if (filterClassId != null) q.eq(ClassFeeEntity::getClassId, filterClassId);
        if (material != null && !material.trim().isEmpty())
            q.like(ClassFeeEntity::getMaterial, material.trim());
        q.orderByDesc(ClassFeeEntity::getFeeDate);
        long total = mapper.selectCount(q);
        q.last("LIMIT " + offset + "," + pageSize);
        List<ClassFeeEntity> entities = mapper.selectList(q);

        List<ClassFeeVO> list = entities == null ? Collections.emptyList()
                : entities.stream().map(e -> {
            ClassFeeVO vo = new ClassFeeVO();
            vo.setId(e.getId()); vo.setClassId(e.getClassId()); vo.setMaterial(e.getMaterial());
            vo.setAmount(e.getAmount()); vo.setStatus(e.getStatus());
            vo.setFeeDate(e.getFeeDate() != null ? e.getFeeDate().format(DATE_FMT) : null);
            vo.setCreateTime(e.getCreateTime() != null ? e.getCreateTime().format(FMT) : null);
            classInfoRepo.findById(e.getClassId()).ifPresent(c -> vo.setClassName(c.getClassName()));
            return vo;
        }).collect(Collectors.toList());

        return PageVO.of(total, list);
    }

    /** 详情 */
    public ClassFeeVO getById(Long id) {
        ClassFeeEntity e = mapper.selectById(id);
        if (e == null) throw new BusinessException("记录不存在");
        ClassFeeVO vo = new ClassFeeVO();
        vo.setId(e.getId()); vo.setClassId(e.getClassId()); vo.setMaterial(e.getMaterial());
        vo.setAmount(e.getAmount()); vo.setStatus(e.getStatus());
        vo.setFeeDate(e.getFeeDate() != null ? e.getFeeDate().format(DATE_FMT) : null);
        vo.setCreateTime(e.getCreateTime() != null ? e.getCreateTime().format(FMT) : null);
        classInfoRepo.findById(e.getClassId()).ifPresent(c -> vo.setClassName(c.getClassName()));
        return vo;
    }

    /** 新增 */
    public void create(ClassFeeRequest req) {
        SysUser user = requireLogin();
        requireNotStudent(user);
        ClassFeeEntity e = new ClassFeeEntity();
        e.setClassId(req.getClassId()); e.setMaterial(req.getMaterial());
        e.setAmount(req.getAmount()); e.setFeeDate(LocalDate.parse(req.getFeeDate(), DATE_FMT));
        e.setStatus(1); e.setCreateBy(user.getId()); e.setCreateTime(LocalDateTime.now());
        mapper.insert(e);
    }

    /** 编辑 */
    public void update(Long id, ClassFeeRequest req) {
        SysUser user = requireLogin();
        requireNotStudent(user);
        ClassFeeEntity e = mapper.selectById(id);
        if (e == null) throw new BusinessException("记录不存在");
        e.setClassId(req.getClassId()); e.setMaterial(req.getMaterial());
        e.setAmount(req.getAmount()); e.setFeeDate(LocalDate.parse(req.getFeeDate(), DATE_FMT));
        e.setUpdateBy(user.getId());
        mapper.updateById(e);
    }

    /** 删除 */
    public void delete(Long id) {
        requireNotStudent(requireLogin());
        mapper.deleteById(id);
    }

    /** 统计：按班级汇总 */
    public List<ClassFeeStatVO> statistics() {
        SysUser user = requireLogin();
        List<ClassFeeStatVO> result = new ArrayList<>();
        // 查所有有效记录
        LambdaQueryWrapper<ClassFeeEntity> q = new LambdaQueryWrapper<>();
        q.eq(ClassFeeEntity::getStatus, 1);
        if (RoleEnum.isHeadmaster(user.getRole()) || RoleEnum.isStudent(user.getRole())) {
            Long classId = user.getClassId();
            if (classId == null) return result;
            q.eq(ClassFeeEntity::getClassId, classId);
        }
        List<ClassFeeEntity> all = mapper.selectList(q);
        Map<Long, BigDecimal> sumMap = new HashMap<>();
        for (ClassFeeEntity e : all) {
            sumMap.merge(e.getClassId(), e.getAmount(), BigDecimal::add);
        }
        for (Map.Entry<Long, BigDecimal> entry : sumMap.entrySet()) {
            ClassFeeStatVO vo = new ClassFeeStatVO();
            vo.setClassId(entry.getKey());
            vo.setTotalAmount(entry.getValue());
            classInfoRepo.findById(entry.getKey()).ifPresent(c -> vo.setClassName(c.getClassName()));
            result.add(vo);
        }
        return result;
    }

    /** 班级费用明细 */
    public List<ClassFeeVO> detail(Long classId) {
        LambdaQueryWrapper<ClassFeeEntity> q = new LambdaQueryWrapper<>();
        q.eq(ClassFeeEntity::getClassId, classId);
        q.eq(ClassFeeEntity::getStatus, 1);
        q.orderByDesc(ClassFeeEntity::getFeeDate);
        List<ClassFeeEntity> entities = mapper.selectList(q);
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(e -> {
            ClassFeeVO vo = new ClassFeeVO();
            vo.setId(e.getId()); vo.setClassId(e.getClassId()); vo.setMaterial(e.getMaterial());
            vo.setAmount(e.getAmount()); vo.setStatus(e.getStatus());
            vo.setFeeDate(e.getFeeDate() != null ? e.getFeeDate().format(DATE_FMT) : null);
            vo.setCreateTime(e.getCreateTime() != null ? e.getCreateTime().format(FMT) : null);
            classInfoRepo.findById(e.getClassId()).ifPresent(c -> vo.setClassName(c.getClassName()));
            return vo;
        }).collect(Collectors.toList());
    }

    private SysUser requireLogin() {
        SysUser u = UserContext.getUser();
        if (u == null) throw new BusinessException(401, "未登录");
        return u;
    }
    private void requireNotStudent(SysUser u) {
        if (RoleEnum.isStudent(u.getRole())) throw new BusinessException(403, "学生无权限");
    }
    private Long resolveClassId(SysUser user) {
        if (RoleEnum.isTeacher(user.getRole())) return null;
        return user.getClassId();
    }
}
