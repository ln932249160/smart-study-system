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

    /** 分页查询全部任务 */
    List<Task> page(int offset, int limit);

    /** 全部任务总数 */
    long count();

    /** 按班级ID分页（headmaster 视角） */
    List<Task> pageByClassId(Long classId, int offset, int limit);

    /** 按班级ID统计任务数 */
    long countByClassId(Long classId);

    /** 班长视角分页：(target_type=1 AND FIND_IN_SET(classId,target_ids)) OR create_by=userId */
    List<Task> pageByHeadmaster(Long classId, Long userId, int offset, int limit);

    /** 班长视角计数 */
    long countByHeadmaster(Long classId, Long userId);

    /** 按任务ID列表分页（student 视角） */
    List<Task> pageByIds(List<Long> ids, int offset, int limit);

    /** 按任务ID列表统计 */
    long countByIds(List<Long> ids);

    /** 查询指定模板下的最大轮次 */
    int maxRoundNoByTemplateId(Long templateId);

    /** 多条件分页 */
    List<Task> pageWithFilters(String taskType, String taskName, Integer isMandatory,
                               String startBegin, String startEnd, String endBegin, String endEnd,
                               int offset, int limit);
    /** 多条件统计 */
    long countWithFilters(String taskType, String taskName, Integer isMandatory,
                          String startBegin, String startEnd, String endBegin, String endEnd);
}
