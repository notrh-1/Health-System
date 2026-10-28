<template>
  <div class="project-container">
    <div class="header">
      <h2>运动项目列表</h2>
      <div>
        <el-button v-if="isAdmin" type="primary" @click="showAddDialog">添加运动</el-button>
        <el-button type="success" @click="showManageDialog">管理运动</el-button>
      </div>
    </div>

    <div class="sport-grid">
      <div class="sport-card" v-for="sport in sports" :key="sport.id || sport.name">
        <div class="card-header">
          <div class="sport-name">{{ sport.name }}</div>
          <div class="action-buttons" v-if="isAdmin">
            <el-button type="primary" size="small" text @click="showEditDialog(sport)">
              编辑
            </el-button>
            <el-button type="danger" size="small" text @click="deleteSport(sport)">
              删除
            </el-button>
          </div>
        </div>
        <div class="sport-info">
          <div class="calorie-info">
            <span class="icon">⚡</span>
            <span>消耗热量：{{ sport.calories }} 卡/小时</span>
          </div>
          <div class="duration-info">
            <span class="icon">⏱</span>
            <span>建议时长：{{ sport.duration }} 分钟</span>
          </div>
        </div>
        <div class="target-section">
          <div class="target-label">今日目标：</div>
          <div class="target-input">
            <el-button size="small" @click="decreaseTarget(sport)" :disabled="sport.target <= 0">-</el-button>
            <el-input-number
              v-model="sport.target"
              :min="0"
              :max="10000"
              size="small"
              style="width: 80px"
            />
            <el-button size="small" @click="increaseTarget(sport)">+</el-button>
            <span class="unit">卡</span>

            <button
              @click="submitSport(sport)"
              style="margin-left: 10px; padding: 6px 16px; background-color: #ff4d4f; color: white; border: none; border-radius: 4px; font-size: 14px; cursor: pointer; font-weight: bold;"
            >
              提交
            </button>
          </div>
        </div>
        <div class="sport-desc">{{ sport.description }}</div>
        <div class="estimated-time" v-if="sport.target > 0">
          <span class="icon">⏰</span>
          <span>预估时间：{{ Math.round((sport.target / sport.calories) * 60) }} 分钟</span>
        </div>
      </div>
    </div>

    <el-dialog v-model="addDialogVisible" title="添加运动项目" width="500px">
      <el-form :model="sportForm" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="项目名称" prop="name">
          <el-input
            v-model="sportForm.name"
            placeholder="请输入项目名称"
            style="width: 100%"
          />
          <div style="margin-top: 8px; color: #909399; font-size: 12px;">
            提示：已有的运动项目包括：{{ sportNames.join('、') }}
          </div>
        </el-form-item>
        <el-form-item label="消耗卡路里" prop="calories">
          <el-input-number v-model="sportForm.calories" :min="0" :max="10000" style="width: 100%;" />
          <span style="margin-left: 10px;">卡/小时</span>
        </el-form-item>
        <el-form-item label="运动时长" prop="duration">
          <el-input-number v-model="sportForm.duration" :min="0" :max="600" style="width: 100%;" />
          <span style="margin-left: 10px;">分钟</span>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="sportForm.description" type="textarea" :rows="3" placeholder="请输入项目描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="editDialogVisible" title="编辑运动项目" width="500px">
      <el-form :model="editForm" :rules="rules" ref="editFormRef" label-width="100px">
        <el-form-item label="项目名称" prop="name">
          <el-input
            v-model="editForm.name"
            placeholder="请输入项目名称"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="消耗卡路里" prop="calories">
          <el-input-number v-model="editForm.calories" :min="0" :max="10000" style="width: 100%;" />
          <span style="margin-left: 10px;">卡/小时</span>
        </el-form-item>
        <el-form-item label="运动时长" prop="duration">
          <el-input-number v-model="editForm.duration" :min="0" :max="600" style="width: 100%;" />
          <span style="margin-left: 10px;">分钟</span>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="editForm.description" type="textarea" :rows="3" placeholder="请输入项目描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="manageDialogVisible" title="管理运动项目" width="600px">
      <el-card class="list-card" style="margin-top: 20px;">
        <template #header>
          <div class="stats-header">
            <span>运动项目记录</span>
          </div>
        </template>
        <el-table :data="projectList" stripe style="width: 100%">
          <el-table-column prop="name" label="项目名称" />
          <el-table-column prop="calories" label="消耗卡路里" width="120">
            <template #default="{ row }">
              {{ row.calories }} 千卡/小时
            </template>
          </el-table-column>
          <el-table-column prop="duration" label="运动时长" width="100">
            <template #default="{ row }">
              {{ row.duration }} 分钟
            </template>
          </el-table-column>
          <el-table-column prop="totalCalories" label="总消耗" width="120">
            <template #default="{ row }">
              {{ row.totalCalories }} 千卡
            </template>
          </el-table-column>
          <el-table-column prop="recordDate" label="日期" width="120" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button type="danger" size="small" text @click="deleteProject(row.id)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
      <template #footer>
        <el-button @click="manageDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="confirmDialogVisible" title="确认提交" width="400px">
      <div class="confirm-content">
        <p>您确定要提交以下运动数据吗？</p>
        <p><strong>项目：</strong>{{ confirmSport.name }}</p>
        <p><strong>目标消耗：</strong>{{ confirmSport.target }} 千卡</p>
        <p><strong>日期：</strong>{{ currentDate }}</p>
      </div>
      <template #footer>
        <el-button @click="confirmDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSubmit">确定提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const projectList = ref([])
const manageDialogVisible = ref(false)
const confirmDialogVisible = ref(false)
const addDialogVisible = ref(false)
const editDialogVisible = ref(false)
const user = ref(JSON.parse(window.sessionStorage.getItem('user')))
const currentDate = ref(new Date().toISOString().split('T')[0])
const formRef = ref(null)
const editFormRef = ref(null)

const confirmSport = ref({
  name: '',
  target: 0
})

const isAdmin = computed(() => {
  return user.value && (user.value.role === 'admin' || user.value.role === '管理员')
})

const sportNames = ref(['跑步', '游泳', '瑜伽', '篮球', '羽毛球', '骑行', '健身操', '跳绳'])

const sportForm = ref({
  name: '',
  calories: 0,
  duration: 0,
  description: ''
})

const editForm = ref({
  id: null,
  name: '',
  calories: 0,
  duration: 0,
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  calories: [{ required: true, message: '请输入消耗卡路里', trigger: 'blur' }],
  duration: [{ required: true, message: '请输入运动时长', trigger: 'blur' }]
}

const sports = ref([
  {
    id: 1,
    name: '跑步',
    calories: 600,
    duration: 45,
    target: 0,
    description: '户外跑步，有氧运动，提高心肺功能'
  },
  {
    id: 2,
    name: '游泳',
    calories: 550,
    duration: 60,
    target: 0,
    description: '全身运动，对关节友好，增强耐力'
  },
  {
    id: 3,
    name: '瑜伽',
    calories: 300,
    duration: 60,
    target: 0,
    description: '放松身心，提高柔韧性，缓解压力'
  },
  {
    id: 4,
    name: '篮球',
    calories: 500,
    duration: 60,
    target: 0,
    description: '团队运动，提高协调性，增强爆发力'
  },
  {
    id: 5,
    name: '羽毛球',
    calories: 400,
    duration: 60,
    target: 0,
    description: '快速反应，提高手眼协调，全身锻炼'
  },
  {
    id: 6,
    name: '骑行',
    calories: 500,
    duration: 60,
    target: 0,
    description: '低冲击运动，锻炼下肢力量，适合长期坚持'
  },
  {
    id: 7,
    name: '健身操',
    calories: 400,
    duration: 60,
    target: 0,
    description: '有氧运动，提高心肺功能，燃烧脂肪'
  },
  {
    id: 8,
    name: '跳绳',
    calories: 700,
    duration: 30,
    target: 0,
    description: '高效燃脂，提高心肺功能，适合居家锻炼'
  }
])

onMounted(() => {
  loadSports()
  loadProjectList()
})

const loadSports = async () => {
  try {
    const res = await request.get('/sport/list')
    if (res && res.flag === 200 && res.sports) {
      const dbSports = res.sports.map(sport => ({
        ...sport,
        id: sport.id,
        name: sport.name,
        calories: sport.calories,
        duration: sport.duration,
        target: 0,
        description: sport.description || ''
      }))
      sports.value = dbSports
      const dbNames = dbSports.map(s => s.name)
      sportNames.value = [...new Set([...sportNames.value, ...dbNames])]
    }
  } catch (error) {
    console.error('加载运动项目失败:', error)
  }
}

const loadProjectList = async () => {
  try {
    const userId = user.value?.id || 1
    const res = await request.get(`/project/list/${userId}`)
    if (res && res.flag === 200) {
      projectList.value = res.list || []
    }
  } catch (error) {
    console.error('加载运动项目失败:', error)
    ElMessage.error('加载运动项目失败')
  }
}

const decreaseTarget = (sport) => {
  if (sport.target > 0) {
    sport.target -= 50
  }
}

const increaseTarget = (sport) => {
  sport.target += 50
}

const submitSport = (sport) => {
  if (sport.target <= 0) {
    ElMessage.warning('请先设置目标消耗卡路里')
    return
  }

  confirmSport.value = {
    name: sport.name,
    target: sport.target
  }
  confirmDialogVisible.value = true
}

const confirmSubmit = async () => {
  try {
    const projectRes = await request.post('/project/add', {
      name: confirmSport.value.name,
      calories: 0,
      duration: 0,
      totalCalories: confirmSport.value.target,
      recordDate: currentDate.value,
      userId: user.value?.id || 1
    })

    if (projectRes.flag === 200) {
      const calorieRes = await request.post('/calorie/add', {
        type: '消耗',
        name: confirmSport.value.name,
        calories: confirmSport.value.target,
        recordDate: currentDate.value,
        userId: user.value?.id || 1
      })

      if (calorieRes.flag === 200) {
        ElMessage.success('提交成功，已同步到卡路里模块')
        confirmDialogVisible.value = false
        loadProjectList()

        sports.value.forEach(sport => {
          if (sport.name === confirmSport.value.name) {
            sport.target = 0
          }
        })
      } else {
        ElMessage.error('提交成功，但同步到卡路里模块失败')
        confirmDialogVisible.value = false
      }
    } else {
      ElMessage.error('提交失败')
    }
  } catch (error) {
    console.error('提交运动项目失败:', error)
    ElMessage.error('提交失败')
  }
}

const showEditDialog = (sport) => {
  editForm.value = {
    id: sport.id,
    name: sport.name,
    calories: sport.calories,
    duration: sport.duration,
    description: sport.description || ''
  }
  editDialogVisible.value = true
}

const submitEdit = async () => {
  try {
    await editFormRef.value.validate()

    const sportName = String(editForm.value.name).trim()

    if (!sportName) {
      ElMessage.warning('请输入项目名称')
      return
    }

    const res = await request.post('/sport/update', {
      id: editForm.value.id,
      name: sportName,
      calories: editForm.value.calories,
      duration: editForm.value.duration,
      description: editForm.value.description
    })

    if (res && res.flag === 200) {
      const index = sports.value.findIndex(s => s.id === editForm.value.id)
      if (index !== -1) {
        sports.value[index].name = sportName
        sports.value[index].calories = editForm.value.calories
        sports.value[index].duration = editForm.value.duration
        sports.value[index].description = editForm.value.description
      }

      ElMessage.success('修改成功')
      editDialogVisible.value = false
    } else {
      ElMessage.error(res?.message || '修改失败')
    }
  } catch (error) {
    console.error('修改运动项目失败:', error)
    ElMessage.error('修改失败')
  }
}

const deleteSport = (sport) => {
  ElMessageBox.confirm(`确定要删除运动项目"${sport.name}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.delete(`/sport/delete/${sport.id}`)
      if (res && res.flag === 200) {
        ElMessage.success('删除成功')
        sports.value = sports.value.filter(s => s.id !== sport.id)
        sportNames.value = sportNames.value.filter(name => name !== sport.name)
      } else {
        ElMessage.error(res?.message || '删除失败')
      }
    } catch (error) {
      console.error('删除运动项目失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

const deleteProject = (id) => {
  ElMessageBox.confirm('确定要删除该运动项目吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.delete(`/project/delete/${id}`)
      if (res.flag === 200) {
        ElMessage.success('删除成功')
        loadProjectList()
      } else {
        ElMessage.error('删除失败')
      }
    } catch (error) {
      console.error('删除运动项目失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

const showManageDialog = () => {
  loadProjectList()
  manageDialogVisible.value = true
}

const showAddDialog = () => {
  sportForm.value = {
    name: '',
    calories: 0,
    duration: 0,
    description: ''
  }
  addDialogVisible.value = true
}

const submitAdd = async () => {
  try {
    await formRef.value.validate()

    const sportName = String(sportForm.value.name).trim()

    if (!sportName) {
      ElMessage.warning('请输入项目名称')
      return
    }

    const res = await request.post('/sport/add', {
      name: sportName,
      calories: sportForm.value.calories,
      duration: sportForm.value.duration,
      description: sportForm.value.description
    })

    if (res && res.flag === 200) {
      const newSport = {
        id: Date.now(),
        name: sportName,
        calories: sportForm.value.calories,
        duration: sportForm.value.duration,
        target: 0,
        description: sportForm.value.description
      }

      sports.value.push(newSport)

      if (!sportNames.value.includes(sportName)) {
        sportNames.value.push(sportName)
      }

      ElMessage.success('添加成功')
      addDialogVisible.value = false
    } else {
      ElMessage.error(res?.message || '添加失败')
    }
  } catch (error) {
    console.error('添加运动项目失败:', error)
    ElMessage.error('添加失败')
  }
}
</script>

<style scoped>
.project-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.sport-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.sport-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.sport-card:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #1890ff;
}

.sport-name {
  font-size: 20px;
  font-weight: 600;
  color: #1890ff;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.sport-info {
  margin-bottom: 15px;
}

.calorie-info,
.duration-info {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
  color: #666;
}

.icon {
  margin-right: 8px;
  font-size: 16px;
}

.target-section {
  margin-bottom: 15px;
}

.target-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.target-input {
  display: flex;
  align-items: center;
}

.unit {
  margin-left: 10px;
  font-size: 14px;
  color: #666;
}

.sport-desc {
  font-size: 14px;
  color: #999;
  line-height: 1.4;
  margin-bottom: 15px;
  min-height: 40px;
}

.estimated-time {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  font-size: 14px;
  color: #67C23A;
  background-color: #f0f9eb;
  padding: 8px 12px;
  border-radius: 4px;
  border-left: 4px solid #67C23A;
}

.confirm-content {
  padding: 20px 0;
}

.confirm-content p {
  margin: 8px 0;
}

.list-card {
  height: 400px;
  overflow: auto;
}

.stats-header {
  font-weight: 600;
}
</style>