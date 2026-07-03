package com.kg.domain.repository;

import com.kg.domain.model.DictItem;

import java.util.List;
import java.util.Optional;

/** sys_dict 仓储接口 */
public interface DictRepository {
    void save(DictItem item);
    void update(DictItem item);
    void deleteById(Long id);
    void deleteByCode(String dictCode);
    Optional<DictItem> findById(Long id);
    List<DictItem> findByCode(String dictCode);
    /** 分组查询：去重 dict_code，支持 dict_name 模糊筛选 */
    List<DictItem> listDistinctCodes(String dictNameFilter, int offset, int limit);
    long countDistinctCodes(String dictNameFilter);
}
