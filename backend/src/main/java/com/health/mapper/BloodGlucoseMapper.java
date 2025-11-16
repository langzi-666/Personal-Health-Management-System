package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.entity.BloodGlucose;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface BloodGlucoseMapper extends BaseMapper<BloodGlucose> {
    Page<BloodGlucose> selectByUserIdAndDateRange(
            Page<BloodGlucose> page,
            @Param("userId") Long userId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);
}
