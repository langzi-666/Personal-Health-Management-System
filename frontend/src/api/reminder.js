import request from './request'

/**
 * 获取提醒规则列表
 */
export const getReminderList = (userId) => {
  return request.get('/reminders', {
    params: { userId }
  })
}

/**
 * 获取单个提醒规则
 */
export const getReminderById = (reminderId) => {
  return request.get(`/reminders/${reminderId}`)
}

/**
 * 创建提醒规则
 */
export const createReminder = (data) => {
  return request.post('/reminders', data)
}

/**
 * 更新提醒规则
 */
export const updateReminder = (reminderId, data) => {
  return request.put(`/reminders/${reminderId}`, data)
}

/**
 * 删除提醒规则
 */
export const deleteReminder = (reminderId) => {
  return request.delete(`/reminders/${reminderId}`)
}

/**
 * 启用/禁用提醒规则
 */
export const toggleReminder = (reminderId, enabled) => {
  return request.put(`/reminders/${reminderId}/toggle`, null, {
    params: { enabled }
  })
}
