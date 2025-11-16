package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.Alert;
import org.apache.ibatis.annotations.Mapper;

/**
 * 告警规则Mapper接口
 */
@Mapper
public interface AlertMapper extends BaseMapper<Alert> {
}
