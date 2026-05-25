package com.kg.infrastructure.converter;

import com.kg.domain.model.Task;
import com.kg.infrastructure.entity.TaskEntity;

/**
 * 防腐层转换器 —— domain.Task ↔ infra.TaskEntity（15 列）。
 */
public class TaskConverter {

    public static Task toDomain(TaskEntity e) {
        if (e == null) return null;
        Task d = new Task();
        d.setId(e.getId());
        d.setTaskName(e.getTaskName());
        d.setTaskType(e.getTaskType());
        d.setIsTemplate(e.getIsTemplate());
        d.setTemplateTaskId(e.getTemplateTaskId());
        d.setRoundNo(e.getRoundNo());
        d.setIsMandatory(e.getIsMandatory());
        d.setTaskDescription(e.getTaskDescription());
        d.setTaskStartTime(e.getTaskStartTime());
        d.setTaskEndTime(e.getTaskEndTime());
        d.setPriority(e.getPriority());
        d.setClassId(e.getClassId());
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
        e.setIsTemplate(d.getIsTemplate());
        e.setTemplateTaskId(d.getTemplateTaskId());
        e.setRoundNo(d.getRoundNo());
        e.setIsMandatory(d.getIsMandatory());
        e.setTaskDescription(d.getTaskDescription());
        e.setTaskStartTime(d.getTaskStartTime());
        e.setTaskEndTime(d.getTaskEndTime());
        e.setPriority(d.getPriority());
        e.setClassId(d.getClassId());
        e.setCreateBy(d.getCreateBy());
        e.setCreateTime(d.getCreateTime());
        e.setUpdateBy(d.getUpdateBy());
        e.setUpdateTime(d.getUpdateTime());
        return e;
    }
}
