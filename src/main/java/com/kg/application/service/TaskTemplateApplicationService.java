package com.kg.application.service;

import com.kg.context.UserContext;
import com.kg.domain.model.SysUser;
import com.kg.domain.model.TaskTemplate;
import com.kg.domain.repository.TaskTemplateRepository;
import com.kg.enums.RoleEnum;
import com.kg.exception.BusinessException;
import com.kg.interfaces.dto.TaskTemplateCreateRequest;
import com.kg.interfaces.dto.TaskTemplateUpdateRequest;
import com.kg.interfaces.dto.TaskTemplateVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 模板任务应用服务 — teacher + headmaster 可管理 */
@Service
public class TaskTemplateApplicationService {
    private static final Logger log = LoggerFactory.getLogger(TaskTemplateApplicationService.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TaskTemplateRepository repo;

    public TaskTemplateApplicationService(TaskTemplateRepository repo) { this.repo = repo; }

    public Map<String, Object> page(int pageNum, int pageSize) {
        requireNotStudent();
        int offset = (pageNum - 1) * pageSize;
        long total = repo.count();
        List<TaskTemplateVO> list = repo.page(offset, pageSize).stream().map(this::toVO).collect(Collectors.toList());
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("total", total); r.put("list", list);
        return r;
    }

    public TaskTemplateVO getById(Long id) {
        return toVO(repo.findById(id).orElseThrow(() -> new BusinessException("模板不存在")));
    }

    public void create(TaskTemplateCreateRequest req) {
        Long uid = requireNotStudent();
        // 模板名称唯一校验
        if (repo.existsByTemplateName(req.getTemplateName())) {
            throw new BusinessException("模板名称已存在，请重新输入");
        }
        TaskTemplate t = new TaskTemplate();
        t.setTemplateName(req.getTemplateName());
        t.setTaskType(req.getTaskType());
        t.setIsMandatory(req.getIsMandatory());
        t.setTaskDescription(req.getTaskDescription());
        t.setDefaultPriority(req.getDefaultPriority());
        t.setCreateBy(uid);
        t.setCreateTime(LocalDateTime.now());
        repo.save(t);
        log.info("创建模板: id={}, name={}", t.getId(), req.getTemplateName());
    }

    public void update(Long id, TaskTemplateUpdateRequest req) {
        Long uid = requireNotStudent();
        // 模板名称唯一校验（排除自身）
        if (repo.existsByTemplateNameExcludingId(req.getTemplateName(), id)) {
            throw new BusinessException("模板名称已存在，请重新输入");
        }
        TaskTemplate t = new TaskTemplate();
        t.setId(id);
        t.setTemplateName(req.getTemplateName());
        t.setTaskType(req.getTaskType());
        t.setIsMandatory(req.getIsMandatory());
        t.setTaskDescription(req.getTaskDescription());
        t.setDefaultPriority(req.getDefaultPriority());
        t.setUpdateBy(uid);
        repo.update(t);
        log.info("编辑模板: id={}", id);
    }

    public void delete(Long id) {
        requireNotStudent();
        repo.deleteById(id);
        log.info("删除模板: id={}", id);
    }

    public List<TaskTemplateVO> listOptions() {
        return repo.listAll().stream().map(t -> {
            TaskTemplateVO vo = new TaskTemplateVO();
            vo.setId(t.getId());
            vo.setTemplateName(t.getTemplateName());
            vo.setTaskType(t.getTaskType());
            return vo;
        }).collect(Collectors.toList());
    }

    /** teacher + headmaster 可管理，student 无权限 */
    private Long requireNotStudent() {
        SysUser u = UserContext.getUser();
        if (u == null) throw new BusinessException(401, "未登录");
        if (RoleEnum.isStudent(u.getRole())) throw new BusinessException(403, "学生无权限");
        return u.getId();
    }

    private TaskTemplateVO toVO(TaskTemplate t) {
        TaskTemplateVO vo = new TaskTemplateVO();
        vo.setId(t.getId());
        vo.setTemplateName(t.getTemplateName());
        vo.setTaskType(t.getTaskType());
        vo.setIsMandatory(t.getIsMandatory());
        vo.setTaskDescription(t.getTaskDescription());
        vo.setDefaultPriority(t.getDefaultPriority());
        if (t.getCreateTime() != null) vo.setCreateTime(t.getCreateTime().format(FMT));
        return vo;
    }
}
