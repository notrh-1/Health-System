<template>
  <div class="knowledge-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>运动科普</span>
          <div>
            <el-button v-if="isAdmin" type="primary" @click="showAddDialog">添加科普</el-button>
            <el-button v-if="isAdmin" type="success" @click="showAddSportDialog">添加运动项目</el-button>
          </div>
        </div>
      </template>

      <div class="category-tabs">
        <el-tag
          v-for="cat in categories"
          :key="cat"
          :type="currentCategory === cat ? '' : 'info'"
          :effect="currentCategory === cat ? 'dark' : 'plain'"
          @click="filterByCategory(cat)"
          class="category-tag"
        >
          {{ cat }}
        </el-tag>
      </div>

      <el-row :gutter="20">
        <el-col :span="8" v-for="item in knowledgeList" :key="item.id">
          <el-card class="knowledge-card" shadow="hover" @click="showDetail(item)">
            <div class="knowledge-content">
              <div class="category-badge">{{ item.category }}</div>
              <h3 class="knowledge-title">{{ item.title }}</h3>
              <p class="knowledge-desc">{{ item.content }}</p>
              <div class="knowledge-footer">
                <span class="time">{{ formatDate(item.createTime) }}</span>
                <el-button v-if="isAdmin" type="danger" size="small" text @click.stop="deleteKnowledge(item.id)">删除</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <el-dialog v-model="detailVisible" :title="currentKnowledge.title" width="600px">
      <div class="detail-content">
        <el-tag type="primary" class="detail-category">{{ currentKnowledge.category }}</el-tag>
        <p class="detail-text">{{ currentKnowledge.content }}</p>
        <div class="detail-time">发布时间：{{ formatDate(currentKnowledge.createTime) }}</div>
      </div>
    </el-dialog>

    <el-dialog v-model="addDialogVisible" title="添加科普文章" width="500px">
      <el-form :model="knowledgeForm" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="knowledgeForm.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select
            v-model="knowledgeForm.category"
            placeholder="请选择或输入新分类"
            style="width: 100%"
            allow-create
            filterable
            default-first-option
          >
            <el-option v-for="cat in sportCategories" :key="cat" :label="cat" :value="cat" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="knowledgeForm.content" type="textarea" :rows="6" placeholder="请输入科普内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="addSportDialogVisible" title="添加运动项目" width="500px">
      <el-form :model="sportForm" :rules="sportRules" ref="sportFormRef" label-width="100px">
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
        <el-button @click="addSportDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAddSport">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const user = ref(JSON.parse(window.sessionStorage.getItem('user') || '{}'))
const isAdmin = computed(() => {
  return user.value?.role === 'admin'
})

const knowledgeList = ref([])
const allKnowledge = ref([])
const detailVisible = ref(false)
const addDialogVisible = ref(false)
const currentKnowledge = ref({})
const currentCategory = ref('全部')
const formRef = ref(null)
const categorySelect = ref(null)

const categories = ref(['全部'])
const sportCategories = ref(['跑步', '游泳', '瑜伽', '篮球', '羽毛球', '骑行', '健身操', '跳绳'])

const knowledgeForm = ref({
  title: '',
  category: '',
  content: '',
  image: ''
})

// 用于存储用户输入的分类值
const categoryInput = ref('')

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: ['change', 'blur'] }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

// 运动项目相关
const addSportDialogVisible = ref(false)
const sportForm = ref({
  name: '',
  calories: 0,
  duration: 30,
  description: ''
})

const sportRules = {
  name: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  calories: [{ required: true, message: '请输入消耗卡路里', trigger: 'blur' }],
  duration: [{ required: true, message: '请输入运动时长', trigger: 'blur' }]
}

const sportFormRef = ref(null)
const sportNames = ref([])

onMounted(() => {
  getList()
  loadSportNames()
})

const getList = async () => {
  const res = await request.get('/knowledge/list')
  if (res.flag === 200) {
    allKnowledge.value = res.list
    knowledgeList.value = res.list
    const allCats = res.list.map(item => item.category)
    const uniqueCats = [...new Set(allCats)]
    categories.value = ['全部', ...uniqueCats.filter(cat => !sportCategories.value.includes(cat)), ...sportCategories.value]
    categories.value = [...new Set(categories.value)]
  }
}

const filterByCategory = (cat) => {
  currentCategory.value = cat
  if (cat === '全部') {
    knowledgeList.value = allKnowledge.value
  } else {
    knowledgeList.value = allKnowledge.value.filter(item => item.category === cat)
  }
}

const showDetail = (item) => {
  currentKnowledge.value = item
  detailVisible.value = true
}

const showAddDialog = () => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员才能添加科普文章')
    return
  }
  knowledgeForm.value = { title: '', category: '', content: '', image: '' }
  addDialogVisible.value = true
}

const submitAdd = async () => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员才能操作')
    return
  }
  try {
    // 确保category字段有值
    if (!knowledgeForm.value.category) {
      ElMessage.warning('请选择或输入分类')
      return
    }
    
    // 处理可能的事件对象
    if (typeof knowledgeForm.value.category === 'object') {
      knowledgeForm.value.category = ''
      ElMessage.warning('请重新输入分类')
      return
    }
    
    await formRef.value.validate()
    
    const res = await request.post('/knowledge/add', knowledgeForm.value)
    if (res.flag === 200) {
      ElMessage.success('添加成功')
      addDialogVisible.value = false
      getList()
    } else {
      ElMessage.error(res.message || '添加失败')
    }
  } catch (error) {
    console.error('表单验证失败:', error)
    ElMessage.error('表单验证失败，请检查输入')
  }
}

const deleteKnowledge = (id) => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员才能删除科普文章')
    return
  }
  ElMessageBox.confirm('确定要删除该科普文章吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await request.delete(`/knowledge/delete/${id}`)
    if (res.flag === 200) {
      ElMessage.success('删除成功')
      getList()
    } else {
      ElMessage.error('删除失败')
    }
  })
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

const handleCategoryInput = (value) => {
  // 存储用户输入的值，处理可能的事件对象
  if (value && typeof value === 'object' && value.target) {
    categoryInput.value = value.target.value
  } else {
    categoryInput.value = value
  }
}

const handleCategoryChange = (value) => {
  if (value && typeof value === 'string') {
    knowledgeForm.value.category = value
    // 清除输入缓存
    categoryInput.value = ''
  }
}

const handleCategoryBlur = (event) => {
  // 当失焦时，如果没有选择现有分类，但有输入值，则使用输入值作为分类
  if (categoryInput.value && !knowledgeForm.value.category) {
    knowledgeForm.value.category = categoryInput.value
  }
}

const handleCategoryEnter = (event) => {
  // 当用户按Enter键时，如果有输入值，则使用输入值作为分类
  if (categoryInput.value) {
    knowledgeForm.value.category = categoryInput.value
  }
  // 阻止默认行为，避免表单提交
  if (event && event.preventDefault) {
    event.preventDefault()
  }
}

// 运动项目相关函数
const showAddSportDialog = () => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员才能添加运动项目')
    return
  }
  sportForm.value = {
    name: '',
    calories: 0,
    duration: 30,
    description: ''
  }
  addSportDialogVisible.value = true
}

const loadSportNames = async () => {
  try {
    const res = await request.get('/sport/list')
    if (res && res.flag === 200 && res.sports) {
      const names = res.sports.map(sport => sport.name)
      sportNames.value = [...new Set(names)]
    }
  } catch (error) {
    console.error('加载运动项目失败:', error)
  }
}

const submitAddSport = async () => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员才能操作')
    return
  }
  try {
    await sportFormRef.value.validate()
    
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
      ElMessage.success('添加成功')
      addSportDialogVisible.value = false
      loadSportNames()
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
.knowledge-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header div {
  display: flex;
  gap: 10px;
}

.category-tabs {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.category-tag {
  cursor: pointer;
  transition: all 0.3s;
}

.category-tag:hover {
  transform: scale(1.05);
}

.knowledge-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.knowledge-card:hover {
  transform: translateY(-5px);
}

.knowledge-content {
  position: relative;
}

.category-badge {
  position: absolute;
  top: 0;
  right: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 4px 12px;
  border-radius: 0 0 0 10px;
  font-size: 12px;
}

.knowledge-title {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 18px;
  font-weight: 600;
  padding-right: 60px;
}

.knowledge-desc {
  color: #606266;
  font-size: 14px;
  line-height: 1.8;
  margin: 0 0 15px 0;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.knowledge-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #ebeef5;
  padding-top: 10px;
}

.time {
  color: #909399;
  font-size: 12px;
}

.detail-content {
  padding: 10px;
}

.detail-category {
  margin-bottom: 20px;
}

.detail-text {
  font-size: 16px;
  line-height: 2;
  color: #303133;
  text-align: justify;
}

.detail-time {
  margin-top: 20px;
  color: #909399;
  font-size: 14px;
}
</style>
