import request from './request'

/**
 * 获取告警规则列表
 */
export const getAlertList = (userId) => {
  return request.get('/alerts', {
    params: { userId }
  })
}

/**
 * 获取单个告警规则
 */
export const getAlertById = (alertId) => {
  return request.get(`/alerts/${alertId}`)
}

/**
 * 创建告警规则
 */
export const createAlert = (data) => {
  return request.post('/alerts', data)
}

/**
 * 更新告警规则
 */
export const updateAlert = (alertId, data) => {
  return request.put(`/alerts/${alertId}`, data)
}

/**
 * 删除告警规则
 */
export const deleteAlert = (alertId) => {
  return request.delete(`/alerts/${alertId}`)
}

/**
 * 启用/禁用告警规则
 */
export const toggleAlert = (alertId, enabled) => {
  return request.put(`/alerts/${alertId}/toggle`, null, {
    params: { enabled }
  })
}
