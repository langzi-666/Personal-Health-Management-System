<template>
  <div class="dashboard">
    <!-- 今日快捷卡片 -->
    <div class="quick-cards">
      <el-card class="quick-card">
        <template #header>
          <div class="card-title">
            <span>今日摄入卡路里</span>
          </div>
        </template>
        <div class="card-content">
          <div class="big-number">{{ todayData.dietCalories || 0 }}</div>
          <div class="unit">kcal</div>
        </div>
      </el-card>

      <el-card class="quick-card">
        <template #header>
          <div class="card-title">
            <span>今日运动卡路里</span>
          </div>
        </template>
        <div class="card-content">
          <div class="big-number">{{ todayData.exerciseCalories || 0 }}</div>
          <div class="unit">kcal</div>
        </div>
      </el-card>

      <el-card class="quick-card">
        <template #header>
          <div class="card-title">
            <span>睡眠时长</span>
          </div>
        </template>
        <div class="card-content">
          <div class="big-number">{{ todayData.sleepDuration || 0 }}</div>
          <div class="unit">小时</div>
        </div>
      </el-card>

      <el-card class="quick-card">
        <template #header>
          <div class="card-title">
            <span>今日步数</span>
          </div>
        </template>
        <div class="card-content">
          <div class="big-number">-</div>
          <div class="unit">步</div>
        </div>
      </el-card>
    </div>

    <!-- 数据概览卡片 -->
    <div class="overview-cards">
      <el-card class="overview-card">
        <template #header>
          <div class="card-title">
            <span>最新体重</span>
          </div>
        </template>
        <div v-if="latestData.weight" class="overview-content">
          <div class="main-value">{{ latestData.weight.weight }} kg</div>
          <div class="sub-info">
            <span>体脂率: {{ latestData.weight.bodyFatRate || '-' }}%</span>
            <span>记录日期: {{ latestData.weight.recordDate }}</span>
          </div>
        </div>
        <div v-else class="no-data">暂无数据</div>
      </el-card>

      <el-card class="overview-card">
        <template #header>
          <div class="card-title">
            <span>最新血压</span>
          </div>
        </template>
        <div v-if="latestData.bloodPressure" class="overview-content">
          <div class="main-value">{{ latestData.bloodPressure.systolic }}/{{ latestData.bloodPressure.diastolic }}</div>
          <div class="sub-info">
            <el-tag :type="getStatusColor(latestData.bloodPressure.status)">{{ latestData.bloodPressure.status }}</el-tag>
            <span>脉搏: {{ latestData.bloodPressure.pulse }} bpm</span>
          </div>
        </div>
        <div v-else class="no-data">暂无数据</div>
      </el-card>

      <el-card class="overview-card">
        <template #header>
          <div class="card-title">
            <span>最新血糖</span>
          </div>
        </template>
        <div v-if="latestData.bloodGlucose" class="overview-content">
          <div class="main-value">{{ latestData.bloodGlucose.glucoseValue }} mmol/L</div>
          <div class="sub-info">
            <span>测量时间: {{ latestData.bloodGlucose.measurementType }}</span>
            <span>记录时间: {{ latestData.bloodGlucose.recordTime }}</span>
          </div>
        </div>
        <div v-else class="no-data">暂无数据</div>
      </el-card>
    </div>

    <!-- 统计信息 -->
    <div class="stats-section">
      <el-card>
        <template #header>
          <div class="card-title">
            <span>周统计</span>
          </div>
        </template>
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="stat-item">
              <div class="stat-label">周运动卡路里</div>
              <div class="stat-value">{{ weeklyData.weekExerciseCalories || 0 }} kcal</div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="stat-item">
              <div class="stat-label">周平均睡眠</div>
              <div class="stat-value">{{ (weeklyData.avgSleep || 0).toFixed(1) }} 小时</div>
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>

    <!-- 月统计 -->
    <div class="stats-section">
      <el-card>
        <template #header>
          <div class="card-title">
            <span>月统计</span>
          </div>
        </template>
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="stat-item">
              <div class="stat-label">体重变化</div>
              <div v-if="monthlyData.weightChange && monthlyData.weightChange.startWeight">
                <span>{{ monthlyData.weightChange.startWeight }} kg → {{ monthlyData.weightChange.endWeight }} kg</span>
              </div>
              <div v-else>暂无数据</div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="stat-item">
              <div class="stat-label">平均血压</div>
              <div v-if="monthlyData.avgBloodPressure && monthlyData.avgBloodPressure.avgSystolic">
                <span>{{ monthlyData.avgBloodPressure.avgSystolic.toFixed(0) }}/{{ monthlyData.avgBloodPressure.avgDiastolic.toFixed(0) }} mmHg</span>
              </div>
              <div v-else>暂无数据</div>
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>

    <!-- 快速操作 -->
    <div class="quick-actions">
      <el-card>
        <template #header>
          <div class="card-title">
            <span>快速操作</span>
          </div>
        </template>
        <el-row :gutter="20">
          <el-col :span="4">
            <el-button type="primary" block @click="goToRecord('weight-record')">记录体重</el-button>
          </el-col>
          <el-col :span="4">
            <el-button type="primary" block @click="goToRecord('blood-pressure-record')">记录血压</el-button>
          </el-col>
          <el-col :span="4">
            <el-button type="primary" block @click="goToRecord('glucose-record')">记录血糖</el-button>
          </el-col>
          <el-col :span="4">
            <el-button type="primary" block @click="goToRecord('exercise-record')">记录运动</el-button>
          </el-col>
          <el-col :span="4">
            <el-button type="primary" block @click="goToRecord('sleep-record')">记录睡眠</el-button>
          </el-col>
          <el-col :span="4">
            <el-button type="primary" block @click="goToRecord('diet-record')">记录饮食</el-button>
          </el-col>
        </el-row>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { ElMessage } from 'element-plus'
import request from '../api/request'

const router = useRouter()
const authStore = useAuthStore()
const currentUser = computed(() => authStore.getCurrentUser())

const latestData = ref({
  weight: null,
  bloodPressure: null,
  bloodGlucose: null
})

const todayData = ref({
  dietCalories: 0,
  exerciseCalories: 0,
  sleepDuration: 0
})

const weeklyData = ref({
  weekExerciseCalories: 0,
  avgSleep: 0
})

const monthlyData = ref({
  weightChange: null,
  avgBloodPressure: null
})

const getStatusColor = (status) => {
  if (status === '正常') return 'success'
  if (status === '偏高') return 'warning'
  if (status === '高血压') return 'danger'
  return 'info'
}

const loadDashboardData = async () => {
  if (!currentUser.value?.id) {
    ElMessage.warning('请先登录')
    return
  }

  try {
    // 暂时使用模拟数据，等待后端接口完善
    latestData.value = {
      weight: {
        weight: 75.5,
        bodyFatRate: 20.3,
        recordDate: '2024-01-15'
      },
      bloodPressure: {
        systolic: 120,
        diastolic: 80,
        pulse: 72,
        status: '正常'
      },
      bloodGlucose: {
        glucoseValue: 5.6,
        measurementType: '空腹',
        recordTime: '08:00'
      }
    }

    todayData.value = {
      dietCalories: 1850,
      exerciseCalories: 450,
      sleepDuration: 7.5
    }

    weeklyData.value = {
      weekExerciseCalories: 2800,
      avgSleep: 7.2
    }

    monthlyData.value = {
      weightChange: {
        startWeight: 78.0,
        endWeight: 75.5
      },
      avgBloodPressure: {
        avgSystolic: 118,
        avgDiastolic: 78
      }
    }
  } catch (error) {
    console.error('加载仪表板数据失败:', error)
    ElMessage.error('加载仪表板数据失败')
  }
}

const goToRecord = (path) => {
  router.push(`/layout/${path}`)
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
  background: #f5f7fa;
}

.quick-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.quick-card {
  background: white;
}

.card-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-content {
  text-align: center;
  padding: 20px 0;
}

.big-number {
  font-size: 32px;
  font-weight: bold;
  color: #546de5;
  margin-bottom: 5px;
}

.unit {
  color: #909399;
  font-size: 14px;
}

.overview-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.overview-card {
  background: white;
}

.overview-content {
  padding: 15px 0;
}

.main-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.sub-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  color: #909399;
  font-size: 12px;
}

.no-data {
  color: #909399;
  text-align: center;
  padding: 20px;
}

.stats-section {
  margin-bottom: 20px;
}

.stat-item {
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
}

.stat-label {
  color: #909399;
  font-size: 12px;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.quick-actions {
  margin-bottom: 20px;
}
</style>
