<template>
  <div class="profile-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <h2>个人信息</h2>
          <div class="header-buttons">
            <el-button type="success" @click="goToDashboard">进入系统</el-button>
            <el-button type="primary" @click="editMode = !editMode">
              {{ editMode ? '取消编辑' : '编辑信息' }}
            </el-button>
          </div>
        </div>
      </template>

      <el-form 
        v-if="!editMode" 
        label-width="100px"
      >
        <el-form-item label="用户ID">
          <span>{{ userData.id }}</span>
        </el-form-item>
        <el-form-item label="用户名">
          <span>{{ userData.username }}</span>
        </el-form-item>
        <el-form-item label="邮箱">
          <span>{{ userData.email }}</span>
        </el-form-item>
        <el-form-item label="真实姓名">
          <span>{{ userData.realName || '未设置' }}</span>
        </el-form-item>
        <el-form-item label="性别">
          <span>{{ genderLabel[userData.gender] || '未设置' }}</span>
        </el-form-item>
        <el-form-item label="身高">
          <span>{{ userData.height ? userData.height + ' cm' : '未设置' }}</span>
        </el-form-item>
        <el-form-item label="目标体重">
          <span>{{ userData.weightGoal ? userData.weightGoal + ' kg' : '未设置' }}</span>
        </el-form-item>
        <el-form-item label="创建时间">
          <span>{{ formatDate(userData.createTime) }}</span>
        </el-form-item>
      </el-form>

      <el-form 
        v-else
        ref="formRef"
        :model="userData"
        label-width="100px"
      >
        <el-form-item label="真实姓名">
          <el-input v-model="userData.realName" placeholder="请输入真实姓名"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="userData.gender" placeholder="请选择性别">
            <el-option label="未知" :value="0"></el-option>
            <el-option label="男" :value="1"></el-option>
            <el-option label="女" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="身高 (cm)">
          <el-input-number v-model="userData.height" :step="1" :min="50" :max="250"></el-input-number>
        </el-form-item>
        <el-form-item label="目标体重 (kg)">
          <el-input-number v-model="userData.weightGoal" :step="0.1" :min="20" :max="200"></el-input-number>
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input 
            v-model="userData.bio" 
            type="textarea" 
            :rows="3"
            placeholder="请输入个人简介"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveUserInfo" :loading="saving">保存修改</el-button>
        </el-form-item>
      </el-form>

      <!-- 修改密码部分 -->
      <el-divider></el-divider>

      <h3>修改密码</h3>
      <el-form 
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input 
            v-model="passwordForm.oldPassword" 
            type="password"
            show-password
            placeholder="请输入旧密码"
          ></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password"
            show-password
            placeholder="请输入新密码"
          ></el-input>
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password"
            show-password
            placeholder="请再次输入新密码"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="changePassword" :loading="changingPassword">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../store/auth.js'
import { getUserInfo, updateUserInfo, changePassword as changePasswordAPI } from '../api/auth.js'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const formRef = ref()
const passwordFormRef = ref()

const editMode = ref(false)
const saving = ref(false)
const changingPassword = ref(false)

const genderLabel = {
  0: '未知',
  1: '男',
  2: '女'
}

const userData = ref({
  id: null,
  username: '',
  email: '',
  realName: '',
  gender: 0,
  height: null,
  weightGoal: null,
  bio: '',
  createTime: null
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '新密码长度不少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' }
  ]
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

const goToDashboard = () => {
  router.push('/layout/dashboard')
}

const loadUserInfo = async () => {
  try {
    const userId = route.params.id
    const response = await getUserInfo(userId)
    if (response.code === 200) {
      userData.value = response.data
    } else {
      ElMessage.error('加载用户信息失败')
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败: ' + error.message)
  }
}

const saveUserInfo = async () => {
  if (!formRef.value) return

  saving.value = true
  try {
    const response = await updateUserInfo(userData.value.id, userData.value)
    if (response.code === 200) {
      ElMessage.success('信息更新成功')
      editMode.value = false
    } else {
      ElMessage.error(response.message || '更新失败')
    }
  } catch (error) {
    ElMessage.error('更新失败: ' + error.message)
  } finally {
    saving.value = false
  }
}

const changePassword = async () => {
  if (!passwordFormRef.value) return

  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }

  try {
    await passwordFormRef.value.validate()
  } catch (error) {
    return
  }

  changingPassword.value = true
  try {
    const response = await changePasswordAPI(
      userData.value.id,
      passwordForm.value.oldPassword,
      passwordForm.value.newPassword
    )
    if (response.code === 200) {
      ElMessage.success('密码修改成功，请重新登录')
      authStore.clearAuth()
      // 跳转到登录页
      window.location.href = '/login'
    } else {
      ElMessage.error(response.message || '修改密码失败')
    }
  } catch (error) {
    ElMessage.error('修改密码失败: ' + error.message)
  } finally {
    changingPassword.value = false
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 0 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

h3 {
  margin-top: 20px;
  margin-bottom: 15px;
  color: #333;
}
</style>
