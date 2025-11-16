package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.entity.BloodPressure;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 血压记录数据访问层
 */
@Mapper
public interface BloodPressureMapper extends BaseMapper<BloodPressure> {

    /**
     * 根据用户ID和日期范围查询血压记录
     */
    List<BloodPressure> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    /**
     * 根据用户ID和日期范围分页查询
     */
    Page<BloodPressure> selectByUserIdAndDateRange(
            Page<BloodPressure> page,
            @Param("userId") Long userId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    /**
     * 获取用户最新的血压记录
     */
    BloodPressure findLatestByUserId(@Param("userId") Long userId);
}
