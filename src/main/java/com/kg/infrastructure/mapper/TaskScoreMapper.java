package com.kg.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.infrastructure.entity.TaskScoreEntity;
import org.apache.ibatis.annotations.Mapper;

/** 任务成绩明细 Mapper */
@Mapper
public interface TaskScoreMapper extends BaseMapper<TaskScoreEntity> {
}
