<template>
  <div class="sport-container">
    <el-card>
      <template #header>
        <div class="card-header">
        <span>运动项目列表</span>
        <div class="header-actions">
          <el-button v-if="isAdmin" type="primary" @click="showManageDialog">管理运动</el-button>
        </div>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="6" v-for="item in sportList" :key="item.id">
          <el-card class="sport-card" shadow="hover">
            <div class="sport-info">
              <h3 class="sport-name">{{ item.name }}</h3>
              <div class="info-item">
                <el-icon><Odometer /></el-icon>
                <span>消耗热量：{{ item.calories }} 卡/小时</span>
              </div>
              <div class="info-item">
                <el-icon><Timer /></el-icon>
                <span>建议时长：{{ item.duration }} 分钟</span>
              </div>
              
              <div class="calculator-section">
                <div class="calculator-title">今日目标：</div>
                <div class="calculator-input">
                  <el-input-number 
                    v-model="item.targetCalories" 
                    :min="10" 
                    :max="2000" 
                    size="small"
                    placeholder="输入卡路里"
                  />
                  <span class="unit">卡</span>
                </div>
                <div class="calculator-result" v-if="item.targetCalories">
                  <el-icon><Timer /></el-icon>
                  <span>预估时间：{{ calculateTime(item) }} 分钟</span>
                </div>
              </div>
              
              <p class="description">{{ item.description }}</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <el-dialog v-model="manageDialogVisible" title="管理运动项目" width="1000px">
      <div class="manage-section">
        <div class="manage-header">
          <el-button type="primary" @click="showAddDialog">添加运动</el-button>
        </div>
        <el-table :data="sportList" border stripe style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="运动名称" width="150" />
          <el-table-column prop="calories" label="消耗热量(卡/小时)" width="180" />
          <el-table-column prop="duration" label="建议时长(分钟)" width="180" />
          <el-table-column prop="description" label="描述" />
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button type="danger" size="small" @click="deleteSport(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="sportForm" :rules="rules" ref="sportFormRef" label-width="100px">
        <el-form-item label="运动名称" prop="name">
          <el-input v-model="sportForm.name" />
        </el-form-item>
        <el-form-item label="消耗热量" prop="calories">
          <el-input-number v-model="sportForm.calories" :min="0" :max="2000" />
          <span style="margin-left: 10px;">卡路里/小时</span>
        </el-form-item>
        <el-form-item label="建议时长" prop="duration">
          <el-input-number v-model="sportForm.duration" :min="0" :max="300" />
          <span style="margin-left: 10px;">分钟</span>
        </el-form-item>
        <el-form-item label="运动描述" prop="description">
          <el-input v-model="sportForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Timer, Odometer } from '@element-plus/icons-vue'
import request from '@/utils/request'

const user = ref(JSON.parse(window.sessionStorage.getItem('user') || '{}'))
const isAdmin = computed(() => {
  return user.value?.role === 'admin'
})

const sportList = ref([])
const dialogVisible = ref(false)
const manageDialogVisible = ref(false)
const dialogTitle = ref('')
const sportFormRef = ref(null)

const sportForm = ref({
  id: null,
  name: '',
  calories: 0,
  duration: 0,
  description: ''
})

const rules = {
  name: [
    { required: true, message: '请输入运动名称', trigger: 'blur' }
  ],
  calories: [
    { required: true, message: '请输入消耗热量', trigger: 'blur' }
  ],
  duration: [
    { required: true, message: '请输入建议时长', trigger: 'blur' }
  ]
}

onMounted(() => {
  getSportList()
})

const getSportList = async () => {
  const res = await request.get('/sport/list')
  if (res.flag === 200) {
    sportList.value = res.sports.map(sport => ({
      ...sport,
      targetCalories: null
    }))
  }
}

const calculateTime = (item) => {
  if (!item.targetCalories || item.calories <= 0) return 0
  const time = (item.targetCalories / item.calories) * 60
  return Math.ceil(time)
}

const showManageDialog = () => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员才能管理运动项目')
    return
  }
  manageDialogVisible.value = true
}

const showAddDialog = () => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员才能添加运动项目')
    return
  }
  dialogTitle.value = '添加运动'
  sportForm.value = {
    id: null,
    name: '',
    calories: 0,
    duration: 0,
    description: ''
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员才能操作')
    return
  }
  const valid = await sportFormRef.value.validate()
  if (!valid) return

  const res = await request.post('/sport/add', sportForm.value)
  if (res.flag === 200) {
    ElMessage.success('添加成功')
    dialogVisible.value = false
    getSportList()
  } else {
    ElMessage.error('添加失败')
  }
}

const deleteSport = (id) => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员才能删除运动项目')
    return
  }
  ElMessageBox.confirm('确定要删除该运动项目吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await request.delete(`/sport/delete/${id}`)
    if (res.flag === 200) {
      ElMessage.success('删除成功')
      getSportList()
    } else {
      ElMessage.error('删除失败')
    }
  })
}
</script>

<style scoped>
.sport-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.sport-card {
  margin-bottom: 20px;
  height: 320px;
}

.sport-card :deep(.el-card__body) {
  padding: 20px;
  height: 100%;
  box-sizing: border-box;
}

.sport-info {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.sport-name {
  margin: 0 0 15px 0;
  color: #409eff;
  font-size: 18px;
  font-weight: bold;
  text-align: center;
  padding-bottom: 10px;
  border-bottom: 2px solid #409eff;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  color: #666;
  font-size: 14px;
}

.info-item .el-icon {
  margin-right: 8px;
  color: #409eff;
  font-size: 16px;
}

.calculator-section {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 12px;
  margin: 10px 0;
}

.calculator-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 8px;
  font-weight: 500;
}

.calculator-input {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.calculator-input .unit {
  color: #909399;
  font-size: 14px;
}

.calculator-result {
  display: flex;
  align-items: center;
  color: #67c23a;
  font-size: 14px;
  font-weight: 500;
}

.calculator-result .el-icon {
  margin-right: 6px;
}

.description {
  color: #999;
  font-size: 14px;
  margin: 10px 0;
  line-height: 1.6;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.manage-section {
  padding: 10px 0;
}

.manage-header {
  margin-bottom: 15px;
}
</style>
