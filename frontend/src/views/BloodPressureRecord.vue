<template>
  <div class="blood-pressure-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <h2>血压记录</h2>
        </div>
      </template>

      <!-- 添加血压表单 -->
      <el-form 
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
        class="add-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="收缩压 (mmHg)" prop="systolic">
              <el-input-number 
                v-model="formData.systolic"
                :step="1"
                :min="50"
                :max="300"
                placeholder="请输入收缩压"
              ></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="舒张压 (mmHg)" prop="diastolic">
              <el-input-number 
                v-model="formData.diastolic"
                :step="1"
                :min="30"
                :max="200"
                placeholder="请输入舒张压"
              ></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="脉搏 (次/分)" prop="pulse">
              <el-input-number 
                v-model="formData.pulse"
                :step="1"
                :min="30"
                :max="200"
                placeholder="请输入脉搏"
              ></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="测量位置" prop="measurementLocation">
              <el-select v-model="formData.measurementLocation" placeholder="请选择测量位置">
                <el-option label="左臂" value="左臂"></el-option>
                <el-option label="右臂" value="右臂"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="记录日期" prop="recordDate">
              <el-date-picker
                v-model="formData.recordDate"
                type="date"
                placeholder="选择日期"
                value-format="YYYY-MM-DD"
              ></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="记录时间" prop="recordTime">
              <el-time-picker
                v-model="formData.recordTime"
                format="HH:mm:ss"
                value-format="HH:mm:ss"
                placeholder="选择时间"
              ></el-time-picker>
            </el-form-item>
          </el-col>
        </el-row>

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
          value-format="YYYY-MM-DD"
        ></el-date-picker>
        <el-button @click="searchRecords" :loading="loading">查询</el-button>
        <el-button @click="resetFilter">重置</el-button>
      </div>

      <!-- 血压记录列表 -->
      <el-table 
        :data="bloodPressureList"
        stripe
        style="width: 100%; margin-top: 20px"
        :loading="loading"
      >
        <el-table-column prop="recordDate" label="日期" width="100"></el-table-column>
        <el-table-column prop="recordTime" label="时间" width="80"></el-table-column>
        <el-table-column prop="systolic" label="收缩压 (mmHg)" width="120"></el-table-column>
        <el-table-column prop="diastolic" label="舒张压 (mmHg)" width="120"></el-table-column>
        <el-table-column prop="pulse" label="脉搏 (次/分)" width="100"></el-table-column>
        <el-table-column prop="status" label="血压状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusColor(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
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
      title="编辑血压记录"
      width="500px"
    >
      <el-form 
        :model="editFormData"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="收缩压 (mmHg)">
              <el-input-number 
                v-model="editFormData.systolic"
                :step="1"
                :min="50"
                :max="300"
              ></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="舒张压 (mmHg)">
              <el-input-number 
                v-model="editFormData.diastolic"
                :step="1"
                :min="30"
                :max="200"
              ></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="脉搏 (次/分)">
              <el-input-number 
                v-model="editFormData.pulse"
                :step="1"
                :min="30"
                :max="200"
              ></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="测量位置">
              <el-select v-model="editFormData.measurementLocation">
                <el-option label="左臂" value="左臂"></el-option>
                <el-option label="右臂" value="右臂"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="记录日期">
              <el-date-picker
                v-model="editFormData.recordDate"
                type="date"
                value-format="YYYY-MM-DD"
              ></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="记录时间">
              <el-time-picker
                v-model="editFormData.recordTime"
                format="HH:mm:ss"
                value-format="HH:mm:ss"
              ></el-time-picker>
            </el-form-item>
          </el-col>
        </el-row>

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
import { addBloodPressure, getBloodPressureList, updateBloodPressure, deleteBloodPressure } from '../api/health.js'

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
const bloodPressureList = ref([])
const dateRange = ref([])

const formData = ref({
  systolic: null,
  diastolic: null,
  pulse: null,
  measurementLocation: '右臂',
  recordDate: new Date().toISOString().split('T')[0],
  recordTime: '12:00:00',
  notes: ''
})

const editFormData = ref({
  id: null,
  systolic: null,
  diastolic: null,
  pulse: null,
  measurementLocation: null,
  recordDate: null,
  recordTime: null,
  notes: ''
})

const rules = {
  systolic: [
    { required: true, message: '请输入收缩压', trigger: 'blur' }
  ],
  diastolic: [
    { required: true, message: '请输入舒张压', trigger: 'blur' }
  ]
}

const getStatusColor = (status) => {
  if (status === '正常') return 'success'
  if (status === '偏高') return 'warning'
  if (status === '高血压') return 'danger'
  return 'info'
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
    const response = await addBloodPressure({
      userId: currentUser.id,
      systolic: formData.value.systolic,
      diastolic: formData.value.diastolic,
      pulse: formData.value.pulse,
      measurementLocation: formData.value.measurementLocation,
      recordDate: formData.value.recordDate,
      recordTime: formData.value.recordTime,
      notes: formData.value.notes
    })
    
    if (response.code === 200) {
      ElMessage.success('血压记录添加成功')
      resetForm()
      loadBloodPressureRecords()
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
    systolic: null,
    diastolic: null,
    pulse: null,
    measurementLocation: '右臂',
    recordDate: new Date().toISOString().split('T')[0],
    recordTime: '12:00:00',
    notes: ''
  }
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

const loadBloodPressureRecords = async () => {
  loading.value = true
  try {
    const startDate = dateRange.value?.[0] || null
    const endDate = dateRange.value?.[1] || null
    const response = await getBloodPressureList(currentUser.id, pageNum.value, pageSize.value, startDate, endDate)
    
    if (response.code === 200) {
      bloodPressureList.value = response.data.records || []
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
  loadBloodPressureRecords()
}

const resetFilter = () => {
  dateRange.value = []
  pageNum.value = 1
  loadBloodPressureRecords()
}

const editRecord = (row) => {
  editFormData.value = {
    id: row.id,
    systolic: row.systolic,
    diastolic: row.diastolic,
    pulse: row.pulse,
    measurementLocation: row.measurementLocation,
    recordDate: row.recordDate,
    recordTime: row.recordTime,
    notes: row.notes
  }
  editDialogVisible.value = true
}

const saveEdit = async () => {
  submitting.value = true
  try {
    const response = await updateBloodPressure(editFormData.value.id, {
      systolic: editFormData.value.systolic,
      diastolic: editFormData.value.diastolic,
      pulse: editFormData.value.pulse,
      measurementLocation: editFormData.value.measurementLocation,
      recordDate: editFormData.value.recordDate,
      recordTime: editFormData.value.recordTime,
      notes: editFormData.value.notes
    })
    
    if (response.code === 200) {
      ElMessage.success('血压记录更新成功')
      editDialogVisible.value = false
      loadBloodPressureRecords()
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
      const response = await deleteBloodPressure(id)
      if (response.code === 200) {
        ElMessage.success('记录删除成功')
        loadBloodPressureRecords()
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
  loadBloodPressureRecords()
})

watch([pageNum, pageSize], () => {
  loadBloodPressureRecords()
})
</script>

<style scoped>
.blood-pressure-container {
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
