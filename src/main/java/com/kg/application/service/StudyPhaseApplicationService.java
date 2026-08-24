package com.kg.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kg.context.UserContext;
import com.kg.domain.model.SysUser;
import com.kg.domain.repository.ClassInfoRepository;
import com.kg.enums.RoleEnum;
import com.kg.exception.BusinessException;
import com.kg.infrastructure.entity.StudyPhaseClassEntity;
import com.kg.infrastructure.entity.StudyPhaseEntity;
import com.kg.infrastructure.mapper.StudyPhaseClassMapper;
import com.kg.infrastructure.mapper.StudyPhaseMapper;
import com.kg.interfaces.dto.StudyPhaseRequest;
import com.kg.interfaces.dto.StudyPhaseVO;
import com.kg.interfaces.dto.PageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/** 学习阶段应用服务 —— 老师增删改，班长/学生只读本班 */
@Service
public class StudyPhaseApplicationService {
    private static final Logger log = LoggerFactory.getLogger(StudyPhaseApplicationService.class);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final StudyPhaseMapper phaseMapper;
    private final StudyPhaseClassMapper phaseClassMapper;
    private final ClassInfoRepository classInfoRepo;

    public StudyPhaseApplicationService(StudyPhaseMapper phaseMapper,
                                         StudyPhaseClassMapper phaseClassMapper,
                                         ClassInfoRepository classInfoRepo) {
        this.phaseMapper = phaseMapper;
        this.phaseClassMapper = phaseClassMapper;
        this.classInfoRepo = classInfoRepo;
    }

    // ======================== 新增 ========================

    @Transactional(rollbackFor = Exception.class)
    public void add(StudyPhaseRequest req) {
        Long uid = requireTeacher();
        LocalDate start = LocalDate.parse(req.getStartDate(), DATE_FMT);
        LocalDate end = LocalDate.parse(req.getEndDate(), DATE_FMT);
        if (end.isBefore(start)) throw new BusinessException("结束日期不能早于开始日期");

        // 同一天最多两个阶段校验
        validateDailyLimit(start, end, req.getClassIds(), null);

        StudyPhaseEntity e = new StudyPhaseEntity();
        e.setPhaseTitle(req.getPhaseTitle()); e.setPhaseContent(req.getPhaseContent());
        e.setStartDate(start); e.setEndDate(end); e.setDisplayColor(req.getDisplayColor());
        e.setStatus(1); e.setCreateBy(uid); e.setCreateTime(LocalDateTime.now());
        phaseMapper.insert(e);

        for (Long cid : req.getClassIds()) {
            StudyPhaseClassEntity pc = new StudyPhaseClassEntity();
            pc.setPhaseId(e.getId()); pc.setClassId(cid);
            phaseClassMapper.insert(pc);
        }
        log.info("新增学习阶段: id={}, title={}", e.getId(), req.getPhaseTitle());
    }

    // ======================== 编辑 ========================

    @Transactional(rollbackFor = Exception.class)
    public void update(StudyPhaseRequest req) {
        Long uid = requireTeacher();
        if (req.getId() == null) throw new BusinessException("id不能为空");
        StudyPhaseEntity e = phaseMapper.selectById(req.getId());
        if (e == null) throw new BusinessException("学习阶段不存在");

        LocalDate start = LocalDate.parse(req.getStartDate(), DATE_FMT);
        LocalDate end = LocalDate.parse(req.getEndDate(), DATE_FMT);
        if (end.isBefore(start)) throw new BusinessException("结束日期不能早于开始日期");

        validateDailyLimit(start, end, req.getClassIds(), req.getId());

        e.setPhaseTitle(req.getPhaseTitle()); e.setPhaseContent(req.getPhaseContent());
        e.setStartDate(start); e.setEndDate(end); e.setDisplayColor(req.getDisplayColor());
        e.setUpdateBy(uid);
        phaseMapper.updateById(e);

        // 重建关联
        LambdaQueryWrapper<StudyPhaseClassEntity> q = new LambdaQueryWrapper<>();
        q.eq(StudyPhaseClassEntity::getPhaseId, req.getId());
        phaseClassMapper.delete(q);
        for (Long cid : req.getClassIds()) {
            StudyPhaseClassEntity pc = new StudyPhaseClassEntity();
            pc.setPhaseId(req.getId()); pc.setClassId(cid);
            phaseClassMapper.insert(pc);
        }
        log.info("编辑学习阶段: id={}", req.getId());
    }

    // ======================== 删除（逻辑删除） ========================

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        requireTeacher();
        StudyPhaseEntity e = phaseMapper.selectById(id);
        if (e == null) throw new BusinessException("学习阶段不存在");
        e.setStatus(0);
        phaseMapper.updateById(e);
        log.info("删除学习阶段: id={}", id);
    }

    // ======================== 详情 ========================

    public StudyPhaseVO detail(Long id) {
        StudyPhaseEntity e = phaseMapper.selectById(id);
        if (e == null) throw new BusinessException("学习阶段不存在");
        return toVO(e);
    }

    // ======================== 分页列表 ========================

    public PageVO<StudyPhaseVO> page(String phaseTitle, Long classId, String startDate, String endDate,
                                     Integer status, int pageNum, int pageSize) {
        SysUser user = requireLogin();
        // 权限：班长/学生只能查自己班级
        if (!RoleEnum.isTeacher(user.getRole())) {
            classId = user.getClassId();
            if (classId == null) return emptyPage();
        }
        int offset = (pageNum - 1) * pageSize;

        // 通过关联表查符合条件的 phase_id
        Set<Long> phaseIds = null;
        if (classId != null) {
            LambdaQueryWrapper<StudyPhaseClassEntity> cq = new LambdaQueryWrapper<>();
            cq.eq(StudyPhaseClassEntity::getClassId, classId);
            List<StudyPhaseClassEntity> cls = phaseClassMapper.selectList(cq);
            phaseIds = cls.stream().map(StudyPhaseClassEntity::getPhaseId).collect(Collectors.toSet());
            if (phaseIds.isEmpty()) return emptyPage();
        }

        LambdaQueryWrapper<StudyPhaseEntity> q = new LambdaQueryWrapper<>();
        q.eq(status != null, StudyPhaseEntity::getStatus, status);
        q.like(phaseTitle != null && !phaseTitle.isEmpty(), StudyPhaseEntity::getPhaseTitle, phaseTitle);
        if (startDate != null && !startDate.isEmpty())
            q.ge(StudyPhaseEntity::getStartDate, LocalDate.parse(startDate, DATE_FMT));
        if (endDate != null && !endDate.isEmpty())
            q.le(StudyPhaseEntity::getEndDate, LocalDate.parse(endDate, DATE_FMT));
        if (phaseIds != null) q.in(StudyPhaseEntity::getId, phaseIds);
        q.orderByDesc(StudyPhaseEntity::getStartDate);

        long total = phaseMapper.selectCount(q);
        q.last("LIMIT " + offset + "," + pageSize);
        List<StudyPhaseEntity> entities = phaseMapper.selectList(q);

        List<StudyPhaseVO> list = entities.stream().map(this::toVO).collect(Collectors.toList());
        return PageVO.of(total, list);
    }

    // ======================== 日历查询 ========================

    public List<StudyPhaseVO> calendar(String startDate, String endDate, Long reqClassId) {
        SysUser user = requireLogin();
        // 权限校验
        if (RoleEnum.isHeadmaster(user.getRole()) || RoleEnum.isStudent(user.getRole())) {
            reqClassId = user.getClassId();
            if (reqClassId == null) return Collections.emptyList();
        }
        LocalDate start = LocalDate.parse(startDate, DATE_FMT);
        LocalDate end = LocalDate.parse(endDate, DATE_FMT);

        // 查关联
        LambdaQueryWrapper<StudyPhaseClassEntity> cq = new LambdaQueryWrapper<>();
        cq.eq(StudyPhaseClassEntity::getClassId, reqClassId);
        List<Long> phaseIds = phaseClassMapper.selectList(cq).stream()
                .map(StudyPhaseClassEntity::getPhaseId).collect(Collectors.toList());
        if (phaseIds.isEmpty()) return Collections.emptyList();

        LambdaQueryWrapper<StudyPhaseEntity> q = new LambdaQueryWrapper<>();
        q.in(StudyPhaseEntity::getId, phaseIds);
        q.eq(StudyPhaseEntity::getStatus, 1);
        q.le(StudyPhaseEntity::getStartDate, end);
        q.ge(StudyPhaseEntity::getEndDate, start);
        q.orderByAsc(StudyPhaseEntity::getStartDate);
        return phaseMapper.selectList(q).stream().map(this::toVO).collect(Collectors.toList());
    }

    // ======================== 校验：同一天最多两个阶段 ========================

    private void validateDailyLimit(LocalDate start, LocalDate end, List<Long> classIds, Long excludeId) {
        for (Long cid : classIds) {
            // 查该班级所有有效阶段
            LambdaQueryWrapper<StudyPhaseClassEntity> cq = new LambdaQueryWrapper<>();
            cq.eq(StudyPhaseClassEntity::getClassId, cid);
            List<Long> pids = phaseClassMapper.selectList(cq).stream()
                    .map(StudyPhaseClassEntity::getPhaseId)
                    .filter(pid -> excludeId == null || !pid.equals(excludeId))
                    .collect(Collectors.toList());
            if (pids.isEmpty()) continue;

            LambdaQueryWrapper<StudyPhaseEntity> pq = new LambdaQueryWrapper<>();
            pq.in(StudyPhaseEntity::getId, pids);
            pq.eq(StudyPhaseEntity::getStatus, 1);
            List<StudyPhaseEntity> existing = phaseMapper.selectList(pq);

            LocalDate d = start;
            while (!d.isAfter(end)) {
                final LocalDate checkDay = d;
                int count = 0;
                for (StudyPhaseEntity ex : existing) {
                    if (!checkDay.isBefore(ex.getStartDate()) && !checkDay.isAfter(ex.getEndDate())) {
                        count++;
                    }
                }
                if (count >= 2) {
                    String className = classInfoRepo.findById(cid).map(c -> c.getClassName()).orElse("班级" + cid);
                    throw new BusinessException(className + " 在 " + checkDay.format(DATE_FMT) + " 已存在 2 个学习阶段，不能继续添加");
                }
                d = d.plusDays(1);
            }
        }
    }

    // ======================== 工具 ========================

    private StudyPhaseVO toVO(StudyPhaseEntity e) {
        StudyPhaseVO vo = new StudyPhaseVO();
        vo.setId(e.getId()); vo.setPhaseTitle(e.getPhaseTitle());
        vo.setPhaseContent(e.getPhaseContent()); vo.setDisplayColor(e.getDisplayColor());
        vo.setStatus(e.getStatus());
        vo.setStartDate(e.getStartDate() != null ? e.getStartDate().format(DATE_FMT) : null);
        vo.setEndDate(e.getEndDate() != null ? e.getEndDate().format(DATE_FMT) : null);
        vo.setCreateTime(e.getCreateTime() != null ? e.getCreateTime().format(DT_FMT) : null);

        // 查关联班级
        LambdaQueryWrapper<StudyPhaseClassEntity> cq = new LambdaQueryWrapper<>();
        cq.eq(StudyPhaseClassEntity::getPhaseId, e.getId());
        List<StudyPhaseClassEntity> cls = phaseClassMapper.selectList(cq);
        vo.setClassIds(cls.stream().map(StudyPhaseClassEntity::getClassId).collect(Collectors.toList()));
        List<String> names = new ArrayList<>();
        for (StudyPhaseClassEntity pc : cls) {
            classInfoRepo.findById(pc.getClassId()).ifPresent(c -> names.add(c.getClassName()));
        }
        vo.setClassNames(names);
        return vo;
    }

    private SysUser requireLogin() {
        SysUser u = UserContext.getUser();
        if (u == null) throw new BusinessException(401, "未登录");
        return u;
    }

    private Long requireTeacher() {
        SysUser u = requireLogin();
        if (!RoleEnum.isTeacher(u.getRole())) throw new BusinessException(403, "仅老师可操作");
        return u.getId();
    }

    private PageVO<StudyPhaseVO> emptyPage() {
        return PageVO.empty();
    }
}
