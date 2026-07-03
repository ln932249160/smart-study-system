package com.kg.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.infrastructure.entity.StudyPhaseClassEntity;
import org.apache.ibatis.annotations.Mapper;

/** 学习阶段班级关联 Mapper */
@Mapper
public interface StudyPhaseClassMapper extends BaseMapper<StudyPhaseClassEntity> {
}
