package com.kg.domain.repository;

import com.kg.domain.model.ClassInfo;

import java.util.List;
import java.util.Optional;

/**
 * 班级仓储接口 —— 定义 class_info 的数据访问契约。
 */
public interface ClassInfoRepository {

    /** 新增班级，插入后回填主键 */
    void save(ClassInfo classInfo);

    /** 根据主键更新（只更新非 null 字段） */
    void update(ClassInfo classInfo);

    /** 根据主键物理删除 */
    void deleteById(Long id);

    /** 按主键查询 */
    Optional<ClassInfo> findById(Long id);

    /**
     * 分页查询全部班级
     *
     * @param offset 偏移量
     * @param limit  每页条数
     */
    List<ClassInfo> page(int offset, int limit);

    /** 班级总数 */
    long count();

    /** 查询所有启用状态的班级，用于下拉列表 */
    List<ClassInfo> listActiveClasses();
}
