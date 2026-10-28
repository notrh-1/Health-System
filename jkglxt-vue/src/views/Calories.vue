<template>
  <div class="calorie-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>卡路里管理</span>
          <el-button type="primary" @click="showAddDialog">添加记录</el-button>
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
        <el-col :span="12">
          <el-card class="stats-card">
            <template #header>
              <div class="stats-header">
                <span>今日统计</span>
              </div>
            </template>
            <div class="stats-content">
              <div class="stat-item intake">
                <div class="stat-label">摄入</div>
                <div class="stat-value">{{ totalIntake }} <span>千卡</span></div>
              </div>
              <div class="stat-divider"></div>
              <div class="stat-item consume">
                <div class="stat-label">消耗</div>
                <div class="stat-value">{{ totalConsume }} <span>千卡</span></div>
              </div>
              <div class="stat-divider"></div>
              <div class="stat-item balance">
                <div class="stat-label">结余</div>
                <div class="stat-value" :class="{ positive: totalBalance >= 0, negative: totalBalance < 0 }">
                  {{ totalBalance >= 0 ? '+' : '' }}{{ totalBalance }} <span>千卡</span>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <div class="stats-header">
                <span>卡路里分布</span>
              </div>
            </template>
            <div ref="pieChartRef" class="pie-chart"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-card class="list-card" style="margin-top: 20px;">
        <template #header>
          <div class="stats-header">
            <span>记录列表</span>
          </div>
        </template>
        <el-table :data="recordList" stripe style="width: 100%">
          <el-table-column prop="type" label="类型" width="100">
            <template #default="{ row }">
              <el-tag :type="row.type === '摄入' ? 'success' : 'danger'">
                {{ row.type }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="calories" label="卡路里" width="120">
            <template #default="{ row }">
              {{ row.calories }} 千卡
            </template>
          </el-table-column>
          <el-table-column prop="recordDate" label="日期" width="120" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button type="danger" size="small" text @click="deleteRecord(row.id)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-card>

    <el-dialog v-model="addDialogVisible" title="添加记录" width="500px">
      <el-form :model="recordForm" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="recordForm.type">
            <el-radio label="摄入">摄入</el-radio>
            <el-radio label="消耗">消耗</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="recordForm.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="卡路里" prop="calories">
          <el-input-number v-model="recordForm.calories" :min="1" :max="10000" style="width: 100%" />
        </el-form-item>
        <el-form-item label="日期" prop="recordDate">
          <el-date-picker
            v-model="recordForm.recordDate"
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
        <el-button type="primary" @click="submitAdd">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
import request from '@/utils/request'

const pieChartRef = ref(null)
let pieChart = null
const selectedDate = ref(new Date().toISOString().split('T')[0])
const recordList = ref([])
const addDialogVisible = ref(false)
const formRef = ref(null)
const user = ref(JSON.parse(window.sessionStorage.getItem('user')))

const recordForm = ref({
  type: '摄入',
  name: '',
  calories: 100,
  recordDate: new Date().toISOString().split('T')[0],
  userId: user.value?.id || 1
})

const rules = {
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  calories: [{ required: true, message: '请输入卡路里', trigger: 'blur' }],
  recordDate: [{ required: true, message: '请选择日期', trigger: 'change' }]
}

const totalIntake = computed(() => {
  return recordList.value.filter(r => r.type === '摄入').reduce((sum, r) => sum + r.calories, 0)
})

const totalConsume = computed(() => {
  return recordList.value.filter(r => r.type === '消耗').reduce((sum, r) => sum + r.calories, 0)
})

const totalBalance = computed(() => {
  return totalIntake.value - totalConsume.value
})

onMounted(() => {
  loadData()
})

onUnmounted(() => {
  if (pieChart) {
    pieChart.dispose()
  }
})

const loadData = async () => {
  try {
    const userId = user.value?.id || 1
    const [listRes, statsRes] = await Promise.all([
      request.get(`/calorie/list/${userId}/${selectedDate.value}`),
      request.get(`/calorie/stats/${userId}/${selectedDate.value}`)
    ])

    if (listRes && listRes.flag === 200) {
      recordList.value = listRes.list || []
    }

    if (statsRes && statsRes.flag === 200) {
      await nextTick()
      initPieChart(statsRes.stats || [])
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  }
}

const initPieChart = (stats) => {
  if (!pieChartRef.value) return

  try {
    if (pieChart) {
      pieChart.dispose()
    }

    pieChart = echarts.init(pieChartRef.value)

    const chartData = stats && stats.length > 0 
      ? stats.map(item => ({
          name: item.type,
          value: item.total
        }))
      : []

    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} 千卡 ({d}%)',
        z: 9999
      },
      legend: {
        orient: 'vertical',
        right: 10,
        top: 'center',
        itemWidth: 15,
        itemHeight: 15,
        textStyle: {
          fontSize: 13
        }
      },
      series: [
        {
          name: '卡路里',
          type: 'pie',
          radius: ['35%', '55%'],
          center: ['45%', '50%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
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
          data: chartData.length > 0 ? chartData : [
            { name: '暂无数据', value: 100 }
          ],
          color: ['#67C23A', '#F56C6C']
        }
      ]
    }

    pieChart.setOption(option)

    const resizeHandler = () => {
      pieChart && pieChart.resize()
    }
    window.addEventListener('resize', resizeHandler)
    onUnmounted(() => {
      window.removeEventListener('resize', resizeHandler)
    })
  } catch (error) {
    console.error('初始化图表失败:', error)
  }
}

const showAddDialog = () => {
  recordForm.value = {
    type: '摄入',
    name: '',
    calories: 100,
    recordDate: selectedDate.value,
    userId: user.value?.id || 1
  }
  addDialogVisible.value = true
}

const submitAdd = async () => {
  const valid = await formRef.value.validate()
  if (!valid) return

  const res = await request.post('/calorie/add', recordForm.value)
  if (res.flag === 200) {
    ElMessage.success('添加成功')
    addDialogVisible.value = false
    loadData()
  } else {
    ElMessage.error('添加失败')
  }
}

const deleteRecord = (id) => {
  ElMessageBox.confirm('确定要删除该记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await request.delete(`/calorie/delete/${id}`)
    if (res.flag === 200) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error('删除失败')
    }
  })
}
</script>

<style scoped>
.calorie-container {
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

.stats-card,
.chart-card,
.list-card {
  height: 100%;
}

.stats-header {
  font-weight: 600;
}

.stats-content {
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 20px 0;
}

.stat-item {
  text-align: center;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}

.stat-value span {
  font-size: 14px;
  font-weight: 400;
  color: #909399;
  margin-left: 4px;
}

.stat-item.intake .stat-value {
  color: #67C23A;
}

.stat-item.consume .stat-value {
  color: #F56C6C;
}

.stat-item.balance .stat-value.positive {
  color: #E6A23C;
}

.stat-item.balance .stat-value.negative {
  color: #F56C6C;
}

.stat-divider {
  width: 1px;
  height: 60px;
  background: #ebeef5;
}

.pie-chart {
  width: 100%;
  height: 300px;
  overflow: visible;
}
</style>
