<template>
  <div class="glucose-record">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>血糖记录</span>
        </div>
      </template>

      <!-- 添加表单 -->
      <el-form :model="formData" label-width="100px" class="form-box">
        <el-form-item label="血糖值(mmol/L)">
          <el-input-number v-model="formData.glucoseValue" :step="0.1" :min="0" :max="30" />
        </el-form-item>
        <el-form-item label="测量类型">
          <el-select v-model="formData.measurementType" placeholder="请选择">
            <el-option label="空腹" value="空腹" />
            <el-option label="饭后" value="饭后" />
            <el-option label="睡前" value="睡前" />
          </el-select>
        </el-form-item>
        <el-form-item label="记录日期">
          <el-date-picker v-model="formData.recordDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="记录时间">
          <el-input v-model="formData.recordTime" placeholder="HH:mm" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.notes" type="textarea" placeholder="输入备注信息" />
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
        <el-table-column prop="glucoseValue" label="血糖值(mmol/L)" width="140" />
        <el-table-column prop="measurementType" label="测量类型" width="100" />
        <el-table-column prop="notes" label="备注" />
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
    <el-dialog v-model="editVisible" title="编辑血糖记录" width="400px">
      <el-form :model="editFormData" label-width="100px">
        <el-form-item label="血糖值">
          <el-input-number v-model="editFormData.glucoseValue" :step="0.1" />
        </el-form-item>
        <el-form-item label="测量类型">
          <el-select v-model="editFormData.measurementType">
            <el-option label="空腹" value="空腹" />
            <el-option label="饭后" value="饭后" />
            <el-option label="睡前" value="睡前" />
          </el-select>
        </el-form-item>
        <el-form-item label="记录时间">
          <el-input v-model="editFormData.recordTime" />
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
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useStore } from 'pinia'
import * as healthApi from '../api/health'

const store = useStore()
const currentUser = computed(() => store.auth.user)

const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const recordList = ref([])
const dateRange = ref([])

const formData = ref({
  glucoseValue: null,
  measurementType: '',
  recordDate: new Date().toISOString().split('T')[0],
  recordTime: '',
  notes: ''
})

const editFormData = ref({})
const editVisible = ref(false)
const editingId = ref(null)

const submitForm = async () => {
  try {
    await healthApi.addBloodGlucose({
      ...formData.value,
      userId: currentUser.value.id
    })
    ElMessage.success('添加成功')
    formData.value = { glucoseValue: null, measurementType: '', recordDate: new Date().toISOString().split('T')[0], recordTime: '', notes: '' }
    loadRecords()
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

const loadRecords = async () => {
  try {
    const response = await healthApi.getBloodGlucoseList(
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
    await healthApi.updateBloodGlucose(editingId.value, editFormData.value)
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
      await healthApi.deleteBloodGlucose(id)
      ElMessage.success('删除成功')
      loadRecords()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

watch([pageNum, pageSize], () => {
  loadRecords()
})

loadRecords()
</script>

<style scoped>
.glucose-record {
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
