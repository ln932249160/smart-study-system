package com.kg.infrastructure.converter;

import com.kg.domain.model.TaskScore;
import com.kg.infrastructure.entity.TaskScoreEntity;

/**
 * 防腐层转换器 —— domain.TaskScore ↔ infra.TaskScoreEntity
 */
public class TaskScoreConverter {

    public static TaskScore toDomain(TaskScoreEntity e) {
        if (e == null) return null;
        TaskScore d = new TaskScore();
        d.setId(e.getId());
        d.setTaskUserId(e.getTaskUserId());
        d.setModuleName(e.getModuleName());
        d.setScore(e.getScore());
        d.setCreateBy(e.getCreateBy());
        d.setCreateTime(e.getCreateTime());
        d.setUpdateBy(e.getUpdateBy());
        d.setUpdateTime(e.getUpdateTime());
        return d;
    }

    public static TaskScoreEntity toEntity(TaskScore d) {
        if (d == null) return null;
        TaskScoreEntity e = new TaskScoreEntity();
        e.setId(d.getId());
        e.setTaskUserId(d.getTaskUserId());
        e.setModuleName(d.getModuleName());
        e.setScore(d.getScore());
        e.setCreateBy(d.getCreateBy());
        e.setCreateTime(d.getCreateTime());
        e.setUpdateBy(d.getUpdateBy());
        e.setUpdateTime(d.getUpdateTime());
        return e;
    }
}
