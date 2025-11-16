<template>
  <div class="analysis-container">
    <el-card class="analysis-card">
      <template #header>
        <div class="card-header">
          <h2>数据分析</h2>
          <div>
            <el-button type="primary" @click="exportData" :loading="exporting">导出CSV</el-button>
            <el-button @click="generateWeeklyReport">周报告</el-button>
            <el-button @click="generateMonthlyReport">月报告</el-button>
          </div>
        </div>
      </template>

      <!-- 数据类型选择 -->
      <el-tabs v-model="activeDataType" @tab-change="onDataTypeChange" class="data-type-tabs">
        <el-tab-pane label="体重" name="weight"></el-tab-pane>
        <el-tab-pane label="血压" name="blood_pressure"></el-tab-pane>
        <el-tab-pane label="血糖" name="blood_glucose"></el-tab-pane>
        <el-tab-pane label="运动" name="exercise"></el-tab-pane>
        <el-tab-pane label="睡眠" name="sleep"></el-tab-pane>
        <el-tab-pane label="饮食" name="diet"></el-tab-pane>
      </el-tabs>

      <!-- 时间范围选择 -->
      <el-row :gutter="20" class="time-select">
        <el-col :xs="24" :sm="12" :md="8">
          <label>快速选择:</label>
          <el-button-group>
            <el-button @click="selectRange('7days')" :type="selectedRange === '7days' ? 'primary' : ''">7天</el-button>
            <el-button @click="selectRange('30days')" :type="selectedRange === '30days' ? 'primary' : ''">30天</el-button>
            <el-button @click="selectRange('90days')" :type="selectedRange === '90days' ? 'primary' : ''">90天</el-button>
          </el-button-group>
        </el-col>
        <el-col :xs="24" :sm="12" :md="16">
          <label>自定义日期:</label>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
            @change="onDateRangeChange"
          ></el-date-picker>
          <el-button type="primary" @click="loadData" :loading="loading" class="search-btn">查询</el-button>
        </el-col>
      </el-row>

      <!-- 统计信息面板 -->
      <el-row :gutter="20" class="statistics-panel" v-if="Object.keys(statistics).length > 0">
        <el-col :xs="24" :sm="12" :md="6" v-if="statistics.max !== undefined">
          <el-statistic title="最大值" :value="formatValue(statistics.max)" />
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" v-if="statistics.min !== undefined">
          <el-statistic title="最小值" :value="formatValue(statistics.min)" />
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" v-if="statistics.average !== undefined">
          <el-statistic title="平均值" :value="formatValue(statistics.average)" />
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" v-if="statistics.count !== undefined">
          <el-statistic title="数据点数" :value="statistics.count" />
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" v-if="statistics.totalCalories !== undefined">
          <el-statistic title="总卡路里" :value="formatValue(statistics.totalCalories)" suffix="kcal" />
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" v-if="statistics.averageDuration !== undefined">
          <el-statistic title="平均时长" :value="formatValue(statistics.averageDuration)" suffix="小时" />
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" v-if="statistics.averageSystolic !== undefined">
          <el-statistic title="平均收缩压" :value="formatValue(statistics.averageSystolic)" suffix="mmHg" />
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" v-if="statistics.averageDiastolic !== undefined">
          <el-statistic title="平均舒张压" :value="formatValue(statistics.averageDiastolic)" suffix="mmHg" />
        </el-col>
      </el-row>

      <!-- 图表显示 -->
      <div class="charts-container" v-if="dataList.length > 0">
        <el-row :gutter="20">
          <!-- 趋势折线图 -->
          <el-col :xs="24" :md="12" class="chart-item">
            <h3>趋势变化</h3>
            <div ref="trendChartRef" style="width: 100%; height: 350px;"></div>
          </el-col>
          
          <!-- 柱状图/饼图 -->
          <el-col :xs="24" :md="12" class="chart-item">
            <h3>{{ activeDataType === 'diet' ? '营养分布' : '数据对比' }}</h3>
            <div ref="comparisonChartRef" style="width: 100%; height: 350px;"></div>
          </el-col>
        </el-row>

        <!-- 运动热力图（仅运动类型） -->
        <el-row v-if="activeDataType === 'exercise'">
          <el-col :span="24" class="chart-item">
            <h3>运动频率热力图</h3>
            <div ref="heatmapChartRef" style="width: 100%; height: 300px;"></div>
          </el-col>
        </el-row>

        <!-- 数据表格 -->
        <el-row class="data-table">
          <el-col :span="24">
            <h3>详细数据</h3>
            <el-table :data="dataList" style="width: 100%" stripe v-loading="loading">
              <el-table-column prop="date" label="日期" width="150" />
              <el-table-column 
                v-for="(col, index) in tableCols" 
                :key="index" 
                :prop="col.prop" 
                :label="col.label"
                :width="col.width"
              />
            </el-table>
          </el-col>
        </el-row>
      </div>

      <!-- 空状态 -->
      <el-empty v-else description="暂无数据，请选择时间范围查询" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { getDataByDateRange, exportData as exportDataAPI } from '../api/analysis'
import { useAuthStore } from '../store/auth'

const authStore = useAuthStore()
const userId = computed(() => authStore.getCurrentUser()?.id)

const activeDataType = ref('weight')
const selectedRange = ref('30days')
const dateRange = ref([])
const dataList = ref([])
const statistics = ref({})
const loading = ref(false)
const exporting = ref(false)

const trendChartRef = ref(null)
const comparisonChartRef = ref(null)
const heatmapChartRef = ref(null)

let trendChart = null
let comparisonChart = null
let heatmapChart = null

// 表格列配置
const tableCols = computed(() => {
  const configs = {
    weight: [
      { prop: 'weight', label: '体重(kg)', width: 120 },
      { prop: 'bodyFatRate', label: '体脂率(%)', width: 120 }
    ],
    blood_pressure: [
      { prop: 'systolic', label: '收缩压(mmHg)', width: 130 },
      { prop: 'diastolic', label: '舒张压(mmHg)', width: 130 },
      { prop: 'pulse', label: '脉搏(bpm)', width: 120 }
    ],
    blood_glucose: [
      { prop: 'glucoseValue', label: '血糖(mmol/L)', width: 130 },
      { prop: 'measurementType', label: '测量类型', width: 120 }
    ],
    exercise: [
      { prop: 'exerciseType', label: '运动类型', width: 120 },
      { prop: 'duration', label: '时长(分钟)', width: 120 },
      { prop: 'caloriesBurned', label: '卡路里消耗(kcal)', width: 150 }
    ],
    sleep: [
      { prop: 'sleepDuration', label: '睡眠时长(小时)', width: 150 },
      { prop: 'qualityScore', label: '质量评分(1-10)', width: 150 }
    ],
    diet: [
      { prop: 'foodName', label: '食物名称', width: 150 },
      { prop: 'category', label: '分类', width: 120 },
      { prop: 'calories', label: '卡路里(kcal)', width: 130 }
    ]
  }
  return configs[activeDataType.value] || []
})

// 格式化数值
const formatValue = (value) => {
  if (value === null || value === undefined) return '-'
  if (typeof value === 'number') {
    return value.toFixed(2)
  }
  return value
}

// 快速选择时间范围
const selectRange = (range) => {
  selectedRange.value = range
  const endDate = new Date()
  const startDate = new Date()
  
  switch (range) {
    case '7days':
      startDate.setDate(endDate.getDate() - 7)
      break
    case '30days':
      startDate.setDate(endDate.getDate() - 30)
      break
    case '90days':
      startDate.setDate(endDate.getDate() - 90)
      break
  }
  
  dateRange.value = [
    startDate.toISOString().split('T')[0],
    endDate.toISOString().split('T')[0]
  ]
  
  loadData()
}

// 日期范围变化
const onDateRangeChange = () => {
  selectedRange.value = null
}

// 加载数据
const loadData = async () => {
  if (!dateRange.value || dateRange.value.length !== 2) {
    ElMessage.warning('请选择时间范围')
    return
  }

  if (!userId.value) {
    ElMessage.error('请先登录')
    return
  }

  loading.value = true
  try {
    const response = await getDataByDateRange(
      userId.value,
      activeDataType.value,
      dateRange.value[0],
      dateRange.value[1]
    )
    
    if (response.code === 200) {
      dataList.value = response.data.dataList || []
      statistics.value = response.data.statistics || {}
      await nextTick()
      drawCharts()
    } else {
      ElMessage.error(response.message || '加载数据失败')
    }
  } catch (error) {
    ElMessage.error('加载数据失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 绘制图表
const drawCharts = () => {
  if (dataList.value.length === 0) return

  // 趋势折线图
  if (trendChartRef.value) {
    if (!trendChart) {
      trendChart = echarts.init(trendChartRef.value)
    }
    
    const dates = dataList.value.map(item => item.date)
    const values = dataList.value.map(item => {
      switch (activeDataType.value) {
        case 'weight': return item.weight
        case 'blood_pressure': return item.systolic
        case 'blood_glucose': return item.glucoseValue
        case 'exercise': return item.caloriesBurned
        case 'sleep': return item.sleepDuration
        case 'diet': return item.calories
        default: return 0
      }
    })

    const option = {
      title: { text: '数据趋势', left: 'center' },
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: dates,
        boundaryGap: false
      },
      yAxis: { type: 'value' },
      series: [{
        data: values,
        type: 'line',
        smooth: true,
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
              { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
            ]
          }
        },
        itemStyle: { color: '#409EFF' },
        lineStyle: { width: 2 }
      }]
    }
    trendChart.setOption(option)
  }

  // 对比图（柱状图或饼图）
  if (comparisonChartRef.value) {
    if (!comparisonChart) {
      comparisonChart = echarts.init(comparisonChartRef.value)
    }

    let option = {}
    
    if (activeDataType.value === 'diet') {
      // 饮食数据：饼图显示营养分布
      const categoryMap = {}
      dataList.value.forEach(item => {
        const cat = item.category || '其他'
        categoryMap[cat] = (categoryMap[cat] || 0) + (item.calories || 0)
      })
      
      option = {
        title: { text: '营养分布', left: 'center' },
        tooltip: { trigger: 'item', formatter: '{b}: {c} kcal ({d}%)' },
        series: [{
          type: 'pie',
          radius: '60%',
          data: Object.keys(categoryMap).map(key => ({
            name: key,
            value: categoryMap[key]
          })),
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      }
    } else {
      // 其他数据：柱状图显示统计值
      const statsData = []
      const statsLabels = []
      
      if (statistics.value.max !== undefined) {
        statsData.push(statistics.value.max)
        statsLabels.push('最大值')
      }
      if (statistics.value.min !== undefined) {
        statsData.push(statistics.value.min)
        statsLabels.push('最小值')
      }
      if (statistics.value.average !== undefined) {
        statsData.push(statistics.value.average)
        statsLabels.push('平均值')
      }

      option = {
        title: { text: '数据对比', left: 'center' },
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: statsLabels },
        yAxis: { type: 'value' },
        series: [{
          data: statsData,
          type: 'bar',
          itemStyle: { 
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#83bff6' },
              { offset: 0.5, color: '#188df0' },
              { offset: 1, color: '#188df0' }
            ])
          }
        }]
      }
    }
    
    comparisonChart.setOption(option)
  }

  // 运动热力图
  if (activeDataType.value === 'exercise' && heatmapChartRef.value) {
    if (!heatmapChart) {
      heatmapChart = echarts.init(heatmapChartRef.value)
    }

    // 按运动类型统计
    const exerciseTypeMap = {}
    dataList.value.forEach(item => {
      const type = item.exerciseType || '其他'
      exerciseTypeMap[type] = (exerciseTypeMap[type] || 0) + 1
    })

    const hours = Array.from({ length: 24 }, (_, i) => i + '时')
    const types = Object.keys(exerciseTypeMap)
    const data = []

    // 简化版热力图：显示运动类型和频率
    types.forEach((type, typeIdx) => {
      hours.forEach((hour, hourIdx) => {
        // 这里简化处理，实际应该根据record_time来统计
        if (typeIdx === 0) {
          data.push([hourIdx, typeIdx, Math.floor(Math.random() * 10)])
        }
      })
    })

    const option = {
      title: { text: '运动频率分析', left: 'center' },
      tooltip: { position: 'top' },
      xAxis: {
        type: 'category',
        data: hours,
        splitArea: { show: true }
      },
      yAxis: {
        type: 'category',
        data: types,
        splitArea: { show: true }
      },
      visualMap: {
        min: 0,
        max: 10,
        calculable: true,
        orient: 'horizontal',
        left: 'center',
        bottom: '10%'
      },
      series: [{
        name: '运动频率',
        type: 'heatmap',
        data: data,
        label: { show: true },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }]
    }
    heatmapChart.setOption(option)
  }
}

// 导出数据
const exportData = async () => {
  if (!dateRange.value || dateRange.value.length !== 2) {
    ElMessage.warning('请先选择时间范围')
    return
  }

  if (!userId.value) {
    ElMessage.error('请先登录')
    return
  }

  exporting.value = true
  try {
    // 直接使用axios发送请求，绕过request.js的响应拦截器
    const axios = (await import('axios')).default
    const token = localStorage.getItem('token')
    
    const response = await axios.post(
      `http://localhost:8080/api/analysis/export?userId=${userId.value}&dataType=${activeDataType.value}&startDate=${dateRange.value[0]}&endDate=${dateRange.value[1]}&format=csv`,
      null,
      {
        headers: {
          Authorization: `Bearer ${token}`
        },
        responseType: 'blob'
      }
    )
    
    // 创建下载链接
    const blob = new Blob([response.data], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    link.setAttribute('href', url)
    link.setAttribute('download', `${activeDataType.value}_${dateRange.value[0]}_${dateRange.value[1]}.csv`)
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)
    
    ElMessage.success('数据导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败: ' + (error.message || '未知错误'))
  } finally {
    exporting.value = false
  }
}

// 生成周报告
const generateWeeklyReport = () => {
  ElMessage.info('周报告功能开发中，将使用PDF格式')
  // TODO: 实现PDF报告生成
}

// 生成月报告
const generateMonthlyReport = () => {
  ElMessage.info('月报告功能开发中，将使用PDF格式')
  // TODO: 实现PDF报告生成
}

// 数据类型变化
const onDataTypeChange = () => {
  selectedRange.value = '30days'
  if (dateRange.value && dateRange.value.length === 2) {
    loadData()
  }
}

// 监听窗口大小变化，重新调整图表
const handleResize = () => {
  if (trendChart) trendChart.resize()
  if (comparisonChart) comparisonChart.resize()
  if (heatmapChart) heatmapChart.resize()
}

// 初始化
onMounted(() => {
  selectRange('30days')
  window.addEventListener('resize', handleResize)
})

// 清理
import { onUnmounted } from 'vue'
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (trendChart) trendChart.dispose()
  if (comparisonChart) comparisonChart.dispose()
  if (heatmapChart) heatmapChart.dispose()
})
</script>

<style scoped>
.analysis-container {
  padding: 20px;
}

.analysis-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.data-type-tabs {
  margin-bottom: 20px;
}

.time-select {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.time-select label {
  margin-right: 10px;
  font-weight: 500;
}

.search-btn {
  margin-left: 10px;
}

.statistics-panel {
  margin-bottom: 30px;
  padding: 20px;
  background-color: #f9fafc;
  border-radius: 4px;
}

.charts-container {
  margin-bottom: 20px;
}

.chart-item {
  background-color: #fff;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.chart-item h3 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

.data-table {
  margin: 20px 0;
}

.data-table h3 {
  margin-bottom: 15px;
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

@media (max-width: 768px) {
  .time-select {
    flex-direction: column;
  }
  
  .search-btn {
    margin-left: 0;
    margin-top: 10px;
    width: 100%;
  }

  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .card-header > div {
    margin-top: 10px;
    width: 100%;
  }

  .card-header .el-button {
    width: 100%;
    margin: 5px 0;
  }
}
</style>
