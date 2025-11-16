<template>
  <div class="mental-health-record">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>心理健康记录</span>
        </div>
      </template>

      <!-- 添加表单 -->
      <el-form :model="formData" label-width="100px" class="form-box">
        <el-form-item label="心情评分(1-10)">
          <el-slider v-model="formData.moodScore" :min="1" :max="10" show-stops />
        </el-form-item>
        <el-form-item label="压力等级">
          <el-select v-model="formData.stressLevel" placeholder="请选择">
            <el-option label="低" value="低" />
            <el-option label="中" value="中" />
            <el-option label="高" value="高" />
          </el-select>
        </el-form-item>
        <el-form-item label="焦虑程度(1-10)">
          <el-slider v-model="formData.anxietyLevel" :min="1" :max="10" show-stops />
        </el-form-item>
        <el-form-item label="心理状态">
          <el-select v-model="formData.mentalsState" placeholder="请选择主要心理状态">
            <el-option label="开心" value="开心" />
            <el-option label="平静" value="平静" />
            <el-option label="沮丧" value="沮丧" />
            <el-option label="焦虑" value="焦虑" />
            <el-option label="兴奋" value="兴奋" />
            <el-option label="疲劳" value="疲劳" />
            <el-option label="中性" value="中性" />
          </el-select>
        </el-form-item>
        <el-form-item label="记录日期">
          <el-date-picker v-model="formData.recordDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="记录时间">
          <el-input v-model="formData.recordTime" placeholder="HH:mm" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.notes" type="textarea" placeholder="记录相关的心理事件或想法" />
        </el-form-item>
        <el-button type="primary" @click="submitForm">添加记录</el-button>
      </el-form>

      <el-divider />

      <!-- 查询和过滤 -->
      <div class="search-box">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          @change="loadRecords"
        />
        <el-button @click="loadRecords">搜索</el-button>
      </div>

      <!-- 列表 -->
      <el-table :data="recordList" style="width: 100%; margin-top: 20px">
        <el-table-column prop="recordDate" label="日期" width="120" />
        <el-table-column prop="recordTime" label="时间" width="100" />
        <el-table-column prop="moodScore" label="心情评分" width="100">
          <template #default="scope">
            <el-rate v-model="scope.row.moodScore" :max="10" disabled allow-half />
          </template>
        </el-table-column>
        <el-table-column prop="stressLevel" label="压力等级" width="100">
          <template #default="scope">
            <el-tag :type="getStressType(scope.row.stressLevel)">{{ scope.row.stressLevel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="mentalsState" label="心理状态" width="100" />
        <el-table-column prop="notes" label="备注" min-width="200" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="editRecord(scope.row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="deleteRecord(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        :current-page="pageNum"
        :page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        :total="total"
        style="text-align: right; margin-top: 20px"
        @current-change="pageNum = $event"
        @size-change="pageSize = $event"
      />
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog v-model="editVisible" title="编辑心理健康记录" width="450px">
      <el-form :model="editFormData" label-width="100px">
        <el-form-item label="心情评分">
          <el-slider v-model="editFormData.moodScore" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="压力等级">
          <el-select v-model="editFormData.stressLevel">
            <el-option label="低" value="低" />
            <el-option label="中" value="中" />
            <el-option label="高" value="高" />
          </el-select>
        </el-form-item>
        <el-form-item label="焦虑程度">
          <el-slider v-model="editFormData.anxietyLevel" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="心理状态">
          <el-select v-model="editFormData.mentalsState">
            <el-option label="开心" value="开心" />
            <el-option label="平静" value="平静" />
            <el-option label="沮丧" value="沮丧" />
            <el-option label="焦虑" value="焦虑" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="editFormData.notes" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '../store/auth.js'
import * as healthApi from '../api/health'

const authStore = useAuthStore()
const currentUser = computed(() => authStore.getCurrentUser())

const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const recordList = ref([])
const dateRange = ref([])

const formData = ref({
  moodScore: 5,
  stressLevel: '',
  anxietyLevel: 5,
  mentalsState: '',
  recordDate: new Date().toISOString().split('T')[0],
  recordTime: '',
  notes: ''
})

const editFormData = ref({})
const editVisible = ref(false)
const editingId = ref(null)

const getStressType = (level) => {
  if (level === '低') return 'success'
  if (level === '中') return 'warning'
  if (level === '高') return 'danger'
  return 'info'
}

const submitForm = async () => {
  try {
    await healthApi.addMentalHealth({
      ...formData.value,
      userId: currentUser.value.id
    })
    ElMessage.success('添加成功')
    formData.value = { moodScore: 5, stressLevel: '', anxietyLevel: 5, mentalsState: '', recordDate: new Date().toISOString().split('T')[0], recordTime: '', notes: '' }
    loadRecords()
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

const loadRecords = async () => {
  try {
    const response = await healthApi.getMentalHealthList(
      currentUser.value.id,
      pageNum.value,
      pageSize.value,
      dateRange.value[0],
      dateRange.value[1]
    )
    recordList.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const editRecord = (row) => {
  editingId.value = row.id
  editFormData.value = { ...row }
  editVisible.value = true
}

const saveEdit = async () => {
  try {
    await healthApi.updateMentalHealth(editingId.value, editFormData.value)
    ElMessage.success('更新成功')
    editVisible.value = false
    loadRecords()
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

const deleteRecord = (id) => {
  ElMessageBox.confirm('确定删除该记录?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await healthApi.deleteMentalHealth(id)
      ElMessage.success('删除成功')
      loadRecords()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

watch([pageNum, pageSize], () => {
  if (currentUser.value?.id) {
    loadRecords()
  }
})

// 确保用户信息加载后再加载数据
onMounted(() => {
  if (currentUser.value?.id) {
    loadRecords()
  } else {
    ElMessage.warning('请先登录')
  }
})
</script>

<style scoped>
.mental-health-record {
  padding: 20px;
}

.form-box {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.search-box {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}
</style>
