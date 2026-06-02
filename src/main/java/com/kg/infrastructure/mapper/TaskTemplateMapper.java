package com.kg.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.infrastructure.entity.TaskTemplateEntity;
import org.apache.ibatis.annotations.Mapper;

/** 模板任务 Mapper */
@Mapper
public interface TaskTemplateMapper extends BaseMapper<TaskTemplateEntity> {
}
