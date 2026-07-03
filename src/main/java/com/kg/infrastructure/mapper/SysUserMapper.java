package com.kg.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.infrastructure.entity.SysUserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户 Mapper，操作 sys_user 表。
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUserEntity> {
}
