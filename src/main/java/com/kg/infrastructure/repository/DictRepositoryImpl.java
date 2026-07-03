package com.kg.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kg.domain.model.DictItem;
import com.kg.domain.repository.DictRepository;
import com.kg.infrastructure.converter.DictConverter;
import com.kg.infrastructure.entity.DictEntity;
import com.kg.infrastructure.mapper.DictMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/** 字典仓储实现 */
@Repository
public class DictRepositoryImpl implements DictRepository {

    private final DictMapper mapper;

    public DictRepositoryImpl(DictMapper mapper) { this.mapper = mapper; }

    @Override
    public void save(DictItem item) {
        DictEntity e = DictConverter.toEntity(item);
        mapper.insert(e);
        item.setId(e.getId());
    }

    @Override
    public void update(DictItem item) {
        LambdaUpdateWrapper<DictEntity> w = new LambdaUpdateWrapper<>();
        w.eq(DictEntity::getId, item.getId());
        if (item.getDictCode() != null) w.set(DictEntity::getDictCode, item.getDictCode());
        if (item.getDictName() != null) w.set(DictEntity::getDictName, item.getDictName());
        if (item.getDictLabel() != null) w.set(DictEntity::getDictLabel, item.getDictLabel());
        if (item.getDictValue() != null) w.set(DictEntity::getDictValue, item.getDictValue());
        if (item.getDisplayColor() != null) w.set(DictEntity::getDisplayColor, item.getDisplayColor());
        if (item.getSortNo() != null) w.set(DictEntity::getSortNo, item.getSortNo());
        if (item.getStatus() != null) w.set(DictEntity::getStatus, item.getStatus());
        if (item.getUpdateBy() != null) w.set(DictEntity::getUpdateBy, item.getUpdateBy());
        mapper.update(null, w);
    }

    @Override
    public void deleteById(Long id) { mapper.deleteById(id); }

    @Override
    public void deleteByCode(String dictCode) {
        LambdaQueryWrapper<DictEntity> q = new LambdaQueryWrapper<>();
        q.eq(DictEntity::getDictCode, dictCode);
        mapper.delete(q);
    }

    @Override
    public Optional<DictItem> findById(Long id) {
        return Optional.ofNullable(DictConverter.toDomain(mapper.selectById(id)));
    }

    @Override
    public List<DictItem> findByCode(String dictCode) {
        LambdaQueryWrapper<DictEntity> q = new LambdaQueryWrapper<>();
        q.eq(DictEntity::getDictCode, dictCode);
        q.orderByAsc(DictEntity::getSortNo);
        List<DictEntity> entities = mapper.selectList(q);
        if (entities == null || entities.isEmpty()) return Collections.emptyList();
        return entities.stream().map(DictConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<DictItem> listDistinctCodes(String dictNameFilter, int offset, int limit) {
        // 查询所有匹配记录，Java 内存按 dict_code 分组去重
        LambdaQueryWrapper<DictEntity> q = new LambdaQueryWrapper<>();
        q.like(dictNameFilter != null && !dictNameFilter.isEmpty(), DictEntity::getDictName, dictNameFilter);
        q.orderByAsc(DictEntity::getDictCode);
        List<DictEntity> all = mapper.selectList(q);
        if (all == null || all.isEmpty()) return Collections.emptyList();

        // 去重：按 dict_code 分组取第一条
        Map<String, DictItem> map = new LinkedHashMap<>();
        for (DictEntity e : all) {
            map.computeIfAbsent(e.getDictCode(), k -> {
                DictItem d = new DictItem();
                d.setDictCode(e.getDictCode());
                d.setDictName(e.getDictName());
                return d;
            });
        }
        List<DictItem> result = new ArrayList<>(map.values());
        int to = Math.min(offset + limit, result.size());
        return offset < result.size() ? result.subList(offset, to) : Collections.emptyList();
    }

    @Override
    public long countDistinctCodes(String dictNameFilter) {
        LambdaQueryWrapper<DictEntity> q = new LambdaQueryWrapper<>();
        q.like(dictNameFilter != null && !dictNameFilter.isEmpty(), DictEntity::getDictName, dictNameFilter);
        q.orderByAsc(DictEntity::getDictCode);
        List<DictEntity> all = mapper.selectList(q);
        return all.stream().map(DictEntity::getDictCode).distinct().count();
    }
}
