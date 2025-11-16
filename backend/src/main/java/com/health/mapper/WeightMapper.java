package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.entity.Weight;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 体重记录数据访问层
 */
@Mapper
public interface WeightMapper extends BaseMapper<Weight> {

    /**
     * 根据用户ID和日期范围查询体重记录
     */
    List<Weight> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    /**
     * 根据用户ID和日期范围分页查询
     */
    Page<Weight> selectByUserIdAndDateRange(
            Page<Weight> page,
            @Param("userId") Long userId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    /**
     * 获取用户最新的体重记录
     */
    Weight findLatestByUserId(@Param("userId") Long userId);
}
