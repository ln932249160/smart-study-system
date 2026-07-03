package com.kg.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kg.domain.model.TaskUser;
import com.kg.domain.repository.TaskUserRepository;
import com.kg.infrastructure.converter.TaskUserConverter;
import com.kg.infrastructure.entity.TaskUserEntity;
import com.kg.infrastructure.mapper.TaskUserMapper;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务分配仓储实现。
 */
@Repository
public class TaskUserRepositoryImpl implements TaskUserRepository {

    private final TaskUserMapper taskUserMapper;

    public TaskUserRepositoryImpl(TaskUserMapper taskUserMapper) {
        this.taskUserMapper = taskUserMapper;
    }

    @Override
    public void save(TaskUser taskUser) {
        taskUserMapper.insert(TaskUserConverter.toEntity(taskUser));
    }

    @Override
    public void batchSave(List<TaskUser> list) {
        if (list == null || list.isEmpty()) return;
        for (TaskUser tu : list) {
            taskUserMapper.insert(TaskUserConverter.toEntity(tu));
        }
    }

    @Override
    public void deleteByTaskId(Long taskId) {
        LambdaQueryWrapper<TaskUserEntity> q = new LambdaQueryWrapper<>();
        q.eq(TaskUserEntity::getTaskId, taskId);
        taskUserMapper.delete(q);
    }

    @Override
    public List<TaskUser> findByTaskId(Long taskId) {
        LambdaQueryWrapper<TaskUserEntity> q = new LambdaQueryWrapper<>();
        q.eq(TaskUserEntity::getTaskId, taskId);
        List<TaskUserEntity> entities = taskUserMapper.selectList(q);
        if (entities == null || entities.isEmpty()) return Collections.emptyList();
        return entities.stream().map(TaskUserConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public TaskUser findByTaskIdAndUserId(Long taskId, Long userId) {
        LambdaQueryWrapper<TaskUserEntity> q = new LambdaQueryWrapper<>();
        q.eq(TaskUserEntity::getTaskId, taskId);
        q.eq(TaskUserEntity::getUserId, userId);
        return TaskUserConverter.toDomain(taskUserMapper.selectOne(q));
    }

    @Override
    public void update(TaskUser taskUser) {
        LambdaUpdateWrapper<TaskUserEntity> w = new LambdaUpdateWrapper<>();
        w.eq(TaskUserEntity::getId, taskUser.getId());
        if (taskUser.getStatus() != null) w.set(TaskUserEntity::getStatus, taskUser.getStatus());
        if (taskUser.getFinishTime() != null) w.set(TaskUserEntity::getFinishTime, taskUser.getFinishTime());
        if (taskUser.getDurationMinutes() != null) w.set(TaskUserEntity::getDurationMinutes, taskUser.getDurationMinutes());
        if (taskUser.getRemark() != null) w.set(TaskUserEntity::getRemark, taskUser.getRemark());
        if (taskUser.getTotalScore() != null) w.set(TaskUserEntity::getTotalScore, taskUser.getTotalScore());
        if (taskUser.getSubmitTime() != null) w.set(TaskUserEntity::getSubmitTime, taskUser.getSubmitTime());
        if (taskUser.getUpdateBy() != null) w.set(TaskUserEntity::getUpdateBy, taskUser.getUpdateBy());
        taskUserMapper.update(null, w);
    }

    @Override
    public List<TaskUser> findByUserId(Long userId) {
        LambdaQueryWrapper<TaskUserEntity> q = new LambdaQueryWrapper<>();
        q.eq(TaskUserEntity::getUserId, userId);
        q.orderByDesc(TaskUserEntity::getId);
        List<TaskUserEntity> entities = taskUserMapper.selectList(q);
        if (entities == null || entities.isEmpty()) return Collections.emptyList();
        return entities.stream().map(TaskUserConverter::toDomain).collect(Collectors.toList());
    }
}
