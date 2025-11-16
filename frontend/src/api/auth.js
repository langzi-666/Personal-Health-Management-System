import request from './request.js'

/**
 * 用户认证API
 */

/**
 * 用户注册
 */
export const register = (data) => {
  return request.post('/auth/register', data)
}

/**
 * 用户登录
 */
export const login = (data) => {
  return request.post('/auth/login', data)
}

/**
 * 重置密码
 */
export const resetPassword = (email, newPassword) => {
  return request.post('/auth/reset-password', null, {
    params: {
      email,
      newPassword
    }
  })
}

/**
 * 获取当前用户信息
 */
export const getCurrentUser = () => {
  return request.get('/auth/me')
}

/**
 * 获取用户信息
 */
export const getUserInfo = (userId) => {
  return request.get(`/users/${userId}`)
}

/**
 * 更新用户信息
 */
export const updateUserInfo = (userId, data) => {
  return request.put(`/users/${userId}`, data)
}

/**
 * 修改密码
 */
export const changePassword = (userId, oldPassword, newPassword) => {
  return request.post(`/users/${userId}/change-password`, null, {
    params: {
      oldPassword,
      newPassword
    }
  })
}

/**
 * 上传头像
 */
export const uploadAvatar = (userId, file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post(`/users/${userId}/avatar`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}