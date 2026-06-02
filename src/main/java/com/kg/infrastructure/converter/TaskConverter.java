package com.kg.infrastructure.converter;

import com.kg.domain.model.Task;
import com.kg.infrastructure.entity.TaskEntity;

/**
 * 防腐层转换器 —— domain.Task ↔ infra.TaskEntity（14 列）。
 */
public class TaskConverter {

    public static Task toDomain(TaskEntity e) {
        if (e == null) return null;
        Task d = new Task();
        d.setId(e.getId());
        d.setTaskName(e.getTaskName());
        d.setTaskType(e.getTaskType());
        d.setTemplateId(e.getTemplateId());
        d.setRoundNo(e.getRoundNo());
        d.setIsMandatory(e.getIsMandatory());
        d.setTaskDescription(e.getTaskDescription());
        d.setTaskStartTime(e.getTaskStartTime());
        d.setTaskEndTime(e.getTaskEndTime());
        d.setPriority(e.getPriority());
        d.setClassId(e.getClassId());
        d.setTargetType(e.getTargetType());
        d.setTargetIds(e.getTargetIds());
        d.setCreateBy(e.getCreateBy());
        d.setCreateTime(e.getCreateTime());
        d.setUpdateBy(e.getUpdateBy());
        d.setUpdateTime(e.getUpdateTime());
        return d;
    }

    public static TaskEntity toEntity(Task d) {
        if (d == null) return null;
        TaskEntity e = new TaskEntity();
        e.setId(d.getId());
        e.setTaskName(d.getTaskName());
        e.setTaskType(d.getTaskType());
        e.setTemplateId(d.getTemplateId());
        e.setRoundNo(d.getRoundNo());
        e.setIsMandatory(d.getIsMandatory());
        e.setTaskDescription(d.getTaskDescription());
        e.setTaskStartTime(d.getTaskStartTime());
        e.setTaskEndTime(d.getTaskEndTime());
        e.setPriority(d.getPriority());
        e.setClassId(d.getClassId());
        e.setTargetType(d.getTargetType());
        e.setTargetIds(d.getTargetIds());
        e.setCreateBy(d.getCreateBy());
        e.setCreateTime(d.getCreateTime());
        e.setUpdateBy(d.getUpdateBy());
        e.setUpdateTime(d.getUpdateTime());
        return e;
    }
}
