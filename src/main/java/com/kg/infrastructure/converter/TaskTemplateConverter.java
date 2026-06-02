package com.kg.infrastructure.converter;

import com.kg.domain.model.TaskTemplate;
import com.kg.infrastructure.entity.TaskTemplateEntity;

/** domain.TaskTemplate ↔ infra.TaskTemplateEntity */
public class TaskTemplateConverter {
    public static TaskTemplate toDomain(TaskTemplateEntity e) {
        if (e == null) return null;
        TaskTemplate d = new TaskTemplate();
        d.setId(e.getId());
        d.setTemplateName(e.getTemplateName());
        d.setTaskType(e.getTaskType());
        d.setIsMandatory(e.getIsMandatory());
        d.setTaskDescription(e.getTaskDescription());
        d.setDefaultPriority(e.getDefaultPriority());
        d.setCreateBy(e.getCreateBy());
        d.setCreateTime(e.getCreateTime());
        d.setUpdateBy(e.getUpdateBy());
        d.setUpdateTime(e.getUpdateTime());
        return d;
    }

    public static TaskTemplateEntity toEntity(TaskTemplate d) {
        if (d == null) return null;
        TaskTemplateEntity e = new TaskTemplateEntity();
        e.setId(d.getId());
        e.setTemplateName(d.getTemplateName());
        e.setTaskType(d.getTaskType());
        e.setIsMandatory(d.getIsMandatory());
        e.setTaskDescription(d.getTaskDescription());
        e.setDefaultPriority(d.getDefaultPriority());
        e.setCreateBy(d.getCreateBy());
        e.setCreateTime(d.getCreateTime());
        e.setUpdateBy(d.getUpdateBy());
        e.setUpdateTime(d.getUpdateTime());
        return e;
    }
}
