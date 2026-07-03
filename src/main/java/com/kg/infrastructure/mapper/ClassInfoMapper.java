package com.kg.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.infrastructure.entity.ClassInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 班级 Mapper，操作 class_info 表。
 */
@Mapper
public interface ClassInfoMapper extends BaseMapper<ClassInfoEntity> {
}
