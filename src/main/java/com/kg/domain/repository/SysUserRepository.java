package com.kg.domain.repository;

import com.kg.domain.model.SysUser;

import java.util.Optional;

/**
 * 系统用户仓储接口 —— 定义 sys_user 的数据访问契约。
 */
public interface SysUserRepository {

    /**
     * 根据账号查询系统用户
     *
     * @param account 账号
     * @return 系统用户（可选）
     */
    Optional<SysUser> findByAccount(String account);
}
