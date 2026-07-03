package com.kg.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.infrastructure.entity.TaskUserEntity;
import org.apache.ibatis.annotations.Mapper;

/** 任务分配 Mapper */
@Mapper
public interface TaskUserMapper extends BaseMapper<TaskUserEntity> {
}
