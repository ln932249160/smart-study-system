package com.kg.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kg.domain.model.TaskTemplate;
import com.kg.domain.repository.TaskTemplateRepository;
import com.kg.infrastructure.converter.TaskTemplateConverter;
import com.kg.infrastructure.entity.TaskTemplateEntity;
import com.kg.infrastructure.mapper.TaskTemplateMapper;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** 模板任务仓储实现 */
@Repository
public class TaskTemplateRepositoryImpl implements TaskTemplateRepository {

    private final TaskTemplateMapper mapper;

    public TaskTemplateRepositoryImpl(TaskTemplateMapper mapper) { this.mapper = mapper; }

    @Override
    public void save(TaskTemplate t) {
        TaskTemplateEntity e = TaskTemplateConverter.toEntity(t);
        mapper.insert(e);
        t.setId(e.getId());
    }

    @Override
    public void update(TaskTemplate t) {
        LambdaUpdateWrapper<TaskTemplateEntity> w = new LambdaUpdateWrapper<>();
        w.eq(TaskTemplateEntity::getId, t.getId());
        if (t.getTemplateName() != null) w.set(TaskTemplateEntity::getTemplateName, t.getTemplateName());
        if (t.getTaskType() != null) w.set(TaskTemplateEntity::getTaskType, t.getTaskType());
        if (t.getIsMandatory() != null) w.set(TaskTemplateEntity::getIsMandatory, t.getIsMandatory());
        if (t.getTaskDescription() != null) w.set(TaskTemplateEntity::getTaskDescription, t.getTaskDescription());
        if (t.getDefaultPriority() != null) w.set(TaskTemplateEntity::getDefaultPriority, t.getDefaultPriority());
        if (t.getUpdateBy() != null) w.set(TaskTemplateEntity::getUpdateBy, t.getUpdateBy());
        mapper.update(null, w);
    }

    @Override
    public void deleteById(Long id) { mapper.deleteById(id); }

    @Override
    public Optional<TaskTemplate> findById(Long id) {
        return Optional.ofNullable(TaskTemplateConverter.toDomain(mapper.selectById(id)));
    }

    @Override
    public List<TaskTemplate> page(int offset, int limit) {
        LambdaQueryWrapper<TaskTemplateEntity> q = new LambdaQueryWrapper<>();
        q.orderByDesc(TaskTemplateEntity::getId);
        q.last("LIMIT " + offset + "," + limit);
        List<TaskTemplateEntity> entities = mapper.selectList(q);
        if (entities == null || entities.isEmpty()) return Collections.emptyList();
        return entities.stream().map(TaskTemplateConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public long count() { return mapper.selectCount(null); }

    @Override
    public List<TaskTemplate> listAll() {
        LambdaQueryWrapper<TaskTemplateEntity> q = new LambdaQueryWrapper<>();
        q.orderByAsc(TaskTemplateEntity::getTemplateName);
        List<TaskTemplateEntity> entities = mapper.selectList(q);
        if (entities == null || entities.isEmpty()) return Collections.emptyList();
        return entities.stream().map(TaskTemplateConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsByTemplateName(String templateName) {
        LambdaQueryWrapper<TaskTemplateEntity> q = new LambdaQueryWrapper<>();
        q.eq(TaskTemplateEntity::getTemplateName, templateName);
        return mapper.selectCount(q) > 0;
    }

    @Override
    public boolean existsByTemplateNameExcludingId(String templateName, Long excludeId) {
        LambdaQueryWrapper<TaskTemplateEntity> q = new LambdaQueryWrapper<>();
        q.eq(TaskTemplateEntity::getTemplateName, templateName);
        q.ne(TaskTemplateEntity::getId, excludeId);
        return mapper.selectCount(q) > 0;
    }
}
