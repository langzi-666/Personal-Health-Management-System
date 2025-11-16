<template>
  <div class="diet-record">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>饮食记录</span>
        </div>
      </template>

      <!-- 添加表单 -->
      <el-form :model="formData" label-width="100px" class="form-box">
        <el-form-item label="食物名称">
          <el-input v-model="formData.foodName" placeholder="输入食物名称" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="formData.category" placeholder="请选择">
            <el-option label="早餐" value="早餐" />
            <el-option label="午餐" value="午餐" />
            <el-option label="晚餐" value="晚餐" />
            <el-option label="零食" value="零食" />
            <el-option label="饮料" value="饮料" />
          </el-select>
        </el-form-item>
        <el-form-item label="份量">
          <el-input v-model="formData.quantity" placeholder="如: 1碗, 100克" />
        </el-form-item>
        <el-form-item label="卡路里(kcal)">
          <el-input-number v-model="formData.calories" :min="0" :step="10" />
        </el-form-item>
        <el-form-item label="蛋白质(克)">
          <el-input-number v-model="formData.protein" :min="0" :step="0.1" />
        </el-form-item>
        <el-form-item label="脂肪(克)">
          <el-input-number v-model="formData.fat" :min="0" :step="0.1" />
        </el-form-item>
        <el-form-item label="碳水(克)">
          <el-input-number v-model="formData.carbohydrates" :min="0" :step="0.1" />
        </el-form-item>
        <el-form-item label="记录日期">
          <el-date-picker v-model="formData.recordDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="记录时间">
          <el-input v-model="formData.recordTime" placeholder="HH:mm" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.notes" type="textarea" />
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
        <el-table-column prop="foodName" label="食物名称" width="120" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="quantity" label="份量" width="100" />
        <el-table-column prop="calories" label="卡路里" width="100" />
        <el-table-column prop="protein" label="蛋白质" width="100" />
        <el-table-column prop="fat" label="脂肪" width="100" />
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
    <el-dialog v-model="editVisible" title="编辑饮食记录" width="450px">
      <el-form :model="editFormData" label-width="100px">
        <el-form-item label="食物名称">
          <el-input v-model="editFormData.foodName" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="editFormData.category">
            <el-option label="早餐" value="早餐" />
            <el-option label="午餐" value="午餐" />
            <el-option label="晚餐" value="晚餐" />
            <el-option label="零食" value="零食" />
          </el-select>
        </el-form-item>
        <el-form-item label="卡路里">
          <el-input-number v-model="editFormData.calories" />
        </el-form-item>
        <el-form-item label="蛋白质">
          <el-input-number v-model="editFormData.protein" />
        </el-form-item>
        <el-form-item label="脂肪">
          <el-input-number v-model="editFormData.fat" />
        </el-form-item>
        <el-form-item label="碳水">
          <el-input-number v-model="editFormData.carbohydrates" />
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
  foodName: '',
  category: '',
  quantity: '',
  calories: null,
  protein: null,
  fat: null,
  carbohydrates: null,
  recordDate: new Date().toISOString().split('T')[0],
  recordTime: '',
  notes: ''
})

const editFormData = ref({})
const editVisible = ref(false)
const editingId = ref(null)

const submitForm = async () => {
  try {
    await healthApi.addDiet({
      ...formData.value,
      userId: currentUser.value.id
    })
    ElMessage.success('添加成功')
    formData.value = { foodName: '', category: '', quantity: '', calories: null, protein: null, fat: null, carbohydrates: null, recordDate: new Date().toISOString().split('T')[0], recordTime: '', notes: '' }
    loadRecords()
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

const loadRecords = async () => {
  try {
    const response = await healthApi.getDietList(
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
    await healthApi.updateDiet(editingId.value, editFormData.value)
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
      await healthApi.deleteDiet(id)
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
.diet-record {
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
