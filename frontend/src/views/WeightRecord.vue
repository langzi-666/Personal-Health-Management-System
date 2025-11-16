<template>
  <div class="weight-record-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <h2>体重记录</h2>
        </div>
      </template>

      <!-- 添加体重表单 -->
      <el-form 
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
        class="add-form"
      >
        <el-form-item label="体重 (kg)" prop="weight">
          <el-input-number 
            v-model="formData.weight"
            :step="0.1"
            :min="20"
            :max="200"
            placeholder="请输入体重"
          ></el-input-number>
        </el-form-item>

        <el-form-item label="体脂率 (%)" prop="bodyFatRate">
          <el-input-number 
            v-model="formData.bodyFatRate"
            :step="0.1"
            :min="0"
            :max="100"
            placeholder="请输入体脂率（可选）"
          ></el-input-number>
        </el-form-item>

        <el-form-item label="记录日期" prop="recordDate">
          <el-date-picker
            v-model="formData.recordDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
          ></el-date-picker>
        </el-form-item>

        <el-form-item label="备注" prop="notes">
          <el-input 
            v-model="formData.notes"
            type="textarea"
            :rows="2"
            placeholder="输入备注（可选）"
          ></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitting">添加记录</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>

      <el-divider></el-divider>

      <!-- 日期范围过滤 -->
      <div class="filter-section">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="handleDateChange"
          value-format="YYYY-MM-DD"
        ></el-date-picker>
        <el-button @click="searchRecords" :loading="loading">查询</el-button>
        <el-button @click="resetFilter">重置</el-button>
      </div>

      <!-- 体重记录列表 -->
      <el-table 
        :data="weightList"
        stripe
        style="width: 100%; margin-top: 20px"
        :loading="loading"
      >
        <el-table-column prop="recordDate" label="日期" width="120"></el-table-column>
        <el-table-column prop="weight" label="体重 (kg)" width="100"></el-table-column>
        <el-table-column prop="bodyFatRate" label="体脂率 (%)" width="100"></el-table-column>
        <el-table-column prop="notes" label="备注" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="editRecord(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteRecord(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        :current-page="pageNum"
        :page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; text-align: right"
        @current-change="pageNum = $event"
        @size-change="pageSize = $event"
      ></el-pagination>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑体重记录"
      width="400px"
    >
      <el-form 
        :model="editFormData"
        label-width="100px"
      >
        <el-form-item label="体重 (kg)">
          <el-input-number 
            v-model="editFormData.weight"
            :step="0.1"
            :min="20"
            :max="200"
          ></el-input-number>
        </el-form-item>

        <el-form-item label="体脂率 (%)">
          <el-input-number 
            v-model="editFormData.bodyFatRate"
            :step="0.1"
            :min="0"
            :max="100"
          ></el-input-number>
        </el-form-item>

        <el-form-item label="记录日期">
          <el-date-picker
            v-model="editFormData.recordDate"
            type="date"
            value-format="YYYY-MM-DD"
          ></el-date-picker>
        </el-form-item>

        <el-form-item label="备注">
          <el-input 
            v-model="editFormData.notes"
            type="textarea"
            :rows="2"
          ></el-input>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEdit" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '../store/auth.js'
import { addWeight, getWeightList, updateWeight, deleteWeight } from '../api/health.js'

const authStore = useAuthStore()
const currentUser = authStore.getCurrentUser()

if (!currentUser) {
  throw new Error('未登录')
}

const formRef = ref()
const loading = ref(false)
const submitting = ref(false)
const editDialogVisible = ref(false)

const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const weightList = ref([])
const dateRange = ref([])

const formData = ref({
  weight: null,
  bodyFatRate: null,
  recordDate: new Date().toISOString().split('T')[0],
  notes: ''
})

const editFormData = ref({
  id: null,
  weight: null,
  bodyFatRate: null,
  recordDate: null,
  notes: ''
})

const rules = {
  weight: [
    { required: true, message: '请输入体重', trigger: 'blur' }
  ]
}

const submitForm = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
  } catch (error) {
    return
  }

  submitting.value = true
  try {
    const response = await addWeight({
      userId: currentUser.id,
      weight: formData.value.weight,
      bodyFatRate: formData.value.bodyFatRate,
      recordDate: formData.value.recordDate,
      notes: formData.value.notes
    })
    
    if (response.code === 200) {
      ElMessage.success('体重记录添加成功')
      resetForm()
      loadWeightRecords()
    } else {
      ElMessage.error(response.message || '添加失败')
    }
  } catch (error) {
    ElMessage.error('添加失败: ' + error.message)
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  formData.value = {
    weight: null,
    bodyFatRate: null,
    recordDate: new Date().toISOString().split('T')[0],
    notes: ''
  }
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

const loadWeightRecords = async () => {
  loading.value = true
  try {
    const startDate = dateRange.value?.[0] || null
    const endDate = dateRange.value?.[1] || null
    const response = await getWeightList(currentUser.id, pageNum.value, pageSize.value, startDate, endDate)
    
    if (response.code === 200) {
      weightList.value = response.data.records || []
      total.value = response.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载数据失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

const searchRecords = () => {
  pageNum.value = 1
  loadWeightRecords()
}

const resetFilter = () => {
  dateRange.value = []
  pageNum.value = 1
  loadWeightRecords()
}

const handleDateChange = () => {
  // 日期变化时可以自动查询
}

const editRecord = (row) => {
  editFormData.value = {
    id: row.id,
    weight: row.weight,
    bodyFatRate: row.bodyFatRate,
    recordDate: row.recordDate,
    notes: row.notes
  }
  editDialogVisible.value = true
}

const saveEdit = async () => {
  submitting.value = true
  try {
    const response = await updateWeight(editFormData.value.id, {
      weight: editFormData.value.weight,
      bodyFatRate: editFormData.value.bodyFatRate,
      recordDate: editFormData.value.recordDate,
      notes: editFormData.value.notes
    })
    
    if (response.code === 200) {
      ElMessage.success('体重记录更新成功')
      editDialogVisible.value = false
      loadWeightRecords()
    } else {
      ElMessage.error(response.message || '更新失败')
    }
  } catch (error) {
    ElMessage.error('更新失败: ' + error.message)
  } finally {
    submitting.value = false
  }
}

const deleteRecord = (id) => {
  ElMessageBox.confirm('确定要删除此记录吗?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await deleteWeight(id)
      if (response.code === 200) {
        ElMessage.success('记录删除成功')
        loadWeightRecords()
      } else {
        ElMessage.error(response.message || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败: ' + error.message)
    }
  }).catch(() => {
    // 用户取消
  })
}

onMounted(() => {
  loadWeightRecords()
})

watch([pageNum, pageSize], () => {
  loadWeightRecords()
})
</script>

<style scoped>
.weight-record-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.add-form {
  background-color: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
}

.filter-section {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}
</style>
