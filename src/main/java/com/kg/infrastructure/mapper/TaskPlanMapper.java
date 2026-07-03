package com.kg.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.infrastructure.entity.TaskPlanEntity;
import org.apache.ibatis.annotations.Mapper;

/** 任务计划 Mapper */
@Mapper
public interface TaskPlanMapper extends BaseMapper<TaskPlanEntity> {
}
