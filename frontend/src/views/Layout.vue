<template>
  <div class="layout-container">
    <!-- 顶部导航栏 -->
    <el-header class="top-navbar">
      <div class="navbar-left">
        <h1>个人健康管理系统</h1>
      </div>
      <div class="navbar-right">
        <!-- 通知图标 -->
        <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
          <el-button
            type="primary"
            :icon="Bell"
            circle
            @click="goToNotifications"
          />
        </el-badge>
        <span class="user-name">{{ currentUser?.username }}</span>
        <el-dropdown @command="handleCommand">
          <el-button type="primary" size="small">
            {{ currentUser?.username }}
            <el-icon class="el-icon--right">
              <ArrowDown />
            </el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人信息</el-dropdown-item>
              <el-dropdown-item command="logout">登出</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <!-- 主容器 -->
    <el-container>
      <!-- 左侧菜单栏 -->
      <el-aside class="left-sidebar" width="200px">
        <el-menu
          :default-active="activeMenu"
          router
          class="menu"
        >
          <el-menu-item index="/layout/dashboard">
            <el-icon>
              <House />
            </el-icon>
            <span>仪表板</span>
          </el-menu-item>

          <el-sub-menu index="records">
            <template #title>
              <el-icon>
                <Document />
              </el-icon>
              <span>数据记录</span>
            </template>
            <el-menu-item index="/layout/weight-record">体重</el-menu-item>
            <el-menu-item index="/layout/blood-pressure-record">血压</el-menu-item>
            <el-menu-item index="/layout/glucose-record">血糖</el-menu-item>
            <el-menu-item index="/layout/exercise-record">运动</el-menu-item>
            <el-menu-item index="/layout/sleep-record">睡眠</el-menu-item>
            <el-menu-item index="/layout/diet-record">饮食</el-menu-item>
            <el-menu-item index="/layout/mental-health-record">心理健康</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/layout/analysis">
            <el-icon>
              <DataLine />
            </el-icon>
            <span>数据分析</span>
          </el-menu-item>

          <el-sub-menu index="settings-menu">
            <template #title>
              <el-icon>
                <Setting />
              </el-icon>
              <span>设置</span>
            </template>
            <el-menu-item index="/layout/reminders">提醒管理</el-menu-item>
            <el-menu-item index="/layout/alerts">告警管理</el-menu-item>
            <el-menu-item index="/layout/notifications">通知中心</el-menu-item>
            <el-menu-item index="/layout/health-advice">健康建议</el-menu-item>
            <el-menu-item index="/layout/settings">设置</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>

      <!-- 右侧内容区域 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown, House, Document, DataLine, Setting, Bell } from '@element-plus/icons-vue'
import { useAuthStore } from '../store/auth.js'
import { getUnreadCount } from '../api/notification.js'

const router = useRouter()
const authStore = useAuthStore()

const currentUser = computed(() => authStore.getCurrentUser())
const activeMenu = computed(() => router.currentRoute.value.path)
const unreadCount = ref(0)
let refreshTimer = null

// 加载未读通知数
const loadUnreadCount = async () => {
  if (!currentUser.value?.id) return
  
  try {
    const response = await getUnreadCount(currentUser.value.id)
    if (response.code === 200) {
      unreadCount.value = response.data?.unreadCount || 0
    }
  } catch (error) {
    console.error('加载未读通知数失败:', error)
  }
}

// 跳转到通知中心
const goToNotifications = () => {
  router.push('/layout/notifications')
}

const handleCommand = (command) => {
  if (command === 'profile') {
    if (currentUser.value?.id) {
      router.push(`/profile/${currentUser.value.id}`)
    }
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要登出吗?', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      authStore.clearAuth()
      ElMessage.success('登出成功')
      router.push('/')
    }).catch(() => {
      // 用户取消
    })
  }
}

onMounted(() => {
  loadUnreadCount()
  // 每30秒刷新一次未读通知数
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
.layout-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.top-navbar {
  background-color: #546de5;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.navbar-left h1 {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.notification-badge {
  margin-right: 10px;
}

.user-name {
  font-size: 14px;
}

.el-container {
  flex: 1;
}

.left-sidebar {
  background-color: #f5f7fa;
  border-right: 1px solid #ebeef5;
  overflow-y: auto;
}

.menu {
  border-right: none;
}

.main-content {
  background-color: #ffffff;
  overflow-y: auto;
}

:deep(.el-header) {
  padding: 0;
}

:deep(.el-aside) {
  padding: 0;
}

:deep(.el-main) {
  padding: 0;
}
</style>
