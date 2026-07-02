package com.kg.infrastructure.converter;

import com.kg.domain.model.TaskPlan;
import com.kg.infrastructure.entity.TaskPlanEntity;

/** TaskPlan ↔ TaskPlanEntity */
public class TaskPlanConverter {
    public static TaskPlan toDomain(TaskPlanEntity e) {
        if (e == null) return null;
        TaskPlan d = new TaskPlan();
        d.setId(e.getId()); d.setPlanName(e.getPlanName()); d.setTaskType(e.getTaskType());
        d.setTemplateId(e.getTemplateId()); d.setIsMandatory(e.getIsMandatory());
        d.setTaskDescription(e.getTaskDescription()); d.setPriority(e.getPriority());
        d.setRepeatType(e.getRepeatType()); d.setRepeatConfig(e.getRepeatConfig());
        d.setStartDate(e.getStartDate()); d.setEndDate(e.getEndDate());
        d.setTargetType(e.getTargetType()); d.setTargetIds(e.getTargetIds());
        d.setGeneratedCount(e.getGeneratedCount()); d.setStatus(e.getStatus());
        d.setCreateBy(e.getCreateBy()); d.setCreateTime(e.getCreateTime());
        d.setUpdateBy(e.getUpdateBy()); d.setUpdateTime(e.getUpdateTime());
        return d;
    }
    public static TaskPlanEntity toEntity(TaskPlan d) {
        if (d == null) return null;
        TaskPlanEntity e = new TaskPlanEntity();
        e.setId(d.getId()); e.setPlanName(d.getPlanName()); e.setTaskType(d.getTaskType());
        e.setTemplateId(d.getTemplateId()); e.setIsMandatory(d.getIsMandatory());
        e.setTaskDescription(d.getTaskDescription()); e.setPriority(d.getPriority());
        e.setRepeatType(d.getRepeatType()); e.setRepeatConfig(d.getRepeatConfig());
        e.setStartDate(d.getStartDate()); e.setEndDate(d.getEndDate());
        e.setTargetType(d.getTargetType()); e.setTargetIds(d.getTargetIds());
        e.setGeneratedCount(d.getGeneratedCount()); e.setStatus(d.getStatus());
        e.setCreateBy(d.getCreateBy()); e.setCreateTime(d.getCreateTime());
        e.setUpdateBy(d.getUpdateBy()); e.setUpdateTime(d.getUpdateTime());
        return e;
    }
}
