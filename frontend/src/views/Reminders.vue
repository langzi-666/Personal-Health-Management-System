<template>
  <div class="reminders-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>提醒管理</h2>
          <el-button type="primary" @click="showDialog = true">
            <el-icon><Plus /></el-icon>
            新建提醒
          </el-button>
        </div>
      </template>

      <!-- 提醒列表 -->
      <el-table :data="reminderList" stripe style="width: 100%">
        <el-table-column prop="reminderName" label="提醒名称" width="200" />
        <el-table-column prop="reminderType" label="提醒类型" width="150">
          <template #default="{ row }">
            <el-tag>{{ getReminderTypeLabel(row.reminderType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reminderTime" label="提醒时间" width="120" />
        <el-table-column prop="frequency" label="频率" width="150">
          <template #default="{ row }">
            <el-tag type="info">{{ row.frequency || '每天' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="notificationMethod" label="通知方式" width="120">
          <template #default="{ row }">
            <el-tag type="success">{{ getNotificationMethodLabel(row.notificationMethod) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isEnabled" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.isEnabled"
              @change="toggleReminder(row.id, row.isEnabled)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="editReminder(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteReminder(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="reminderList.length === 0 && !loading" description="暂无提醒规则" />
    </el-card>

    <!-- 新建/编辑对话框 -->
    <el-dialog
      v-model="showDialog"
      :title="editingReminder ? '编辑提醒' : '新建提醒'"
      width="500px"
    >
      <el-form :model="reminderForm" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="提醒名称" prop="reminderName">
          <el-input v-model="reminderForm.reminderName" placeholder="请输入提醒名称" />
        </el-form-item>
        <el-form-item label="提醒类型" prop="reminderType">
          <el-select v-model="reminderForm.reminderType" placeholder="请选择提醒类型" style="width: 100%">
            <el-option label="数据记录" value="data_record" />
            <el-option label="运动提醒" value="exercise" />
            <el-option label="饮水提醒" value="water" />
            <el-option label="睡眠提醒" value="sleep" />
            <el-option label="用药提醒" value="medication" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="提醒时间" prop="reminderTime">
          <el-time-picker
            v-model="reminderForm.reminderTime"
            format="HH:mm:ss"
            value-format="HH:mm:ss"
            placeholder="选择提醒时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="频率" prop="frequency">
          <el-select v-model="reminderForm.frequency" placeholder="请选择频率" style="width: 100%">
            <el-option label="每天" value="daily" />
            <el-option label="每周一" value="monday" />
            <el-option label="每周二" value="tuesday" />
            <el-option label="每周三" value="wednesday" />
            <el-option label="每周四" value="thursday" />
            <el-option label="每周五" value="friday" />
            <el-option label="每周六" value="saturday" />
            <el-option label="每周日" value="sunday" />
            <el-option label="工作日" value="weekday" />
            <el-option label="周末" value="weekend" />
          </el-select>
        </el-form-item>
        <el-form-item label="通知方式" prop="notificationMethod">
          <el-select v-model="reminderForm.notificationMethod" placeholder="请选择通知方式" style="width: 100%">
            <el-option label="应用内通知" value="app" />
            <el-option label="邮件通知" value="email" />
            <el-option label="短信通知" value="sms" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="saveReminder" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useAuthStore } from '../store/auth.js'
import {
  getReminderList,
  createReminder,
  updateReminder,
  deleteReminder as deleteReminderAPI,
  toggleReminder as toggleReminderAPI
} from '../api/reminder.js'

const authStore = useAuthStore()
const userId = computed(() => authStore.getCurrentUser()?.id)

const reminderList = ref([])
const loading = ref(false)
const saving = ref(false)
const showDialog = ref(false)
const editingReminder = ref(null)
const formRef = ref(null)

const reminderForm = ref({
  reminderName: '',
  reminderType: '',
  reminderTime: '',
  frequency: 'daily',
  notificationMethod: 'app'
})

const rules = {
  reminderName: [{ required: true, message: '请输入提醒名称', trigger: 'blur' }],
  reminderType: [{ required: true, message: '请选择提醒类型', trigger: 'change' }],
  reminderTime: [{ required: true, message: '请选择提醒时间', trigger: 'change' }],
  frequency: [{ required: true, message: '请选择频率', trigger: 'change' }],
  notificationMethod: [{ required: true, message: '请选择通知方式', trigger: 'change' }]
}

// 获取提醒类型标签
const getReminderTypeLabel = (type) => {
  const labels = {
    data_record: '数据记录',
    exercise: '运动提醒',
    water: '饮水提醒',
    sleep: '睡眠提醒',
    medication: '用药提醒',
    other: '其他'
  }
  return labels[type] || type
}

// 获取通知方式标签
const getNotificationMethodLabel = (method) => {
  const labels = {
    app: '应用内通知',
    email: '邮件通知',
    sms: '短信通知'
  }
  return labels[method] || method
}

// 加载提醒列表
const loadReminders = async () => {
  if (!userId.value) return
  
  loading.value = true
  try {
    const response = await getReminderList(userId.value)
    if (response.code === 200) {
      reminderList.value = response.data || []
    }
  } catch (error) {
    ElMessage.error('加载提醒列表失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 编辑提醒
const editReminder = (reminder) => {
  editingReminder.value = reminder
  reminderForm.value = {
    reminderName: reminder.reminderName,
    reminderType: reminder.reminderType,
    reminderTime: reminder.reminderTime,
    frequency: reminder.frequency || 'daily',
    notificationMethod: reminder.notificationMethod || 'app'
  }
  showDialog.value = true
}

// 保存提醒
const saveReminder = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    saving.value = true
    try {
      const data = {
        userId: userId.value,
        reminderName: reminderForm.value.reminderName,
        reminderType: reminderForm.value.reminderType,
        reminderTime: reminderForm.value.reminderTime,
        frequency: reminderForm.value.frequency,
        notificationMethod: reminderForm.value.notificationMethod
      }
      
      if (editingReminder.value) {
        // 更新
        const response = await updateReminder(editingReminder.value.id, data)
        if (response.code === 200) {
          ElMessage.success('更新提醒成功')
          showDialog.value = false
          loadReminders()
        }
      } else {
        // 创建
        const response = await createReminder(data)
        if (response.code === 200) {
          ElMessage.success('创建提醒成功')
          showDialog.value = false
          loadReminders()
        }
      }
      
      // 重置表单
      editingReminder.value = null
      reminderForm.value = {
        reminderName: '',
        reminderType: '',
        reminderTime: '',
        frequency: 'daily',
        notificationMethod: 'app'
      }
    } catch (error) {
      ElMessage.error('保存提醒失败: ' + (error.message || '未知错误'))
    } finally {
      saving.value = false
    }
  })
}

// 删除提醒
const deleteReminder = async (reminderId) => {
  try {
    await ElMessageBox.confirm('确定要删除这个提醒吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await deleteReminderAPI(reminderId)
    if (response.code === 200) {
      ElMessage.success('删除提醒成功')
      loadReminders()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除提醒失败: ' + (error.message || '未知错误'))
    }
  }
}

// 切换提醒状态
const toggleReminder = async (reminderId, enabled) => {
  try {
    const response = await toggleReminderAPI(reminderId, enabled)
    if (response.code === 200) {
      ElMessage.success(enabled ? '已启用提醒' : '已禁用提醒')
    } else {
      // 如果失败，恢复状态
      const reminder = reminderList.value.find(r => r.id === reminderId)
      if (reminder) {
        reminder.isEnabled = !enabled
      }
    }
  } catch (error) {
    ElMessage.error('操作失败: ' + (error.message || '未知错误'))
    // 恢复状态
    const reminder = reminderList.value.find(r => r.id === reminderId)
    if (reminder) {
      reminder.isEnabled = !enabled
    }
  }
}

// 监听对话框关闭
const handleDialogClose = () => {
  editingReminder.value = null
  reminderForm.value = {
    reminderName: '',
    reminderType: '',
    reminderTime: '',
    frequency: 'daily',
    notificationMethod: 'app'
  }
}

onMounted(() => {
  loadReminders()
})
</script>

<style scoped>
.reminders-page {
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

:deep(.el-table) {
  margin-top: 20px;
}
</style>
