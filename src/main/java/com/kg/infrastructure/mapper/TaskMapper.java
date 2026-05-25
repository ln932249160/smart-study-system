package com.kg.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.infrastructure.entity.TaskEntity;
import org.apache.ibatis.annotations.Mapper;

/** 任务 Mapper */
@Mapper
public interface TaskMapper extends BaseMapper<TaskEntity> {
}
