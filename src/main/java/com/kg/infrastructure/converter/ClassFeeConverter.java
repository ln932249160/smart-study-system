package com.kg.infrastructure.converter;

import com.kg.domain.model.ClassFee;
import com.kg.infrastructure.entity.ClassFeeEntity;

/** ClassFee ↔ ClassFeeEntity */
public class ClassFeeConverter {
    public static ClassFee toDomain(ClassFeeEntity e) {
        if (e == null) return null;
        ClassFee d = new ClassFee();
        d.setId(e.getId()); d.setClassId(e.getClassId()); d.setMaterial(e.getMaterial());
        d.setAmount(e.getAmount()); d.setFeeDate(e.getFeeDate()); d.setStatus(e.getStatus());
        d.setCreateBy(e.getCreateBy()); d.setCreateTime(e.getCreateTime());
        d.setUpdateBy(e.getUpdateBy()); d.setUpdateTime(e.getUpdateTime());
        return d;
    }
    public static ClassFeeEntity toEntity(ClassFee d) {
        if (d == null) return null;
        ClassFeeEntity e = new ClassFeeEntity();
        e.setId(d.getId()); e.setClassId(d.getClassId()); e.setMaterial(d.getMaterial());
        e.setAmount(d.getAmount()); e.setFeeDate(d.getFeeDate()); e.setStatus(d.getStatus());
        e.setCreateBy(d.getCreateBy()); e.setCreateTime(d.getCreateTime());
        e.setUpdateBy(d.getUpdateBy()); e.setUpdateTime(d.getUpdateTime());
        return e;
    }
}
