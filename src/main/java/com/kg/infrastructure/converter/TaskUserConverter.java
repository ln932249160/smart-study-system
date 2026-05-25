package com.kg.infrastructure.converter;

import com.kg.domain.model.TaskUser;
import com.kg.infrastructure.entity.TaskUserEntity;

/**
 * 防腐层转换器 —— domain.TaskUser ↔ infra.TaskUserEntity
 */
public class TaskUserConverter {

    public static TaskUser toDomain(TaskUserEntity e) {
        if (e == null) return null;
        TaskUser d = new TaskUser();
        d.setId(e.getId());
        d.setTaskId(e.getTaskId());
        d.setUserId(e.getUserId());
        d.setStatus(e.getStatus());
        d.setStartTime(e.getStartTime());
        d.setFinishTime(e.getFinishTime());
        d.setDurationMinutes(e.getDurationMinutes());
        d.setRemark(e.getRemark());
        d.setTotalScore(e.getTotalScore());
        d.setSubmitTime(e.getSubmitTime());
        d.setCreateBy(e.getCreateBy());
        d.setCreateTime(e.getCreateTime());
        d.setUpdateBy(e.getUpdateBy());
        d.setUpdateTime(e.getUpdateTime());
        return d;
    }

    public static TaskUserEntity toEntity(TaskUser d) {
        if (d == null) return null;
        TaskUserEntity e = new TaskUserEntity();
        e.setId(d.getId());
        e.setTaskId(d.getTaskId());
        e.setUserId(d.getUserId());
        e.setStatus(d.getStatus());
        e.setStartTime(d.getStartTime());
        e.setFinishTime(d.getFinishTime());
        e.setDurationMinutes(d.getDurationMinutes());
        e.setRemark(d.getRemark());
        e.setTotalScore(d.getTotalScore());
        e.setSubmitTime(d.getSubmitTime());
        e.setCreateBy(d.getCreateBy());
        e.setCreateTime(d.getCreateTime());
        e.setUpdateBy(d.getUpdateBy());
        e.setUpdateTime(d.getUpdateTime());
        return e;
    }
}
