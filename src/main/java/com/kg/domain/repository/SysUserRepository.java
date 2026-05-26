package com.kg.domain.repository;

import com.kg.domain.model.SysUser;

import java.util.List;
import java.util.Optional;

/**
 * 系统用户仓储接口 —— 定义 sys_user 的数据访问契约。
 */
public interface SysUserRepository {

    /** 新增用户，插入后回填主键 ID */
    void save(SysUser user);

    /** 根据主键查询 */
    Optional<SysUser> findById(Long id);

    /** 根据账号查询 */
    Optional<SysUser> findByAccount(String account);

    /**
     * 根据账号模糊 + 角色列表分页查询
     */
    List<SysUser> pageByNameAndRoles(String name, List<String> roles, int offset, int limit);

    /** 统计分页总数 */
    long countByNameAndRoles(String name, List<String> roles);

    /** 按主键更新（只更新非 null 字段） */
    void update(SysUser user);

    /** 按主键物理删除 */
    void deleteById(Long id);

    /**
     * 查询指定班级下的所有学生
     */
    List<SysUser> findByClassId(Long classId);

    /**
     * 统计某班级下的学生人数
     */
    long countByClassId(Long classId);

    /**
     * 将指定班级下所有学生的 class_id 置为 NULL
     */
    void clearClassId(Long classId);

    /**
     * 批量更新学生的 class_id
     *
     * @param userIds 学生主键列表
     * @param classId 班级 ID
     */
    void batchUpdateClassId(List<Long> userIds, Long classId);

    /**
     * 查询指定角色的所有启用用户，用于定时任务批量分配
     */
    List<SysUser> listByRoles(List<String> roles);

    /**
     * 查询所有学生（role = student），用于班级学生复选框
     */
    List<SysUser> listStudentsByRole(String role);
}
