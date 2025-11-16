import request from './request'

/**
 * 体重管理API
 */

export const addWeight = (data) => {
  return request.post('/health/weight', data)
}

export const getWeightList = (userId, pageNum = 1, pageSize = 10, startDate, endDate) => {
  return request.get('/health/weight', {
    params: {
      userId,
      pageNum,
      pageSize,
      startDate,
      endDate
    }
  })
}

export const updateWeight = (id, data) => {
  return request.put(`/health/weight/${id}`, data)
}

export const deleteWeight = (id) => {
  return request.delete(`/health/weight/${id}`)
}

/**
 * 血压管理API
 */

export const addBloodPressure = (data) => {
  return request.post('/health/blood-pressure', data)
}

export const getBloodPressureList = (userId, pageNum = 1, pageSize = 10, startDate, endDate) => {
  return request.get('/health/blood-pressure', {
    params: {
      userId,
      pageNum,
      pageSize,
      startDate,
      endDate
    }
  })
}

export const updateBloodPressure = (id, data) => {
  return request.put(`/health/blood-pressure/${id}`, data)
}

export const deleteBloodPressure = (id) => {
  return request.delete(`/health/blood-pressure/${id}`)
}

/**
 * 血糖管理API
 */

export const addBloodGlucose = (data) => {
  return request.post('/health/blood-glucose', data)
}

export const getBloodGlucoseList = (userId, pageNum = 1, pageSize = 10, startDate, endDate) => {
  return request.get('/health/blood-glucose', {
    params: {
      userId,
      pageNum,
      pageSize,
      startDate,
      endDate
    }
  })
}

export const updateBloodGlucose = (id, data) => {
  return request.put(`/health/blood-glucose/${id}`, data)
}

export const deleteBloodGlucose = (id) => {
  return request.delete(`/health/blood-glucose/${id}`)
}

/**
 * 运动管理API
 */

export const addExercise = (data) => {
  return request.post('/health/exercise', data)
}

export const getExerciseList = (userId, pageNum = 1, pageSize = 10, startDate, endDate) => {
  return request.get('/health/exercise', {
    params: {
      userId,
      pageNum,
      pageSize,
      startDate,
      endDate
    }
  })
}

export const updateExercise = (id, data) => {
  return request.put(`/health/exercise/${id}`, data)
}

export const deleteExercise = (id) => {
  return request.delete(`/health/exercise/${id}`)
}

/**
 * 睡眠管理API
 */

export const addSleep = (data) => {
  return request.post('/health/sleep', data)
}

export const getSleepList = (userId, pageNum = 1, pageSize = 10, startDate, endDate) => {
  return request.get('/health/sleep', {
    params: {
      userId,
      pageNum,
      pageSize,
      startDate,
      endDate
    }
  })
}

export const updateSleep = (id, data) => {
  return request.put(`/health/sleep/${id}`, data)
}

export const deleteSleep = (id) => {
  return request.delete(`/health/sleep/${id}`)
}

/**
 * 饮食管理API
 */

export const addDiet = (data) => {
  return request.post('/health/diet', data)
}

export const getDietList = (userId, pageNum = 1, pageSize = 10, startDate, endDate) => {
  return request.get('/health/diet', {
    params: {
      userId,
      pageNum,
      pageSize,
      startDate,
      endDate
    }
  })
}

export const updateDiet = (id, data) => {
  return request.put(`/health/diet/${id}`, data)
}

export const deleteDiet = (id) => {
  return request.delete(`/health/diet/${id}`)
}

/**
 * 心理健康管理API
 */

export const addMentalHealth = (data) => {
  return request.post('/health/mental-health', data)
}

export const getMentalHealthList = (userId, pageNum = 1, pageSize = 10, startDate, endDate) => {
  return request.get('/health/mental-health', {
    params: {
      userId,
      pageNum,
      pageSize,
      startDate,
      endDate
    }
  })
}

export const updateMentalHealth = (id, data) => {
  return request.put(`/health/mental-health/${id}`, data)
}

export const deleteMentalHealth = (id) => {
  return request.delete(`/health/mental-health/${id}`)
}

/**
 * 健康建议API
 */
export const getHealthAdvice = (userId) => {
  return request.get('/advice', {
    params: { userId }
  })
}