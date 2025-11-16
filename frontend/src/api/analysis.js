import request from './request.js'

/**
 * 获取日期范围内的数据
 */
export const getDataByDateRange = (userId, dataType, startDate, endDate) => {
  return request.get('/analysis/range', {
    params: {
      userId,
      dataType,
      startDate,
      endDate
    }
  })
}

/**
 * 获取对比分析数据
 */
export const getComparisonData = (userId, dataType, currentPeriod, previousPeriod) => {
  return request.get('/analysis/comparison', {
    params: {
      userId,
      dataType,
      currentPeriod,
      previousPeriod
    }
  })
}

/**
 * 导出数据
 */
export const exportData = (userId, dataType, startDate, endDate, format = 'csv') => {
  return request.post('/analysis/export', null, {
    params: {
      userId,
      dataType,
      startDate,
      endDate,
      format
    },
    responseType: 'blob' // 重要：设置响应类型为blob以处理文件下载
  })
}
