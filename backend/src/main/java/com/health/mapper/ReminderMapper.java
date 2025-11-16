package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.Reminder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 提醒规则Mapper接口
 */
@Mapper
public interface ReminderMapper extends BaseMapper<Reminder> {
}
