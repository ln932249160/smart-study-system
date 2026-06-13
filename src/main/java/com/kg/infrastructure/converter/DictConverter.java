package com.kg.infrastructure.converter;

import com.kg.domain.model.DictItem;
import com.kg.infrastructure.entity.DictEntity;

/** DictItem ↔ DictEntity */
public class DictConverter {
    public static DictItem toDomain(DictEntity e) {
        if (e == null) return null;
        DictItem d = new DictItem();
        d.setId(e.getId()); d.setDictCode(e.getDictCode()); d.setDictName(e.getDictName());
        d.setDictLabel(e.getDictLabel());
        d.setDictValue(e.getDictValue()); d.setDisplayColor(e.getDisplayColor());
        d.setSortNo(e.getSortNo()); d.setStatus(e.getStatus());
        d.setCreateBy(e.getCreateBy()); d.setCreateTime(e.getCreateTime());
        d.setUpdateBy(e.getUpdateBy()); d.setUpdateTime(e.getUpdateTime());
        return d;
    }
    public static DictEntity toEntity(DictItem d) {
        if (d == null) return null;
        DictEntity e = new DictEntity();
        e.setId(d.getId()); e.setDictCode(d.getDictCode()); e.setDictName(d.getDictName());
        e.setDictLabel(d.getDictLabel());
        e.setDictValue(d.getDictValue()); e.setDisplayColor(d.getDisplayColor());
        e.setSortNo(d.getSortNo()); e.setStatus(d.getStatus());
        e.setCreateBy(d.getCreateBy()); e.setCreateTime(d.getCreateTime());
        e.setUpdateBy(d.getUpdateBy()); e.setUpdateTime(d.getUpdateTime());
        return e;
    }
}
