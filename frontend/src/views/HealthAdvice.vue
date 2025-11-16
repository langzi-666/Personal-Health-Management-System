<template>
  <div class="health-advice-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>健康建议</h2>
          <el-button @click="loadAdvice" :loading="loading">
            <el-icon><Refresh /></el-icon>
            刷新建议
          </el-button>
        </div>
      </template>

      <div v-if="adviceList.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无健康建议，请先记录一些健康数据" />
      </div>

      <div v-else class="advice-list">
        <el-collapse v-model="activeNames">
          <el-collapse-item
            v-for="(advice, index) in sortedAdviceList"
            :key="index"
            :name="index"
          >
            <template #title>
              <div class="advice-header">
                <el-tag :type="getAdviceTypeTag(advice.type)" size="large">
                  {{ advice.category }}
                </el-tag>
                <span class="advice-title">{{ advice.title }}</span>
                <el-tag :type="getPriorityTag(advice.priority)" size="small" style="margin-left: auto">
                  {{ getPriorityLabel(advice.priority) }}
                </el-tag>
              </div>
            </template>
            <div class="advice-content">
              <p>{{ advice.content }}</p>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>

      <div v-if="generateTime" class="generate-time">
        建议生成时间：{{ generateTime }}
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { useAuthStore } from '../store/auth.js'
import { getHealthAdvice } from '../api/health.js'

const authStore = useAuthStore()
const userId = computed(() => authStore.getCurrentUser()?.id)

const adviceList = ref([])
const loading = ref(false)
const activeNames = ref([])
const generateTime = ref('')

// 按优先级排序的建议列表
const sortedAdviceList = computed(() => {
  return [...adviceList.value].sort((a, b) => {
    // 优先级数字越小，优先级越高
    return (a.priority || 0) - (b.priority || 0)
  })
})

// 获取建议类型标签
const getAdviceTypeTag = (type) => {
  const tags = {
    success: 'success',
    warning: 'warning',
    info: 'info',
    danger: 'danger'
  }
  return tags[type] || 'info'
}

// 获取优先级标签
const getPriorityTag = (priority) => {
  if (priority === 0) return 'success'
  if (priority === 1) return 'info'
  if (priority === 2) return 'warning'
  if (priority >= 3) return 'danger'
  return 'info'
}

// 获取优先级标签文本
const getPriorityLabel = (priority) => {
  if (priority === 0) return '低优先级'
  if (priority === 1) return '一般'
  if (priority === 2) return '重要'
  if (priority >= 3) return '紧急'
  return '一般'
}

// 加载健康建议
const loadAdvice = async () => {
  if (!userId.value) {
    ElMessage.warning('请先登录')
    return
  }
  
  loading.value = true
  try {
    const response = await getHealthAdvice(userId.value)
    if (response.code === 200) {
      adviceList.value = response.data?.adviceList || []
      generateTime.value = response.data?.generateTime || ''
      
      // 默认展开前3个
      activeNames.value = [0, 1, 2].filter(i => i < adviceList.value.length)
      
      if (adviceList.value.length === 0) {
        ElMessage.info('暂无健康建议')
      } else {
        ElMessage.success(`已生成 ${adviceList.value.length} 条健康建议`)
      }
    }
  } catch (error) {
    ElMessage.error('加载健康建议失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadAdvice()
})
</script>

<style scoped>
.health-advice-page {
  padding: 20px;
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

.empty-state {
  padding: 40px 0;
}

.advice-list {
  margin-bottom: 20px;
}

.advice-header {
  display: flex;
  align-items: center;
  gap: 15px;
  width: 100%;
}

.advice-title {
  font-weight: 600;
  font-size: 16px;
  flex: 1;
}

.advice-content {
  padding: 10px 0;
  color: #606266;
  line-height: 1.8;
}

.generate-time {
  text-align: center;
  color: #909399;
  font-size: 12px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

:deep(.el-collapse-item__header) {
  padding: 15px 20px;
}

:deep(.el-collapse-item__content) {
  padding: 0 20px 15px 20px;
}
</style>

