package com.kg.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.infrastructure.entity.DictEntity;
import org.apache.ibatis.annotations.Mapper;

/** 字典 Mapper */
@Mapper
public interface DictMapper extends BaseMapper<DictEntity> {
}
