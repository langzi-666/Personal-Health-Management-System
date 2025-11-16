package com.health.controller;

import com.health.dto.ResponseDTO;
import com.health.entity.Reminder;
import com.health.service.ReminderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 提醒规则控制器
 */
@Slf4j
@Api(tags = "提醒管理", description = "用户提醒规则相关接口")
@RestController
@RequestMapping("/api/reminders")
@Validated
public class ReminderController {

    @Autowired
    private ReminderService reminderService;

    /**
     * 创建提醒规则
     */
    @PostMapping
    @ApiOperation(value = "创建提醒规则", notes = "创建新的用户提醒规则")
    public ResponseDTO<Reminder> createReminder(@Validated @RequestBody Reminder reminder) {
        try {
            Reminder result = reminderService.createReminder(reminder);
            return ResponseDTO.success("创建提醒规则成功", result);
        } catch (Exception e) {
            log.error("创建提醒规则失败: {}", e.getMessage());
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 获取用户的提醒规则列表
     */
    @GetMapping
    @ApiOperation(value = "获取提醒规则列表", notes = "获取当前用户的所有提醒规则")
    public ResponseDTO<List<Reminder>> getReminderList(@RequestParam Long userId) {
        try {
            List<Reminder> list = reminderService.getReminderList(userId);
            return ResponseDTO.success("获取提醒规则列表成功", list);
        } catch (Exception e) {
            log.error("获取提醒规则列表失败: {}", e.getMessage());
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 获取单个提醒规则
     */
    @GetMapping("/{reminderId}")
    @ApiOperation(value = "获取提醒规则详情", notes = "获取单个提醒规则的详细信息")
    public ResponseDTO<Reminder> getReminderById(@PathVariable Long reminderId) {
        try {
            Reminder reminder = reminderService.getReminderById(reminderId);
            return ResponseDTO.success("获取提醒规则成功", reminder);
        } catch (Exception e) {
            log.error("获取提醒规则失败: {}", e.getMessage());
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 更新提醒规则
     */
    @PutMapping("/{reminderId}")
    @ApiOperation(value = "更新提醒规则", notes = "更新已有的提醒规则")
    public ResponseDTO<Reminder> updateReminder(
            @PathVariable Long reminderId,
            @Validated @RequestBody Reminder updateReminder) {
        try {
            Reminder result = reminderService.updateReminder(reminderId, updateReminder);
            return ResponseDTO.success("更新提醒规则成功", result);
        } catch (Exception e) {
            log.error("更新提醒规则失败: {}", e.getMessage());
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 删除提醒规则
     */
    @DeleteMapping("/{reminderId}")
    @ApiOperation(value = "删除提醒规则", notes = "删除指定的提醒规则")
    public ResponseDTO<String> deleteReminder(@PathVariable Long reminderId) {
        try {
            reminderService.deleteReminder(reminderId);
            return ResponseDTO.success("删除提醒规则成功");
        } catch (Exception e) {
            log.error("删除提醒规则失败: {}", e.getMessage());
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 启用/禁用提醒规则
     */
    @PutMapping("/{reminderId}/toggle")
    @ApiOperation(value = "启用/禁用提醒规则", notes = "更改提醒规则的启用状态")
    public ResponseDTO<String> toggleReminder(
            @PathVariable Long reminderId,
            @RequestParam Boolean enabled) {
        try {
            reminderService.toggleReminder(reminderId, enabled);
            String message = enabled ? "启用提醒规则成功" : "禁用提醒规则成功";
            return ResponseDTO.success(message);
        } catch (Exception e) {
            log.error("更改提醒规则状态失败: {}", e.getMessage());
            return ResponseDTO.failure(400, e.getMessage());
        }
    }
}
