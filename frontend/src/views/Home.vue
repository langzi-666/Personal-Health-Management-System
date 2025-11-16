<template>
  <div class="home-container">
    <el-card class="box-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="title" v-if="isLoggedIn">欢迎, {{ currentUser?.username }}!</span>
          <span class="title" v-else>欢迎使用个人健康管理系统</span>
          <el-button v-if="isLoggedIn" type="danger" @click="handleLogout" size="small">登出</el-button>
        </div>
      </template>
      
      <div class="content">
        <template v-if="isLoggedIn">
          <el-button type="primary" @click="goToProfile">个人信息</el-button>
          <el-button @click="checkHealth" type="success">检查系统状态</el-button>
        </template>
        <template v-else>
          <el-button type="primary" @click="goToLogin">登录</el-button>
          <el-button @click="goToRegister">注册</el-button>
          <el-button @click="checkHealth" type="success">检查系统状态</el-button>
        </template>
      </div>

      <div v-if="isLoggedIn" class="user-info">
        <h3>用户信息</h3>
        <p>用户名: {{ currentUser?.username }}</p>
        <p>邮箱: {{ currentUser?.email }}</p>
        <p>真实姓名: {{ currentUser?.realName || '未设置' }}</p>
      </div>

      <div v-if="systemInfo" class="system-info">
        <h3>系统信息</h3>
        <p>系统名称: {{ systemInfo.name }}</p>
        <p>版本: {{ systemInfo.version }}</p>
        <p>Java版本: {{ systemInfo.javaVersion }}</p>
        <p>操作系统: {{ systemInfo.osName }} {{ systemInfo.osVersion }}</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '../store/auth.js'
import request from '../api/request.js'

const router = useRouter()
const authStore = useAuthStore()
const systemInfo = ref(null)

const isLoggedIn = computed(() => authStore.isLoggedIn())
const currentUser = computed(() => authStore.getCurrentUser())

const goToLogin = () => {
  router.push('/login')
}

const goToRegister = () => {
  router.push('/register')
}

const goToDashboard = () => {
  router.push('/layout/dashboard')
}

const goToProfile = () => {
  if (currentUser.value?.id) {
    router.push(`/profile/${currentUser.value.id}`)
  }
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要登出吗?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    authStore.clearAuth()
    ElMessage.success('登出成功')
    router.push('/')
  }).catch(() => {
    // 用户取消操作
  })
}

const checkHealth = async () => {
  try {
    const response = await request.get('/health')
    ElMessage.success('系统正常运行！')
  } catch (error) {
    ElMessage.error('系统检查失败：' + error.message)
  }
}

const getSystemInfo = async () => {
  try {
    const response = await request.get('/system/info')
    if (response.code === 200) {
      systemInfo.value = response.data
    }
  } catch (error) {
    console.error('获取系统信息失败:', error)
  }
}

onMounted(() => {
  getSystemInfo()
  // 如果已登录，重定向到仪表板
  if (isLoggedIn.value) {
    setTimeout(() => {
      router.replace('/layout/dashboard')
    }, 500)
  }
})
</script>

<style scoped>
.home-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.box-card {
  width: 500px;
  margin: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.content {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  justify-content: center;
}

.user-info {
  margin: 20px 0;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.user-info h3 {
  margin: 0 0 10px 0;
  color: #333;
}

.user-info p {
  margin: 5px 0;
  font-size: 14px;
  color: #666;
}

.system-info {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.system-info h3 {
  margin: 0 0 10px 0;
  color: #333;
}

.system-info p {
  margin: 5px 0;
  font-size: 14px;
  color: #666;
}
</style>
