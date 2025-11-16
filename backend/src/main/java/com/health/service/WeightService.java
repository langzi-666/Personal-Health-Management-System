package com.health.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.entity.Weight;
import com.health.mapper.WeightMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 体重管理业务逻辑类
 */
@Slf4j
@Service
public class WeightService extends ServiceImpl<WeightMapper, Weight> {

    @Autowired
    private WeightMapper weightMapper;

    /**
     * 添加体重记录
     */
    public Weight addWeight(Weight weight) {
        if (weight.getUserId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        if (weight.getWeight() == null) {
            throw new RuntimeException("体重不能为空");
        }
        if (weight.getRecordDate() == null) {
            weight.setRecordDate(new Date());
        }
        weight.setCreateTime(new Date());
        weight.setUpdateTime(new Date());
        weightMapper.insert(weight);
        log.info("体重记录添加成功: userId={}, weight={}", weight.getUserId(), weight.getWeight());
        return weight;
    }

    /**
     * 获取体重记录列表（分页）
     */
    public Page<Weight> getWeightList(Long userId, Integer pageNum, Integer pageSize, Date startDate, Date endDate) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        Page<Weight> page = new Page<>(pageNum, pageSize);
        return weightMapper.selectByUserIdAndDateRange(page, userId, startDate, endDate);
    }

    /**
     * 获取单条体重记录
     */
    public Weight getWeightById(Long id) {
        Weight weight = weightMapper.selectById(id);
        if (weight == null) {
            throw new RuntimeException("体重记录不存在");
        }
        return weight;
    }

    /**
     * 获取用户最新的体重记录
     */
    public Weight getLatestWeight(Long userId) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        return weightMapper.findLatestByUserId(userId);
    }

    /**
     * 更新体重记录
     */
    public Weight updateWeight(Long id, Weight updateWeight) {
        Weight weight = weightMapper.selectById(id);
        if (weight == null) {
            throw new RuntimeException("体重记录不存在");
        }

        // 更新允许修改的字段
        if (updateWeight.getWeight() != null) {
            weight.setWeight(updateWeight.getWeight());
        }
        if (updateWeight.getBodyFatRate() != null) {
            weight.setBodyFatRate(updateWeight.getBodyFatRate());
        }
        if (updateWeight.getRecordDate() != null) {
            weight.setRecordDate(updateWeight.getRecordDate());
        }
        if (updateWeight.getNotes() != null) {
            weight.setNotes(updateWeight.getNotes());
        }

        weight.setUpdateTime(new Date());
        weightMapper.updateById(weight);
        log.info("体重记录已更新: id={}", id);
        return weight;
    }

    /**
     * 删除体重记录
     */
    public void deleteWeight(Long id) {
        Weight weight = weightMapper.selectById(id);
        if (weight == null) {
            throw new RuntimeException("体重记录不存在");
        }
        weightMapper.deleteById(id);
        log.info("体重记录已删除: id={}", id);
    }
}

