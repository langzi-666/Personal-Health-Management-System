import axios from 'axios'
import { showLoading, hideLoading } from '../utils/loading.js'
import { ElMessage } from 'element-plus'

// 创建axios实例
const instance = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求计数器，用于控制加载指示器
let requestCount = 0

// 请求拦截器
instance.interceptors.request.use(
  (config) => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    // 显示加载指示器（排除文件上传等特殊请求）
    if (!config.headers['Content-Type']?.includes('multipart/form-data')) {
      requestCount++
      if (requestCount === 1) {
        showLoading()
      }
    }

    // 记录请求日志
    console.log(`[请求] ${config.method.toUpperCase()} ${config.url}`, config.params || config.data)

    return config
  },
  (error) => {
    hideLoading()
    return Promise.reject(error)
  }
)

// 响应拦截器
instance.interceptors.response.use(
  (response) => {
    // 隐藏加载指示器
    requestCount--
    if (requestCount <= 0) {
      requestCount = 0
      hideLoading()
    }

    // 记录响应日志
    console.log(`[响应] ${response.config.method.toUpperCase()} ${response.config.url}`, response.data)

    return response.data
  },
  (error) => {
    // 隐藏加载指示器
    requestCount--
    if (requestCount <= 0) {
      requestCount = 0
      hideLoading()
    }

    // 记录错误日志
    console.error(`[错误] ${error.config?.method?.toUpperCase()} ${error.config?.url}`, error.response?.data || error.message)

    if (error.response?.status === 401) {
      // Token失效或未授权
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      setTimeout(() => {
        window.location.href = '/login'
      }, 1000)
    } else if (error.response?.status === 403) {
      ElMessage.error('没有权限访问该资源')
    } else if (error.response?.status === 404) {
      ElMessage.error('请求的资源不存在')
    } else if (error.response?.status >= 500) {
      ElMessage.error('服务器错误，请稍后重试')
    } else if (error.message === 'Network Error') {
      ElMessage.error('网络连接失败，请检查网络')
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请稍后重试')
    } else {
      const errorMessage = error.response?.data?.message || error.message || '请求失败'
      ElMessage.error(errorMessage)
    }

    return Promise.reject(error)
  }
)

export default instance
