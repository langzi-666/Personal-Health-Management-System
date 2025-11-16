package com.health.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.entity.Reminder;
import com.health.mapper.ReminderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 提醒规则服务
 */
@Slf4j
@Service
public class ReminderService {

    @Autowired
    private ReminderMapper reminderMapper;

    /**
     * 创建提醒规则
     */
    public Reminder createReminder(Reminder reminder) {
        if (reminder.getReminderName() == null || reminder.getReminderName().isEmpty()) {
            throw new RuntimeException("提醒名称不能为空");
        }
        if (reminder.getReminderTime() == null || reminder.getReminderTime().isEmpty()) {
            throw new RuntimeException("提醒时间不能为空");
        }

        reminder.setCreateTime(new Date());
        reminder.setUpdateTime(new Date());
        reminder.setIsEnabled(true);

        reminderMapper.insert(reminder);
        log.info("创建提醒规则成功: {}", reminder.getReminderName());
        return reminder;
    }

    /**
     * 获取用户的提醒规则列表
     */
    public List<Reminder> getReminderList(Long userId) {
        QueryWrapper<Reminder> query = new QueryWrapper<>();
        query.eq("user_id", userId).orderByDesc("create_time");
        return reminderMapper.selectList(query);
    }

    /**
     * 获取单个提醒规则
     */
    public Reminder getReminderById(Long reminderId) {
        Reminder reminder = reminderMapper.selectById(reminderId);
        if (reminder == null) {
            throw new RuntimeException("提醒规则不存在");
        }
        return reminder;
    }

    /**
     * 更新提醒规则
     */
    public Reminder updateReminder(Long reminderId, Reminder updateReminder) {
        Reminder reminder = getReminderById(reminderId);

        if (updateReminder.getReminderName() != null) {
            reminder.setReminderName(updateReminder.getReminderName());
        }
        if (updateReminder.getReminderType() != null) {
            reminder.setReminderType(updateReminder.getReminderType());
        }
        if (updateReminder.getReminderTime() != null) {
            reminder.setReminderTime(updateReminder.getReminderTime());
        }
        if (updateReminder.getFrequency() != null) {
            reminder.setFrequency(updateReminder.getFrequency());
        }
        if (updateReminder.getIsEnabled() != null) {
            reminder.setIsEnabled(updateReminder.getIsEnabled());
        }
        if (updateReminder.getNotificationMethod() != null) {
            reminder.setNotificationMethod(updateReminder.getNotificationMethod());
        }

        reminder.setUpdateTime(new Date());
        reminderMapper.updateById(reminder);
        log.info("更新提醒规则成功: {}", reminderId);
        return reminder;
    }

    /**
     * 删除提醒规则
     */
    public void deleteReminder(Long reminderId) {
        Reminder reminder = getReminderById(reminderId);
        reminderMapper.deleteById(reminderId);
        log.info("删除提醒规则成功: {}", reminder.getReminderName());
    }

    /**
     * 启用/禁用提醒规则
     */
    public void toggleReminder(Long reminderId, Boolean enabled) {
        Reminder reminder = getReminderById(reminderId);
        reminder.setIsEnabled(enabled);
        reminder.setUpdateTime(new Date());
        reminderMapper.updateById(reminder);
        log.info("更改提醒规则状态: {} - {}", reminderId, enabled ? "启用" : "禁用");
    }
}
