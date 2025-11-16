package com.health.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.entity.BloodPressure;
import com.health.mapper.BloodPressureMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 血压管理业务逻辑类
 */
@Slf4j
@Service
public class BloodPressureService extends ServiceImpl<BloodPressureMapper, BloodPressure> {

    @Autowired
    private BloodPressureMapper bloodPressureMapper;

    /**
     * 判断血压状态
     */
    private String judgeBloodPressureStatus(Integer systolic, Integer diastolic) {
        if (systolic < 120 && diastolic < 80) {
            return "正常";
        } else if (systolic >= 120 && systolic < 130 && diastolic < 80) {
            return "偏高";
        } else {
            return "高血压";
        }
    }

    /**
     * 添加血压记录
     */
    public BloodPressure addBloodPressure(BloodPressure bloodPressure) {
        if (bloodPressure.getUserId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        if (bloodPressure.getSystolic() == null || bloodPressure.getDiastolic() == null) {
            throw new RuntimeException("收缩压和舒张压不能为空");
        }
        
        // 自动判断血压状态
        String status = judgeBloodPressureStatus(bloodPressure.getSystolic(), bloodPressure.getDiastolic());
        bloodPressure.setStatus(status);
        
        if (bloodPressure.getRecordDate() == null) {
            bloodPressure.setRecordDate(new Date());
        }
        bloodPressure.setCreateTime(new Date());
        bloodPressure.setUpdateTime(new Date());
        bloodPressureMapper.insert(bloodPressure);
        log.info("血压记录添加成功: userId={}, systolic={}, diastolic={}, status={}", 
                 bloodPressure.getUserId(), bloodPressure.getSystolic(), 
                 bloodPressure.getDiastolic(), status);
        return bloodPressure;
    }

    /**
     * 获取血压记录列表（分页）
     */
    public Page<BloodPressure> getBloodPressureList(Long userId, Integer pageNum, Integer pageSize, Date startDate, Date endDate) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        Page<BloodPressure> page = new Page<>(pageNum, pageSize);
        return bloodPressureMapper.selectByUserIdAndDateRange(page, userId, startDate, endDate);
    }

    /**
     * 获取单条血压记录
     */
    public BloodPressure getBloodPressureById(Long id) {
        BloodPressure bloodPressure = bloodPressureMapper.selectById(id);
        if (bloodPressure == null) {
            throw new RuntimeException("血压记录不存在");
        }
        return bloodPressure;
    }

    /**
     * 获取用户最新的血压记录
     */
    public BloodPressure getLatestBloodPressure(Long userId) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        return bloodPressureMapper.findLatestByUserId(userId);
    }

    /**
     * 更新血压记录
     */
    public BloodPressure updateBloodPressure(Long id, BloodPressure updateBloodPressure) {
        BloodPressure bloodPressure = bloodPressureMapper.selectById(id);
        if (bloodPressure == null) {
            throw new RuntimeException("血压记录不存在");
        }

        // 更新允许修改的字段
        if (updateBloodPressure.getSystolic() != null) {
            bloodPressure.setSystolic(updateBloodPressure.getSystolic());
        }
        if (updateBloodPressure.getDiastolic() != null) {
            bloodPressure.setDiastolic(updateBloodPressure.getDiastolic());
        }
        if (updateBloodPressure.getPulse() != null) {
            bloodPressure.setPulse(updateBloodPressure.getPulse());
        }
        if (updateBloodPressure.getMeasurementLocation() != null) {
            bloodPressure.setMeasurementLocation(updateBloodPressure.getMeasurementLocation());
        }
        if (updateBloodPressure.getRecordDate() != null) {
            bloodPressure.setRecordDate(updateBloodPressure.getRecordDate());
        }
        if (updateBloodPressure.getRecordTime() != null) {
            bloodPressure.setRecordTime(updateBloodPressure.getRecordTime());
        }
        if (updateBloodPressure.getNotes() != null) {
            bloodPressure.setNotes(updateBloodPressure.getNotes());
        }

        // 重新判断血压状态
        if (updateBloodPressure.getSystolic() != null || updateBloodPressure.getDiastolic() != null) {
            Integer systolic = updateBloodPressure.getSystolic() != null ? updateBloodPressure.getSystolic() : bloodPressure.getSystolic();
            Integer diastolic = updateBloodPressure.getDiastolic() != null ? updateBloodPressure.getDiastolic() : bloodPressure.getDiastolic();
            String status = judgeBloodPressureStatus(systolic, diastolic);
            bloodPressure.setStatus(status);
        }

        bloodPressure.setUpdateTime(new Date());
        bloodPressureMapper.updateById(bloodPressure);
        log.info("血压记录已更新: id={}", id);
        return bloodPressure;
    }

    /**
     * 删除血压记录
     */
    public void deleteBloodPressure(Long id) {
        BloodPressure bloodPressure = bloodPressureMapper.selectById(id);
        if (bloodPressure == null) {
            throw new RuntimeException("血压记录不存在");
        }
        bloodPressureMapper.deleteById(id);
        log.info("血压记录已删除: id={}", id);
    }
}

