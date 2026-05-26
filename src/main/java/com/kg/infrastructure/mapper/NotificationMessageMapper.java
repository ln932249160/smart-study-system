package com.kg.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.infrastructure.entity.NotificationMessageEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知消息 Mapper，操作 notification_message 表。
 */
@Mapper
public interface NotificationMessageMapper extends BaseMapper<NotificationMessageEntity> {
}
