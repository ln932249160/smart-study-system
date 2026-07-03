package com.kg.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kg.domain.model.ClassInfo;
import com.kg.domain.repository.ClassInfoRepository;
import com.kg.infrastructure.converter.ClassInfoConverter;
import com.kg.infrastructure.entity.ClassInfoEntity;
import com.kg.infrastructure.mapper.ClassInfoMapper;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 班级仓储实现 —— 使用 MyBatis-Plus 操作 MySQL。
 */
@Repository
public class ClassInfoRepositoryImpl implements ClassInfoRepository {

    private static final int STATUS_ACTIVE = 1;

    private final ClassInfoMapper classInfoMapper;

    public ClassInfoRepositoryImpl(ClassInfoMapper classInfoMapper) {
        this.classInfoMapper = classInfoMapper;
    }

    @Override
    public void save(ClassInfo classInfo) {
        ClassInfoEntity entity = ClassInfoConverter.toEntity(classInfo);
        classInfoMapper.insert(entity);
        classInfo.setId(entity.getId());
    }

    @Override
    public void update(ClassInfo classInfo) {
        LambdaUpdateWrapper<ClassInfoEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ClassInfoEntity::getId, classInfo.getId());
        if (classInfo.getClassName() != null) {
            wrapper.set(ClassInfoEntity::getClassName, classInfo.getClassName());
        }
        if (classInfo.getDescription() != null) {
            wrapper.set(ClassInfoEntity::getDescription, classInfo.getDescription());
        }
        if (classInfo.getStatus() != null) {
            wrapper.set(ClassInfoEntity::getStatus, classInfo.getStatus());
        }
        if (classInfo.getUpdateBy() != null) {
            wrapper.set(ClassInfoEntity::getUpdateBy, classInfo.getUpdateBy());
        }
        classInfoMapper.update(null, wrapper);
    }

    @Override
    public void deleteById(Long id) {
        classInfoMapper.deleteById(id);
    }

    @Override
    public Optional<ClassInfo> findById(Long id) {
        ClassInfoEntity entity = classInfoMapper.selectById(id);
        return Optional.ofNullable(ClassInfoConverter.toDomain(entity));
    }

    @Override
    public List<ClassInfo> page(int offset, int limit) {
        LambdaQueryWrapper<ClassInfoEntity> query = new LambdaQueryWrapper<>();
        query.orderByDesc(ClassInfoEntity::getId);
        query.last("LIMIT " + offset + "," + limit);
        List<ClassInfoEntity> entities = classInfoMapper.selectList(query);
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream().map(ClassInfoConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public long count() {
        return classInfoMapper.selectCount(null);
    }

    @Override
    public List<ClassInfo> listActiveClasses() {
        LambdaQueryWrapper<ClassInfoEntity> query = new LambdaQueryWrapper<>();
        query.eq(ClassInfoEntity::getStatus, STATUS_ACTIVE);
        query.orderByAsc(ClassInfoEntity::getClassName);
        List<ClassInfoEntity> entities = classInfoMapper.selectList(query);
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream().map(ClassInfoConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsByClassName(String className) {
        LambdaQueryWrapper<ClassInfoEntity> q = new LambdaQueryWrapper<>();
        q.eq(ClassInfoEntity::getClassName, className);
        return classInfoMapper.selectCount(q) > 0;
    }

    @Override
    public boolean existsByClassNameExcludingId(String className, Long excludeId) {
        LambdaQueryWrapper<ClassInfoEntity> q = new LambdaQueryWrapper<>();
        q.eq(ClassInfoEntity::getClassName, className);
        q.ne(ClassInfoEntity::getId, excludeId);
        return classInfoMapper.selectCount(q) > 0;
    }

    @Override
    public Optional<ClassInfo> findByClassName(String className) {
        LambdaQueryWrapper<ClassInfoEntity> q = new LambdaQueryWrapper<>();
        q.eq(ClassInfoEntity::getClassName, className);
        return Optional.ofNullable(ClassInfoConverter.toDomain(classInfoMapper.selectOne(q)));
    }

    @Override
    public List<Long> findIdsByClassNameLike(String className) {
        LambdaQueryWrapper<ClassInfoEntity> q = new LambdaQueryWrapper<>();
        q.like(className != null, ClassInfoEntity::getClassName, className);
        q.select(ClassInfoEntity::getId);
        List<ClassInfoEntity> entities = classInfoMapper.selectList(q);
        if (entities == null || entities.isEmpty()) return Collections.emptyList();
        return entities.stream().map(ClassInfoEntity::getId).collect(Collectors.toList());
    }
}
