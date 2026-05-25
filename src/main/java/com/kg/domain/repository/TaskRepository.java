package com.kg.domain.repository;

import com.kg.domain.model.Task;

import java.util.List;
import java.util.Optional;

/**
 * 任务仓储接口 —— 定义 task 表的数据访问契约。
 */
public interface TaskRepository {

    /** 新增任务，回填主键 */
    void save(Task task);

    /** 按主键更新（只更新非 null 字段） */
    void update(Task task);

    /** 按主键物理删除 */
    void deleteById(Long id);

    /** 按主键查询 */
    Optional<Task> findById(Long id);

    /** 分页查询（不含模板任务） */
    List<Task> page(int offset, int limit);

    /** 非模板任务总数 */
    long count();

    /** 查询所有模板任务（is_template=1），用于下拉选择 */
    List<Task> listTemplates();
}
