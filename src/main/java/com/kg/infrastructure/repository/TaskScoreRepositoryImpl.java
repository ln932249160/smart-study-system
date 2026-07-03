package com.kg.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kg.domain.model.TaskScore;
import com.kg.domain.repository.TaskScoreRepository;
import com.kg.infrastructure.converter.TaskScoreConverter;
import com.kg.infrastructure.entity.TaskScoreEntity;
import com.kg.infrastructure.mapper.TaskScoreMapper;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务成绩明细仓储实现。
 */
@Repository
public class TaskScoreRepositoryImpl implements TaskScoreRepository {

    private final TaskScoreMapper taskScoreMapper;

    public TaskScoreRepositoryImpl(TaskScoreMapper taskScoreMapper) {
        this.taskScoreMapper = taskScoreMapper;
    }

    @Override
    public void batchSave(List<TaskScore> list) {
        if (list == null || list.isEmpty()) return;
        for (TaskScore s : list) {
            taskScoreMapper.insert(TaskScoreConverter.toEntity(s));
        }
    }

    @Override
    public List<TaskScore> findByTaskUserId(Long taskUserId) {
        LambdaQueryWrapper<TaskScoreEntity> q = new LambdaQueryWrapper<>();
        q.eq(TaskScoreEntity::getTaskUserId, taskUserId);
        List<TaskScoreEntity> entities = taskScoreMapper.selectList(q);
        if (entities == null || entities.isEmpty()) return Collections.emptyList();
        return entities.stream().map(TaskScoreConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteByTaskUserId(Long taskUserId) {
        LambdaQueryWrapper<TaskScoreEntity> q = new LambdaQueryWrapper<>();
        q.eq(TaskScoreEntity::getTaskUserId, taskUserId);
        taskScoreMapper.delete(q);
    }

    @Override
    public void deleteByTaskId(Long taskId) {
        // 关联 task_user 表：先查 task_user_id 列表再删除
        // 使用原生 SQL 更高效，但 MVP 阶段用子查询
        LambdaQueryWrapper<TaskScoreEntity> q = new LambdaQueryWrapper<>();
        q.inSql(TaskScoreEntity::getTaskUserId,
                "SELECT id FROM task_user WHERE task_id = " + taskId);
        taskScoreMapper.delete(q);
    }
}
