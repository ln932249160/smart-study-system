package com.kg.domain.repository;

import com.kg.domain.model.TaskScore;

import java.util.List;

/**
 * 任务成绩明细仓储接口 —— 定义 task_score 表的数据访问契约。
 */
public interface TaskScoreRepository {

    /** 批量插入成绩明细 */
    void batchSave(List<TaskScore> list);

    /** 按任务分配ID查询成绩明细 */
    List<TaskScore> findByTaskUserId(Long taskUserId);

    /** 按任务分配ID删除 */
    void deleteByTaskUserId(Long taskUserId);

    /** 按任务ID删除（关联 task_user） */
    void deleteByTaskId(Long taskId);
}
