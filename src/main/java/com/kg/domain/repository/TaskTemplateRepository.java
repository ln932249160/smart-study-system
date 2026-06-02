package com.kg.domain.repository;

import com.kg.domain.model.TaskTemplate;

import java.util.List;
import java.util.Optional;

/** 模板任务仓储接口 */
public interface TaskTemplateRepository {

    void save(TaskTemplate t);
    void update(TaskTemplate t);
    void deleteById(Long id);
    Optional<TaskTemplate> findById(Long id);
    List<TaskTemplate> page(int offset, int limit);
    long count();
    /** 全部模板，供下拉选择 */
    List<TaskTemplate> listAll();

    /** 模板名称是否已存在 */
    boolean existsByTemplateName(String templateName);

    /** 模板名称是否已存在（排除自身） */
    boolean existsByTemplateNameExcludingId(String templateName, Long excludeId);
}
