package com.health.controller;

import com.health.dto.ResponseDTO;
import com.health.entity.Notification;
import com.health.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知控制器
 */
@Slf4j
@Api(tags = "通知管理", description = "用户通知相关接口")
@RestController
@RequestMapping("/api/notifications")
@Validated
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 获取通知列表（分页）
     */
    @GetMapping
    @ApiOperation(value = "获取通知列表", notes = "获取用户的通知列表（分页）")
    public ResponseDTO<List<Notification>> getNotificationList(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        try {
            List<Notification> list = notificationService.getNotificationList(userId, pageNum, pageSize);
            return ResponseDTO.success("获取通知列表成功", list);
        } catch (Exception e) {
            log.error("获取通知列表失败: {}", e.getMessage());
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 获取未读通知数
     */
    @GetMapping("/unread-count")
    @ApiOperation(value = "获取未读通知数", notes = "获取用户的未读通知计数")
    public ResponseDTO<Map<String, Object>> getUnreadCount(@RequestParam Long userId) {
        try {
            Long count = notificationService.getUnreadCount(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("unreadCount", count);
            return ResponseDTO.success("获取未读通知数成功", result);
        } catch (Exception e) {
            log.error("获取未读通知数失败: {}", e.getMessage());
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 标记通知为已读
     */
    @PutMapping("/{notificationId}/read")
    @ApiOperation(value = "标记通知为已读", notes = "标记指定通知为已读")
    public ResponseDTO<String> markAsRead(@PathVariable Long notificationId) {
        try {
            notificationService.markAsRead(notificationId);
            return ResponseDTO.success("标记通知为已读成功");
        } catch (Exception e) {
            log.error("标记通知为已读失败: {}", e.getMessage());
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 标记所有通知为已读
     */
    @PutMapping("/read-all")
    @ApiOperation(value = "标记所有通知为已读", notes = "标记用户的所有通知为已读")
    public ResponseDTO<String> markAllAsRead(@RequestParam Long userId) {
        try {
            notificationService.markAllAsRead(userId);
            return ResponseDTO.success("标记所有通知为已读成功");
        } catch (Exception e) {
            log.error("标记所有通知为已读失败: {}", e.getMessage());
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/{notificationId}")
    @ApiOperation(value = "删除通知", notes = "删除指定的通知")
    public ResponseDTO<String> deleteNotification(@PathVariable Long notificationId) {
        try {
            notificationService.deleteNotification(notificationId);
            return ResponseDTO.success("删除通知成功");
        } catch (Exception e) {
            log.error("删除通知失败: {}", e.getMessage());
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 按类型获取通知
     */
    @GetMapping("/by-type")
    @ApiOperation(value = "按类型获取通知", notes = "按通知类型过滤通知列表")
    public ResponseDTO<List<Notification>> getNotificationsByType(
            @RequestParam Long userId,
            @RequestParam String notificationType) {
        try {
            List<Notification> list = notificationService.getNotificationsByType(userId, notificationType);
            return ResponseDTO.success("获取通知列表成功", list);
        } catch (Exception e) {
            log.error("按类型获取通知失败: {}", e.getMessage());
            return ResponseDTO.failure(400, e.getMessage());
        }
    }

    /**
     * 按已读状态获取通知
     */
    @GetMapping("/by-read-status")
    @ApiOperation(value = "按已读状态获取通知", notes = "按已读/未读状态过滤通知列表")
    public ResponseDTO<List<Notification>> getNotificationsByReadStatus(
            @RequestParam Long userId,
            @RequestParam Boolean isRead) {
        try {
            List<Notification> list = notificationService.getNotificationsByReadStatus(userId, isRead);
            return ResponseDTO.success("获取通知列表成功", list);
        } catch (Exception e) {
            log.error("按已读状态获取通知失败: {}", e.getMessage());
            return ResponseDTO.failure(400, e.getMessage());
        }
    }
}
