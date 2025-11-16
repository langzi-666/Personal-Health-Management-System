package com.health.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.entity.Alert;
import com.health.mapper.AlertMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 告警规则服务
 */
@Slf4j
@Service
public class AlertService {

    @Autowired
    private AlertMapper alertMapper;

    /**
     * 创建告警规则
     */
    public Alert createAlert(Alert alert) {
        if (alert.getAlertName() == null || alert.getAlertName().isEmpty()) {
            throw new RuntimeException("告警名称不能为空");
        }
        if (alert.getAlertType() == null || alert.getAlertType().isEmpty()) {
            throw new RuntimeException("告警类型不能为空");
        }

        alert.setCreateTime(new Date());
        alert.setUpdateTime(new Date());
        alert.setIsEnabled(true);

        alertMapper.insert(alert);
        log.info("创建告警规则成功: {}", alert.getAlertName());
        return alert;
    }

    /**
     * 获取用户的告警规则列表
     */
    public List<Alert> getAlertList(Long userId) {
        QueryWrapper<Alert> query = new QueryWrapper<>();
        query.eq("user_id", userId).orderByDesc("create_time");
        return alertMapper.selectList(query);
    }

    /**
     * 获取单个告警规则
     */
    public Alert getAlertById(Long alertId) {
        Alert alert = alertMapper.selectById(alertId);
        if (alert == null) {
            throw new RuntimeException("告警规则不存在");
        }
        return alert;
    }

    /**
     * 更新告警规则
     */
    public Alert updateAlert(Long alertId, Alert updateAlert) {
        Alert alert = getAlertById(alertId);

        if (updateAlert.getAlertName() != null) {
            alert.setAlertName(updateAlert.getAlertName());
        }
        if (updateAlert.getAlertType() != null) {
            alert.setAlertType(updateAlert.getAlertType());
        }
        if (updateAlert.getConditionType() != null) {
            alert.setConditionType(updateAlert.getConditionType());
        }
        if (updateAlert.getThresholdValue() != null) {
            alert.setThresholdValue(updateAlert.getThresholdValue());
        }
        if (updateAlert.getIsEnabled() != null) {
            alert.setIsEnabled(updateAlert.getIsEnabled());
        }
        if (updateAlert.getNotificationMethod() != null) {
            alert.setNotificationMethod(updateAlert.getNotificationMethod());
        }

        alert.setUpdateTime(new Date());
        alertMapper.updateById(alert);
        log.info("更新告警规则成功: {}", alertId);
        return alert;
    }

    /**
     * 删除告警规则
     */
    public void deleteAlert(Long alertId) {
        Alert alert = getAlertById(alertId);
        alertMapper.deleteById(alertId);
        log.info("删除告警规则成功: {}", alert.getAlertName());
    }

    /**
     * 启用/禁用告警规则
     */
    public void toggleAlert(Long alertId, Boolean enabled) {
        Alert alert = getAlertById(alertId);
        alert.setIsEnabled(enabled);
        alert.setUpdateTime(new Date());
        alertMapper.updateById(alert);
        log.info("更改告警规则状态: {} - {}", alertId, enabled ? "启用" : "禁用");
    }
}
