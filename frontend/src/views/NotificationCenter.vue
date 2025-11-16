<template>
  <div class="notification-center">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>通知中心</h2>
          <div class="header-actions">
            <el-button @click="markAllAsRead" :disabled="unreadCount === 0">
              全部标记为已读
            </el-button>
            <el-button @click="refreshNotifications" :loading="loading">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <!-- 过滤栏 -->
      <div class="filter-bar">
        <el-radio-group v-model="filterType" @change="handleFilterChange">
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button label="reminder">提醒</el-radio-button>
          <el-radio-button label="alert">告警</el-radio-button>
          <el-radio-button label="advice">建议</el-radio-button>
        </el-radio-group>
        <el-radio-group v-model="filterReadStatus" @change="handleFilterChange" style="margin-left: 20px">
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button label="unread">未读</el-radio-button>
          <el-radio-button label="read">已读</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 通知列表 -->
      <div class="notification-list">
        <div
          v-for="notification in notificationList"
          :key="notification.id"
          :class="['notification-item', { 'unread': !notification.isRead }]"
          @click="markAsRead(notification.id)"
        >
          <div class="notification-icon">
            <el-icon :size="24" :color="getNotificationIconColor(notification.notificationType)">
              <component :is="getNotificationIcon(notification.notificationType)" />
            </el-icon>
          </div>
          <div class="notification-content">
            <div class="notification-title">
              <span>{{ notification.title }}</span>
              <el-tag :type="getNotificationTagType(notification.notificationType)" size="small">
                {{ getNotificationTypeLabel(notification.notificationType) }}
              </el-tag>
            </div>
            <div class="notification-text">{{ notification.content }}</div>
            <div class="notification-time">{{ formatTime(notification.createTime) }}</div>
          </div>
          <div class="notification-actions">
            <el-button
              type="danger"
              size="small"
              text
              @click.stop="deleteNotification(notification.id)"
            >
              删除
            </el-button>
          </div>
        </div>
        <el-empty v-if="notificationList.length === 0 && !loading" description="暂无通知" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Bell, Warning, InfoFilled, CircleCheck } from '@element-plus/icons-vue'
import { useAuthStore } from '../store/auth.js'
import {
  getNotificationList,
  getUnreadCount,
  markAsRead as markAsReadAPI,
  markAllAsRead as markAllAsReadAPI,
  deleteNotification as deleteNotificationAPI,
  getNotificationsByType,
  getNotificationsByReadStatus
} from '../api/notification.js'

const authStore = useAuthStore()
const userId = computed(() => authStore.getCurrentUser()?.id)

const notificationList = ref([])
const loading = ref(false)
const unreadCount = ref(0)
const filterType = ref('all')
const filterReadStatus = ref('all')
let refreshTimer = null

// 获取通知类型标签
const getNotificationTypeLabel = (type) => {
  const labels = {
    reminder: '提醒',
    alert: '告警',
    advice: '建议'
  }
  return labels[type] || type
}

// 获取通知图标
const getNotificationIcon = (type) => {
  const icons = {
    reminder: Bell,
    alert: Warning,
    advice: InfoFilled
  }
  return icons[type] || Bell
}

// 获取通知图标颜色
const getNotificationIconColor = (type) => {
  const colors = {
    reminder: '#409EFF',
    alert: '#F56C6C',
    advice: '#67C23A'
  }
  return colors[type] || '#909399'
}

// 获取通知标签类型
const getNotificationTagType = (type) => {
  const types = {
    reminder: 'primary',
    alert: 'danger',
    advice: 'success'
  }
  return types[type] || 'info'
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString()
}

// 加载通知列表
const loadNotifications = async () => {
  if (!userId.value) return
  
  loading.value = true
  try {
    let response
    if (filterType.value === 'all' && filterReadStatus.value === 'all') {
      response = await getNotificationList(userId.value, 1, 50)
    } else if (filterType.value !== 'all' && filterReadStatus.value === 'all') {
      response = await getNotificationsByType(userId.value, filterType.value)
    } else if (filterType.value === 'all' && filterReadStatus.value !== 'all') {
      const isRead = filterReadStatus.value === 'read'
      response = await getNotificationsByReadStatus(userId.value, isRead)
    } else {
      // 两个过滤条件都有，先按类型过滤，再按已读状态过滤
      const typeResponse = await getNotificationsByType(userId.value, filterType.value)
      if (typeResponse.code === 200) {
        const isRead = filterReadStatus.value === 'read'
        notificationList.value = (typeResponse.data || []).filter(n => n.isRead === isRead)
        loading.value = false
        return
      }
      response = typeResponse
    }
    
    if (response.code === 200) {
      notificationList.value = response.data || []
    }
  } catch (error) {
    ElMessage.error('加载通知列表失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 加载未读数量
const loadUnreadCount = async () => {
  if (!userId.value) return
  
  try {
    const response = await getUnreadCount(userId.value)
    if (response.code === 200) {
      unreadCount.value = response.data?.unreadCount || 0
    }
  } catch (error) {
    console.error('加载未读数量失败:', error)
  }
}

// 标记为已读
const markAsRead = async (notificationId) => {
  try {
    const response = await markAsReadAPI(notificationId)
    if (response.code === 200) {
      const notification = notificationList.value.find(n => n.id === notificationId)
      if (notification) {
        notification.isRead = true
      }
      loadUnreadCount()
    }
  } catch (error) {
    ElMessage.error('标记失败: ' + (error.message || '未知错误'))
  }
}

// 全部标记为已读
const markAllAsRead = async () => {
  if (!userId.value) return
  
  try {
    const response = await markAllAsReadAPI(userId.value)
    if (response.code === 200) {
      ElMessage.success('已全部标记为已读')
      notificationList.value.forEach(n => {
        n.isRead = true
      })
      loadUnreadCount()
    }
  } catch (error) {
    ElMessage.error('操作失败: ' + (error.message || '未知错误'))
  }
}

// 删除通知
const deleteNotification = async (notificationId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条通知吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await deleteNotificationAPI(notificationId)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      notificationList.value = notificationList.value.filter(n => n.id !== notificationId)
      loadUnreadCount()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + (error.message || '未知错误'))
    }
  }
}

// 刷新通知
const refreshNotifications = () => {
  loadNotifications()
  loadUnreadCount()
}

// 处理过滤变化
const handleFilterChange = () => {
  loadNotifications()
}

onMounted(() => {
  loadNotifications()
  loadUnreadCount()
  
  // 每30秒刷新一次未读数量
  refreshTimer = setInterval(() => {
    loadUnreadCount()
  }, 30000)
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
})
</script>

<style scoped>
.notification-center {
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

.header-actions {
  display: flex;
  gap: 10px;
}

.filter-bar {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.notification-list {
  max-height: 600px;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  padding: 15px;
  margin-bottom: 10px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.notification-item:hover {
  background-color: #f5f7fa;
  border-color: #c0c4cc;
}

.notification-item.unread {
  background-color: #ecf5ff;
  border-color: #b3d8ff;
}

.notification-icon {
  margin-right: 15px;
  display: flex;
  align-items: center;
}

.notification-content {
  flex: 1;
}

.notification-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
  font-weight: 600;
  font-size: 16px;
}

.notification-text {
  color: #606266;
  margin-bottom: 8px;
  line-height: 1.5;
}

.notification-time {
  color: #909399;
  font-size: 12px;
}

.notification-actions {
  display: flex;
  align-items: center;
  margin-left: 15px;
}
</style>
