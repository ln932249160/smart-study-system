package com.kg.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kg.domain.model.Task;
import com.kg.domain.repository.TaskRepository;
import com.kg.infrastructure.converter.TaskConverter;
import com.kg.infrastructure.entity.TaskEntity;
import com.kg.infrastructure.mapper.TaskMapper;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 任务仓储实现。
 */
@Repository
public class TaskRepositoryImpl implements TaskRepository {

    private final TaskMapper taskMapper;

    public TaskRepositoryImpl(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    @Override
    public void save(Task task) {
        TaskEntity e = TaskConverter.toEntity(task);
        taskMapper.insert(e);
        task.setId(e.getId());
    }

    @Override
    public void update(Task task) {
        LambdaUpdateWrapper<TaskEntity> w = new LambdaUpdateWrapper<>();
        w.eq(TaskEntity::getId, task.getId());
        if (task.getTaskName() != null) w.set(TaskEntity::getTaskName, task.getTaskName());
        if (task.getTaskType() != null) w.set(TaskEntity::getTaskType, task.getTaskType());
        if (task.getIsTemplate() != null) w.set(TaskEntity::getIsTemplate, task.getIsTemplate());
        if (task.getTemplateTaskId() != null) w.set(TaskEntity::getTemplateTaskId, task.getTemplateTaskId());
        if (task.getRoundNo() != null) w.set(TaskEntity::getRoundNo, task.getRoundNo());
        if (task.getIsMandatory() != null) w.set(TaskEntity::getIsMandatory, task.getIsMandatory());
        if (task.getTaskDescription() != null) w.set(TaskEntity::getTaskDescription, task.getTaskDescription());
        if (task.getTaskStartTime() != null) w.set(TaskEntity::getTaskStartTime, task.getTaskStartTime());
        if (task.getTaskEndTime() != null) w.set(TaskEntity::getTaskEndTime, task.getTaskEndTime());
        if (task.getPriority() != null) w.set(TaskEntity::getPriority, task.getPriority());
        if (task.getClassId() != null) w.set(TaskEntity::getClassId, task.getClassId());
        if (task.getUpdateBy() != null) w.set(TaskEntity::getUpdateBy, task.getUpdateBy());
        taskMapper.update(null, w);
    }

    @Override
    public void deleteById(Long id) { taskMapper.deleteById(id); }

    @Override
    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(TaskConverter.toDomain(taskMapper.selectById(id)));
    }

    @Override
    public List<Task> page(int offset, int limit) {
        return queryPage(null, null, offset, limit);
    }

    @Override
    public long count() {
        return queryCount(null, null);
    }

    @Override
    public List<Task> listTemplates() {
        LambdaQueryWrapper<TaskEntity> q = new LambdaQueryWrapper<>();
        q.eq(TaskEntity::getIsTemplate, 1);
        q.orderByAsc(TaskEntity::getTaskName);
        List<TaskEntity> entities = taskMapper.selectList(q);
        if (entities == null || entities.isEmpty()) return Collections.emptyList();
        return entities.stream().map(TaskConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Task> pageByClassId(Long classId, int offset, int limit) {
        return queryPage(classId, null, offset, limit);
    }

    @Override
    public long countByClassId(Long classId) {
        return queryCount(classId, null);
    }

    @Override
    public List<Task> pageByIds(List<Long> ids, int offset, int limit) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return queryPage(null, ids, offset, limit);
    }

    @Override
    public long countByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        return queryCount(null, ids);
    }

    private List<Task> queryPage(Long classId, List<Long> ids, int offset, int limit) {
        LambdaQueryWrapper<TaskEntity> q = buildCommonQuery(classId, ids);
        q.orderByDesc(TaskEntity::getId);
        q.last("LIMIT " + offset + "," + limit);
        List<TaskEntity> entities = taskMapper.selectList(q);
        if (entities == null || entities.isEmpty()) return Collections.emptyList();
        return entities.stream().map(TaskConverter::toDomain).collect(Collectors.toList());
    }

    private long queryCount(Long classId, List<Long> ids) {
        return taskMapper.selectCount(buildCommonQuery(classId, ids));
    }

    private LambdaQueryWrapper<TaskEntity> buildCommonQuery(Long classId, List<Long> ids) {
        LambdaQueryWrapper<TaskEntity> q = new LambdaQueryWrapper<>();
        q.eq(TaskEntity::getIsTemplate, 0);
        if (classId != null) q.eq(TaskEntity::getClassId, classId);
        if (ids != null && !ids.isEmpty()) q.in(TaskEntity::getId, ids);
        return q;
    }
}
