<template>
  <div class="meal-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>营养配餐</span>
          <el-button type="primary" @click="showAddDialog">添加配餐</el-button>
        </div>
      </template>

      <div class="date-selector">
        <el-date-picker
          v-model="selectedDate"
          type="date"
          placeholder="选择日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          @change="loadData"
        />
      </div>

      <el-row :gutter="20">
        <el-col :span="14">
          <el-card class="stats-card">
            <template #header>
              <div class="stats-header">
                <span>今日营养摄入</span>
              </div>
            </template>
            <div class="stats-content">
              <div class="stat-item">
                <div class="stat-icon calories">
                  <el-icon><Coffee /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-label">卡路里</div>
                  <div class="stat-value">{{ nutritionStats.totalCalories || 0 }} <span>千卡</span></div>
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-icon protein">
                  <el-icon><Bowl /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-label">蛋白质</div>
                  <div class="stat-value">{{ formatNumber(nutritionStats.totalProtein) }} <span>克</span></div>
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-icon carbs">
                  <el-icon><Apple /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-label">碳水化合物</div>
                  <div class="stat-value">{{ formatNumber(nutritionStats.totalCarbs) }} <span>克</span></div>
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-icon fat">
                  <el-icon><Orange /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-label">脂肪</div>
                  <div class="stat-value">{{ formatNumber(nutritionStats.totalFat) }} <span>克</span></div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="10">
          <el-card class="chart-card">
            <template #header>
              <div class="stats-header">
                <span>营养比例</span>
              </div>
            </template>
            <div ref="pieChartRef" class="pie-chart"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-card class="meal-list-card" style="margin-top: 20px;">
        <template #header>
          <div class="stats-header">
            <span>今日餐食</span>
          </div>
        </template>
        <div class="meal-groups">
          <div v-for="mealType in mealTypes" :key="mealType" class="meal-group">
            <div class="meal-type-header">
              <el-tag :type="getMealTagType(mealType)" size="large">{{ mealType }}</el-tag>
              <span class="meal-type-calories">{{ getMealCalories(mealType) }} 千卡</span>
            </div>
            <div class="meal-items">
              <el-card v-for="item in getMealItems(mealType)" :key="item.id" class="meal-item" shadow="hover">
                <div class="meal-item-content">
                  <div class="meal-item-name">
                    {{ item.foodName }}
                    <el-tag v-if="item.calorieRecordId" type="success" size="small" style="margin-left: 8px;">已同步</el-tag>
                  </div>
                  <div class="meal-item-nutrition">
                    <span>{{ item.calories }} 千卡</span>
                    <span>蛋白质 {{ item.protein }}g</span>
                    <span>碳水 {{ item.carbs }}g</span>
                    <span>脂肪 {{ item.fat }}g</span>
                  </div>
                </div>
                <div class="meal-item-actions">
                  <el-button 
                    v-if="!item.calorieRecordId" 
                    type="primary" 
                    size="small" 
                    text 
                    @click="syncToCalorie(item)"
                  >
                    同步到卡路里
                  </el-button>
                  <el-button type="danger" size="small" text @click="deleteMeal(item.id)">
                    删除
                  </el-button>
                </div>
              </el-card>
              <el-empty v-if="getMealItems(mealType).length === 0" description="暂无配餐" :image-size="60" />
            </div>
          </div>
        </div>
      </el-card>
    </el-card>

    <el-dialog v-model="addDialogVisible" title="添加配餐" width="700px">
      <el-form :model="mealForm" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="餐食类型" prop="mealType">
          <el-select v-model="mealForm.mealType" placeholder="请选择餐食类型" style="width: 100%">
            <el-option label="早餐" value="早餐" />
            <el-option label="午餐" value="午餐" />
            <el-option label="晚餐" value="晚餐" />
            <el-option label="加餐" value="加餐" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择菜品">
          <el-select v-model="selectedFoodType" placeholder="选择菜品类型" @change="loadFoodsByType" style="width: 100%; margin-bottom: 10px;">
            <el-option label="全部" value="" />
            <el-option v-for="type in foodTypes" :key="type" :label="type" :value="type" />
          </el-select>
          <el-select v-model="selectedFood" placeholder="选择菜品添加" @change="addFoodToSelected" style="width: 100%;" clearable>
            <el-option v-for="food in filteredFoods" :key="food.id" :label="food.foodName" :value="food" />
          </el-select>
        </el-form-item>
        <el-form-item label="已选菜品">
          <div class="selected-foods">
            <el-tag v-for="(food, index) in selectedFoods" :key="index" closable @close="removeSelectedFood(index)" style="margin-right: 8px; margin-bottom: 8px;">
              {{ food.foodName }} ({{ food.calories }}千卡)
            </el-tag>
            <el-empty v-if="selectedFoods.length === 0" description="暂无已选菜品" :image-size="50" />
          </div>
        </el-form-item>
        <el-divider content-position="left">合计</el-divider>
        <el-form-item label="总卡路里">
          <span class="total-nutrition">{{ totalCalories }} 千卡</span>
        </el-form-item>
        <el-form-item label="总蛋白质">
          <span class="total-nutrition">{{ totalProtein }} 克</span>
        </el-form-item>
        <el-form-item label="日期" prop="mealDate">
          <el-date-picker
            v-model="mealForm.mealDate"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd" :disabled="selectedFoods.length === 0">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Coffee, Bowl, Apple, Orange } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import request from '@/utils/request'

const pieChartRef = ref(null)
let pieChart = null
const selectedDate = ref(new Date().toISOString().split('T')[0])
const mealList = ref([])
const nutritionStats = ref({})
const addDialogVisible = ref(false)
const formRef = ref(null)
const user = ref(JSON.parse(window.sessionStorage.getItem('user')))
const foodLibrary = ref([])
const foodTypes = ref([])
const selectedFoodType = ref('')
const selectedFood = ref(null)
const selectedFoods = ref([])

const mealTypes = ['早餐', '午餐', '晚餐', '加餐']

const mealForm = ref({
  mealType: '早餐',
  mealDate: new Date().toISOString().split('T')[0],
  userId: user.value?.id || 1
})

const rules = {
  mealType: [{ required: true, message: '请选择餐食类型', trigger: 'change' }],
  mealDate: [{ required: true, message: '请选择日期', trigger: 'change' }]
}

const totalCalories = computed(() => {
  return selectedFoods.value.reduce((sum, food) => sum + food.calories, 0)
})

const totalProtein = computed(() => {
  return selectedFoods.value.reduce((sum, food) => sum + (food.protein || 0), 0).toFixed(1)
})

onMounted(() => {
  loadData()
  loadFoodLibrary()
})

const loadFoodLibrary = async () => {
  try {
    const [listRes, typesRes] = await Promise.all([
      request.get('/food/list'),
      request.get('/food/types')
    ])
    if (listRes && listRes.flag === 200) {
      foodLibrary.value = listRes.data || []
    }
    if (typesRes && typesRes.flag === 200) {
      foodTypes.value = typesRes.data || []
    }
  } catch (error) {
    console.error('加载菜品库失败:', error)
  }
}

const filteredFoods = computed(() => {
  if (!selectedFoodType.value) {
    return foodLibrary.value
  }
  return foodLibrary.value.filter(food => food.foodType === selectedFoodType.value)
})

const loadFoodsByType = () => {
  selectedFood.value = null
}

const addFoodToSelected = (food) => {
  if (food) {
    selectedFoods.value.push({ ...food })
    selectedFood.value = null
  }
}

const removeSelectedFood = (index) => {
  selectedFoods.value.splice(index, 1)
}

onUnmounted(() => {
  if (pieChart) {
    pieChart.dispose()
  }
})

const formatNumber = (num) => {
  if (!num) return '0.0'
  return Number(num).toFixed(1)
}

const loadData = async () => {
  try {
    const userId = user.value?.id || 1
    const [listRes, statsRes] = await Promise.all([
      request.get(`/meal/list/${userId}/${selectedDate.value}`),
      request.get(`/meal/stats/${userId}/${selectedDate.value}`)
    ])

    if (listRes && listRes.flag === 200) {
      mealList.value = listRes.list || []
    }

    if (statsRes && statsRes.flag === 200) {
      nutritionStats.value = statsRes.stats || {}
      await nextTick()
      initPieChart()
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  }
}

const getMealItems = (mealType) => {
  return mealList.value.filter(item => item.mealType === mealType)
}

const getMealCalories = (mealType) => {
  return getMealItems(mealType).reduce((sum, item) => sum + item.calories, 0)
}

const getMealTagType = (mealType) => {
  const typeMap = {
    '早餐': 'success',
    '午餐': 'warning',
    '晚餐': 'danger',
    '加餐': 'info'
  }
  return typeMap[mealType] || 'info'
}

const initPieChart = () => {
  if (!pieChartRef.value) return

  try {
    if (pieChart) {
      pieChart.dispose()
    }

    pieChart = echarts.init(pieChartRef.value)

    const protein = Number(nutritionStats.value.totalProtein) || 0
    const carbs = Number(nutritionStats.value.totalCarbs) || 0
    const fat = Number(nutritionStats.value.totalFat) || 0

    const chartData = [
      { name: '蛋白质', value: protein },
      { name: '碳水化合物', value: carbs },
      { name: '脂肪', value: fat }
    ].filter(item => item.value > 0)

    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c}克 ({d}%)',
        z: 9999,
        textStyle: {
          fontSize: 12
        }
      },
      legend: {
        orient: 'horizontal',
        bottom: 15,
        padding: [0, 20],
        itemWidth: 15,
        itemHeight: 15,
        textStyle: {
          fontSize: 13
        }
      },
      grid: {
        top: 10,
        bottom: 80,
        left: 10,
        right: 10
      },
      series: [
        {
          name: '营养成分',
          type: 'pie',
          radius: ['40%', '65%'],
          center: ['50%', '50%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 8,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false
          },
          emphasis: {
            label: {
              show: false
            }
          },
          data: chartData.length > 0 ? chartData : [{ name: '暂无数据', value: 100 }],
          color: ['#409EFF', '#67C23A', '#E6A23C']
        }
      ]
    }

    pieChart.setOption(option)

    const resizeHandler = () => {
      pieChart && pieChart.resize()
    }
    window.addEventListener('resize', resizeHandler)
  } catch (error) {
    console.error('初始化图表失败:', error)
  }
}

const showAddDialog = () => {
  mealForm.value = {
    mealType: '早餐',
    mealDate: selectedDate.value,
    userId: user.value?.id || 1
  }
  selectedFoodType.value = ''
  selectedFood.value = null
  selectedFoods.value = []
  addDialogVisible.value = true
}

const submitAdd = async () => {
  if (selectedFoods.value.length === 0) {
    ElMessage.warning('请至少选择一道菜品')
    return
  }

  try {
    for (const food of selectedFoods.value) {
      const mealData = {
        ...mealForm.value,
        foodName: food.foodName,
        calories: food.calories,
        protein: food.protein || 0,
        carbs: food.carbs || 0,
        fat: food.fat || 0
      }
      await request.post('/meal/add', mealData)
    }
    ElMessage.success('添加成功')
    addDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('添加失败:', error)
    ElMessage.error('添加失败')
  }
}

const deleteMeal = (id) => {
  ElMessageBox.confirm('确定要删除该配餐吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await request.delete(`/meal/delete/${id}`)
    if (res.flag === 200) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error('删除失败')
    }
  })
}

const syncToCalorie = async (meal) => {
  try {
    const calorieData = {
      userId: meal.userId,
      type: '摄入',
      name: `${meal.mealType} - ${meal.foodName}`,
      calories: meal.calories,
      recordDate: meal.mealDate,
      mealPlanId: meal.id
    }
    const res = await request.post('/calorie/add', calorieData)
    if (res.flag === 200 && res.data) {
      await request.put(`/meal/updateCalorieRecordId/${meal.id}/${res.data}`)
      ElMessage.success('同步成功')
      loadData()
    } else {
      ElMessage.error('同步失败')
    }
  } catch (error) {
    console.error('同步失败:', error)
    ElMessage.error('同步失败')
  }
}
</script>

<style scoped>
.meal-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.date-selector {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
}

.stats-header {
  font-weight: 600;
}

.stats-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 15px;
  width: 100%;
  justify-content: flex-start;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 12px;
}

.stat-icon {
  width: 54px;
  height: 54px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  color: white;
  flex-shrink: 0;
}

.stat-icon.calories {
  background: linear-gradient(135deg, #FF6B6B, #FF8E53);
}

.stat-icon.protein {
  background: linear-gradient(135deg, #4ECDC4, #44A08D);
}

.stat-icon.carbs {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.stat-icon.fat {
  background: linear-gradient(135deg, #f093fb, #f5576c);
}

.stat-info {
  text-align: left;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-width: 90px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 6px;
  font-weight: 500;
}

.stat-value {
  font-size: 26px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-value span {
  font-size: 13px;
  font-weight: 400;
  color: #909399;
  margin-left: 5px;
}

.pie-chart {
  width: 100%;
  height: 350px;
  overflow: visible;
}

.meal-groups {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.meal-group {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 15px;
}

.meal-type-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.meal-type-calories {
  font-size: 14px;
  color: #909399;
  font-weight: 600;
}

.meal-items {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.meal-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.meal-item-content {
  flex: 1;
}

.meal-item-name {
  font-weight: 600;
  margin-bottom: 5px;
  color: #303133;
}

.meal-item-nutrition {
  font-size: 12px;
  color: #909399;
  display: flex;
  gap: 10px;
}

.meal-item-nutrition span {
  background: #f5f7fa;
  padding: 2px 8px;
  border-radius: 4px;
}

.meal-item-actions {
  display: flex;
  flex-direction: column;
  gap: 5px;
  margin-left: 15px;
}

.selected-foods {
  min-height: 60px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 8px;
}

.total-nutrition {
  font-size: 18px;
  font-weight: 700;
  color: #409EFF;
}
</style>
