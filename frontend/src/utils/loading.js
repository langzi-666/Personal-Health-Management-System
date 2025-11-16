import { ElLoading } from 'element-plus'

let loadingInstance = null

/**
 * 显示全局加载指示器
 */
export const showLoading = (text = '加载中...') => {
  if (loadingInstance) {
    return
  }
  loadingInstance = ElLoading.service({
    lock: true,
    text: text,
    background: 'rgba(0, 0, 0, 0.7)'
  })
}

/**
 * 隐藏全局加载指示器
 */
export const hideLoading = () => {
  if (loadingInstance) {
    loadingInstance.close()
    loadingInstance = null
  }
}

