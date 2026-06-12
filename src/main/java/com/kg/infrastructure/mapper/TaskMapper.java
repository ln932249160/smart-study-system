package com.kg.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.infrastructure.entity.TaskEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/** 任务 Mapper */
@Mapper
public interface TaskMapper extends BaseMapper<TaskEntity> {

    /** 查询指定班级未结束的班级任务（用于新学生补发） */
    @Select("SELECT * FROM task WHERE target_type = 1  AND FIND_IN_SET(#{classId}, target_ids) AND task_end_time > NOW()")
    List<TaskEntity> findUnfinishedClassTasks(@Param("classId") Long classId);
}
