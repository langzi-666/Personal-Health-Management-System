import request from './request'

/**
 * 获取通知列表（分页）
 */
export const getNotificationList = (userId, pageNum = 1, pageSize = 20) => {
  return request.get('/notifications', {
    params: { userId, pageNum, pageSize }
  })
}

/**
 * 获取未读通知数
 */
export const getUnreadCount = (userId) => {
  return request.get('/notifications/unread-count', {
    params: { userId }
  })
}

/**
 * 标记通知为已读
 */
export const markAsRead = (notificationId) => {
  return request.put(`/notifications/${notificationId}/read`)
}

/**
 * 标记所有通知为已读
 */
export const markAllAsRead = (userId) => {
  return request.put('/notifications/read-all', null, {
    params: { userId }
  })
}

/**
 * 删除通知
 */
export const deleteNotification = (notificationId) => {
  return request.delete(`/notifications/${notificationId}`)
}

/**
 * 按类型获取通知
 */
export const getNotificationsByType = (userId, notificationType) => {
  return request.get('/notifications/by-type', {
    params: { userId, notificationType }
  })
}

/**
 * 按已读状态获取通知
 */
export const getNotificationsByReadStatus = (userId, isRead) => {
  return request.get('/notifications/by-read-status', {
    params: { userId, isRead }
  })
}
