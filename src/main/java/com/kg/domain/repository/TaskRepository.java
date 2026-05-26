package com.kg.domain.repository;

import com.kg.domain.model.Task;

import java.util.List;
import java.util.Optional;

/**
 * 任务仓储接口 —— 定义 task 表的数据访问契约。
 */
public interface TaskRepository {

    void save(Task task);
    void update(Task task);
    void deleteById(Long id);
    Optional<Task> findById(Long id);

    /** 分页查询全部非模板任务 */
    List<Task> page(int offset, int limit);

    /** 全部非模板任务总数 */
    long count();

    /** 查询所有模板任务 */
    List<Task> listTemplates();

    /** 按班级ID分页（headmaster 视角） */
    List<Task> pageByClassId(Long classId, int offset, int limit);

    /** 按班级ID统计任务数 */
    long countByClassId(Long classId);

    /** 按任务ID列表分页（student 视角） */
    List<Task> pageByIds(List<Long> ids, int offset, int limit);

    /** 按任务ID列表统计 */
    long countByIds(List<Long> ids);
}
