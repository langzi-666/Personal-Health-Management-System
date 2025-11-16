<template>
  <div class="alerts-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>告警管理</h2>
          <el-button type="primary" @click="showDialog = true">
            <el-icon><Plus /></el-icon>
            新建告警
          </el-button>
        </div>
      </template>

      <!-- 告警列表 -->
      <el-table :data="alertList" stripe style="width: 100%">
        <el-table-column prop="alertName" label="告警名称" width="200" />
        <el-table-column prop="alertType" label="告警类型" width="150">
          <template #default="{ row }">
            <el-tag>{{ getAlertTypeLabel(row.alertType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="conditionType" label="条件类型" width="150">
          <template #default="{ row }">
            <el-tag type="warning">{{ getConditionTypeLabel(row.conditionType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="thresholdValue" label="阈值" width="120">
          <template #default="{ row }">
            {{ row.thresholdValue }} {{ getUnit(row.alertType) }}
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
              @change="toggleAlert(row.id, row.isEnabled)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="editAlert(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteAlert(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="alertList.length === 0 && !loading" description="暂无告警规则" />
    </el-card>

    <!-- 新建/编辑对话框 -->
    <el-dialog
      v-model="showDialog"
      :title="editingAlert ? '编辑告警' : '新建告警'"
      width="500px"
    >
      <el-form :model="alertForm" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="告警名称" prop="alertName">
          <el-input v-model="alertForm.alertName" placeholder="请输入告警名称" />
        </el-form-item>
        <el-form-item label="告警类型" prop="alertType">
          <el-select v-model="alertForm.alertType" placeholder="请选择告警类型" style="width: 100%" @change="handleAlertTypeChange">
            <el-option label="体重" value="weight" />
            <el-option label="血压" value="blood_pressure" />
            <el-option label="血糖" value="blood_glucose" />
            <el-option label="运动" value="exercise" />
            <el-option label="睡眠" value="sleep" />
          </el-select>
        </el-form-item>
        <el-form-item label="条件类型" prop="conditionType">
          <el-select v-model="alertForm.conditionType" placeholder="请选择条件类型" style="width: 100%">
            <el-option label="高于" value="above" />
            <el-option label="低于" value="below" />
            <el-option label="异常变化" value="abnormal_change" />
          </el-select>
        </el-form-item>
        <el-form-item label="阈值" prop="thresholdValue">
          <el-input-number
            v-model="alertForm.thresholdValue"
            :precision="2"
            :step="0.1"
            :min="0"
            style="width: 100%"
            :placeholder="`请输入阈值（${getUnit(alertForm.alertType)}）`"
          />
        </el-form-item>
        <el-form-item label="通知方式" prop="notificationMethod">
          <el-select v-model="alertForm.notificationMethod" placeholder="请选择通知方式" style="width: 100%">
            <el-option label="应用内通知" value="app" />
            <el-option label="邮件通知" value="email" />
            <el-option label="短信通知" value="sms" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="saveAlert" :loading="saving">保存</el-button>
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
  getAlertList,
  createAlert,
  updateAlert,
  deleteAlert as deleteAlertAPI,
  toggleAlert as toggleAlertAPI
} from '../api/alert.js'

const authStore = useAuthStore()
const userId = computed(() => authStore.getCurrentUser()?.id)

const alertList = ref([])
const loading = ref(false)
const saving = ref(false)
const showDialog = ref(false)
const editingAlert = ref(null)
const formRef = ref(null)

const alertForm = ref({
  alertName: '',
  alertType: '',
  conditionType: '',
  thresholdValue: null,
  notificationMethod: 'app'
})

const rules = {
  alertName: [{ required: true, message: '请输入告警名称', trigger: 'blur' }],
  alertType: [{ required: true, message: '请选择告警类型', trigger: 'change' }],
  conditionType: [{ required: true, message: '请选择条件类型', trigger: 'change' }],
  thresholdValue: [{ required: true, message: '请输入阈值', trigger: 'blur' }],
  notificationMethod: [{ required: true, message: '请选择通知方式', trigger: 'change' }]
}

// 获取告警类型标签
const getAlertTypeLabel = (type) => {
  const labels = {
    weight: '体重',
    blood_pressure: '血压',
    blood_glucose: '血糖',
    exercise: '运动',
    sleep: '睡眠'
  }
  return labels[type] || type
}

// 获取条件类型标签
const getConditionTypeLabel = (type) => {
  const labels = {
    above: '高于',
    below: '低于',
    abnormal_change: '异常变化'
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

// 获取单位
const getUnit = (type) => {
  const units = {
    weight: 'kg',
    blood_pressure: 'mmHg',
    blood_glucose: 'mmol/L',
    exercise: '分钟',
    sleep: '小时'
  }
  return units[type] || ''
}

// 处理告警类型变化
const handleAlertTypeChange = () => {
  alertForm.value.thresholdValue = null
}

// 加载告警列表
const loadAlerts = async () => {
  if (!userId.value) return
  
  loading.value = true
  try {
    const response = await getAlertList(userId.value)
    if (response.code === 200) {
      alertList.value = response.data || []
    }
  } catch (error) {
    ElMessage.error('加载告警列表失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 编辑告警
const editAlert = (alert) => {
  editingAlert.value = alert
  alertForm.value = {
    alertName: alert.alertName,
    alertType: alert.alertType,
    conditionType: alert.conditionType,
    thresholdValue: alert.thresholdValue,
    notificationMethod: alert.notificationMethod || 'app'
  }
  showDialog.value = true
}

// 保存告警
const saveAlert = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    saving.value = true
    try {
      const data = {
        userId: userId.value,
        alertName: alertForm.value.alertName,
        alertType: alertForm.value.alertType,
        conditionType: alertForm.value.conditionType,
        thresholdValue: alertForm.value.thresholdValue,
        notificationMethod: alertForm.value.notificationMethod
      }
      
      if (editingAlert.value) {
        // 更新
        const response = await updateAlert(editingAlert.value.id, data)
        if (response.code === 200) {
          ElMessage.success('更新告警成功')
          showDialog.value = false
          loadAlerts()
        }
      } else {
        // 创建
        const response = await createAlert(data)
        if (response.code === 200) {
          ElMessage.success('创建告警成功')
          showDialog.value = false
          loadAlerts()
        }
      }
      
      // 重置表单
      editingAlert.value = null
      alertForm.value = {
        alertName: '',
        alertType: '',
        conditionType: '',
        thresholdValue: null,
        notificationMethod: 'app'
      }
    } catch (error) {
      ElMessage.error('保存告警失败: ' + (error.message || '未知错误'))
    } finally {
      saving.value = false
    }
  })
}

// 删除告警
const deleteAlert = async (alertId) => {
  try {
    await ElMessageBox.confirm('确定要删除这个告警吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await deleteAlertAPI(alertId)
    if (response.code === 200) {
      ElMessage.success('删除告警成功')
      loadAlerts()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除告警失败: ' + (error.message || '未知错误'))
    }
  }
}

// 切换告警状态
const toggleAlert = async (alertId, enabled) => {
  try {
    const response = await toggleAlertAPI(alertId, enabled)
    if (response.code === 200) {
      ElMessage.success(enabled ? '已启用告警' : '已禁用告警')
    } else {
      // 如果失败，恢复状态
      const alert = alertList.value.find(a => a.id === alertId)
      if (alert) {
        alert.isEnabled = !enabled
      }
    }
  } catch (error) {
    ElMessage.error('操作失败: ' + (error.message || '未知错误'))
    // 恢复状态
    const alert = alertList.value.find(a => a.id === alertId)
    if (alert) {
      alert.isEnabled = !enabled
    }
  }
}

onMounted(() => {
  loadAlerts()
})
</script>

<style scoped>
.alerts-page {
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
