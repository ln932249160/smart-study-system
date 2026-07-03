package com.kg.domain.repository;

import com.kg.domain.model.TaskUser;

import java.util.List;

/**
 * 任务分配仓储接口 —— 定义 task_user 表的数据访问契约。
 */
public interface TaskUserRepository {

    /** 新增单条分配记录 */
    void save(TaskUser taskUser);

    /** 批量插入 */
    void batchSave(List<TaskUser> list);

    /** 按任务ID删除所有分配记录 */
    void deleteByTaskId(Long taskId);

    /** 按任务ID查询分配列表 */
    List<TaskUser> findByTaskId(Long taskId);

    /** 按任务ID + 用户ID 查询单条分配记录 */
    TaskUser findByTaskIdAndUserId(Long taskId, Long userId);

    /** 按主键更新 */
    void update(TaskUser taskUser);

    /** 查询当前用户的任务分配列表 */
    List<TaskUser> findByUserId(Long userId);
}
