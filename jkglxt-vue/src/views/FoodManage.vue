<template>
  <div class="food-manage-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>食物库管理</span>
          <el-button type="primary" @click="showAddDialog">添加食物</el-button>
        </div>
      </template>

      <div class="filter-bar">
        <el-select v-model="filterType" placeholder="筛选类型" clearable @change="loadFoods" style="width: 200px;">
          <el-option label="全部" value="" />
          <el-option v-for="type in foodTypes" :key="type" :label="type" :value="type" />
        </el-select>
      </div>

      <el-table :data="filteredFoods" stripe style="width: 100%; margin-top: 20px;">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="foodName" label="食物名称" width="180" />
        <el-table-column prop="foodType" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.foodType)">{{ row.foodType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="calories" label="卡路里" width="100">
          <template #default="{ row }">
            {{ row.calories }} 千卡
          </template>
        </el-table-column>
        <el-table-column prop="protein" label="蛋白质" width="100">
          <template #default="{ row }">
            {{ row.protein || 0 }}g
          </template>
        </el-table-column>
        <el-table-column prop="carbs" label="碳水" width="100">
          <template #default="{ row }">
            {{ row.carbs || 0 }}g
          </template>
        </el-table-column>
        <el-table-column prop="fat" label="脂肪" width="100">
          <template #default="{ row }">
            {{ row.fat || 0 }}g
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="showEditDialog(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteFood(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="foodForm" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="食物名称" prop="foodName">
          <el-input v-model="foodForm.foodName" placeholder="请输入食物名称" />
        </el-form-item>
        <el-form-item label="食物类型" prop="foodType">
          <el-select v-model="foodForm.foodType" placeholder="请选择类型" style="width: 100%;">
            <el-option v-for="type in foodTypes" :key="type" :label="type" :value="type" />
          </el-select>
          <el-input v-if="showNewTypeInput" v-model="newTypeName" placeholder="输入新类型" style="margin-top: 10px;" />
          <el-button v-else type="text" @click="toggleNewTypeInput" style="margin-top: 10px;">+ 添加新类型</el-button>
        </el-form-item>
        <el-form-item label="卡路里" prop="calories">
          <el-input-number v-model="foodForm.calories" :min="0" :max="10000" style="width: 100%;" />
          <span style="margin-left: 10px;">千卡</span>
        </el-form-item>
        <el-form-item label="蛋白质">
          <el-input-number v-model="foodForm.protein" :min="0" :max="500" :precision="1" step="0.1" style="width: 100%;" />
          <span style="margin-left: 10px;">克</span>
        </el-form-item>
        <el-form-item label="碳水化合物">
          <el-input-number v-model="foodForm.carbs" :min="0" :max="500" :precision="1" step="0.1" style="width: 100%;" />
          <span style="margin-left: 10px;">克</span>
        </el-form-item>
        <el-form-item label="脂肪">
          <el-input-number v-model="foodForm.fat" :min="0" :max="500" :precision="1" step="0.1" style="width: 100%;" />
          <span style="margin-left: 10px;">克</span>
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const foodList = ref([])
const foodTypes = ref([])
const filterType = ref('')
const dialogVisible = ref(false)
const dialogTitle = ref('添加食物')
const formRef = ref(null)
const isEdit = ref(false)
const showNewTypeInput = ref(false)
const newTypeName = ref('')

const foodForm = ref({
  id: null,
  foodName: '',
  foodType: '',
  calories: 0,
  protein: 0,
  carbs: 0,
  fat: 0
})

const rules = {
  foodName: [{ required: true, message: '请输入食物名称', trigger: 'blur' }],
  foodType: [{ required: true, message: '请选择食物类型', trigger: 'change' }],
  calories: [{ required: true, message: '请输入卡路里', trigger: 'blur' }]
}

const filteredFoods = computed(() => {
  if (!filterType.value) {
    return foodList.value
  }
  return foodList.value.filter(food => food.foodType === filterType.value)
})

const getTypeTagType = (type) => {
  const typeMap = {
    '主食': 'warning',
    '蔬菜': 'success',
    '水果': 'danger',
    '肉类': 'primary',
    '蛋奶': 'info',
    '水产': ''
  }
  return typeMap[type] || 'info'
}

onMounted(() => {
  loadFoods()
  loadTypes()
})

const loadFoods = async () => {
  try {
    const res = await request.get('/food/list')
    if (res.flag === 200) {
      foodList.value = res.data || []
    }
  } catch (error) {
    console.error('加载食物列表失败:', error)
  }
}

const loadTypes = async () => {
  try {
    const res = await request.get('/food/types')
    if (res.flag === 200) {
      foodTypes.value = res.data || []
    }
  } catch (error) {
    console.error('加载食物类型失败:', error)
  }
}

const showAddDialog = () => {
  dialogTitle.value = '添加食物'
  isEdit.value = false
  showNewTypeInput.value = false
  newTypeName.value = ''
  foodForm.value = {
    id: null,
    foodName: '',
    foodType: '',
    calories: 0,
    protein: 0,
    carbs: 0,
    fat: 0
  }
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  dialogTitle.value = '编辑食物'
  isEdit.value = true
  showNewTypeInput.value = false
  newTypeName.value = ''
  foodForm.value = { ...row }
  dialogVisible.value = true
}

const toggleNewTypeInput = () => {
  showNewTypeInput.value = !showNewTypeInput.value
}

const submitForm = async () => {
  try {
    await formRef.value.validate()
    
    if (showNewTypeInput.value && newTypeName.value) {
      if (!foodTypes.value.includes(newTypeName.value)) {
        foodTypes.value.push(newTypeName.value)
      }
      foodForm.value.foodType = newTypeName.value
    }

    if (isEdit.value) {
      await request.put('/food/update', foodForm.value)
      ElMessage.success('更新成功')
    } else {
      await request.post('/food/add', foodForm.value)
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    loadFoods()
    loadTypes()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

const deleteFood = (row) => {
  ElMessageBox.confirm(`确定要删除食物"${row.foodName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/food/delete/${row.id}`)
      ElMessage.success('删除成功')
      loadFoods()
    } catch (error) {
      console.error('删除失败:', error)
    }
  })
}
</script>

<style scoped>
.food-manage-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-bar {
  display: flex;
  justify-content: flex-start;
}
</style>
