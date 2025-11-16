package com.health.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.health.entity.Notification;
import com.health.mapper.NotificationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 通知服务
 */
@Slf4j
@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * 创建通知
     */
    public Notification createNotification(Notification notification) {
        notification.setIsRead(false);
        notification.setCreateTime(new Date());
        notification.setUpdateTime(new Date());

        notificationMapper.insert(notification);
        log.info("创建通知成功: {}", notification.getTitle());
        return notification;
    }

    /**
     * 获取用户的通知列表
     */
    public List<Notification> getNotificationList(Long userId, int pageNum, int pageSize) {
        QueryWrapper<Notification> query = new QueryWrapper<>();
        query.eq("user_id", userId)
                .orderByDesc("create_time")
                .last("LIMIT " + (pageNum - 1) * pageSize + ", " + pageSize);
        return notificationMapper.selectList(query);
    }

    /**
     * 获取用户未读通知数
     */
    public Long getUnreadCount(Long userId) {
        QueryWrapper<Notification> query = new QueryWrapper<>();
        query.eq("user_id", userId).eq("is_read", false);
        return notificationMapper.selectCount(query);
    }

    /**
     * 标记通知为已读
     */
    public void markAsRead(Long notificationId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            throw new RuntimeException("通知不存在");
        }

        notification.setIsRead(true);
        notification.setUpdateTime(new Date());
        notificationMapper.updateById(notification);
        log.info("标记通知为已读: {}", notificationId);
    }

    /**
     * 标记所有通知为已读
     */
    public void markAllAsRead(Long userId) {
        QueryWrapper<Notification> query = new QueryWrapper<>();
        query.eq("user_id", userId).eq("is_read", false);
        
        List<Notification> notifications = notificationMapper.selectList(query);
        for (Notification notification : notifications) {
            notification.setIsRead(true);
            notification.setUpdateTime(new Date());
            notificationMapper.updateById(notification);
        }
        log.info("标记用户{}的所有通知为已读", userId);
    }

    /**
     * 删除通知
     */
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            throw new RuntimeException("通知不存在");
        }

        notificationMapper.deleteById(notificationId);
        log.info("删除通知成功: {}", notificationId);
    }

    /**
     * 删除用户的所有旧通知（7天前）
     */
    public void deleteOldNotifications(Long userId) {
        QueryWrapper<Notification> query = new QueryWrapper<>();
        long sevenDaysAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
        query.eq("user_id", userId)
                .lt("create_time", new Date(sevenDaysAgo));
        
        notificationMapper.delete(query);
        log.info("删除用户{}的旧通知", userId);
    }

    /**
     * 按类型过滤通知
     */
    public List<Notification> getNotificationsByType(Long userId, String notificationType) {
        QueryWrapper<Notification> query = new QueryWrapper<>();
        query.eq("user_id", userId)
                .eq("notification_type", notificationType)
                .orderByDesc("create_time");
        return notificationMapper.selectList(query);
    }

    /**
     * 按已读状态过滤通知
     */
    public List<Notification> getNotificationsByReadStatus(Long userId, Boolean isRead) {
        QueryWrapper<Notification> query = new QueryWrapper<>();
        query.eq("user_id", userId)
                .eq("is_read", isRead)
                .orderByDesc("create_time");
        return notificationMapper.selectList(query);
    }
}
