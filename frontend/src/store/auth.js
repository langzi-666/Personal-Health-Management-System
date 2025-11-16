import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

/**
 * 用户认证状态管理
 */
export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const isAuthenticated = computed(() => !!token.value)

  // 设置Token和用户信息
  const setAuth = (authToken, userData) => {
    token.value = authToken
    user.value = userData
    localStorage.setItem('token', authToken)
    localStorage.setItem('user', JSON.stringify(userData))
  }

  // 清空认证信息
  const clearAuth = () => {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  // 获取当前用户
  const getCurrentUser = () => user.value

  // 获取Token
  const getToken = () => token.value

  // 检查是否已登录
  const isLoggedIn = () => !!token.value

  return {
    token,
    user,
    isAuthenticated,
    setAuth,
    clearAuth,
    getCurrentUser,
    getToken,
    isLoggedIn
  }
})
