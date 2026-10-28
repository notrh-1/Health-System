<template>
  <div class="rights-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>讨论区</span>
        </div>
      </template>
      
      <el-table :data="userList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="role" label="角色" width="200">
          <template #default="scope">
            <el-select 
              v-model="scope.row.role" 
              @change="updateRole(scope.row.id, scope.row.role)"
              placeholder="请选择角色"
            >
              <el-option label="管理员" value="admin" />
              <el-option label="普通管理员" value="manager" />
              <el-option label="普通用户" value="user" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="state" label="状态" width="150">
          <template #default="scope">
            <el-switch
              v-model="scope.row.state"
              :active-value="1"
              :inactive-value="0"
              active-text="启用"
              inactive-text="禁用"
              @change="updateState(scope.row.id, scope.row.state)"
            />
          </template>
        </el-table-column>
        <el-table-column label="角色说明" width="200">
          <template #default="scope">
            <el-tag v-if="scope.row.role === 'admin'" type="danger">拥有所有权限</el-tag>
            <el-tag v-else-if="scope.row.role === 'manager'" type="warning">部分管理权限</el-tag>
            <el-tag v-else type="info">基础访问权限</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card class="info-card">
      <template #header>
        <span>权限说明</span>
      </template>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="管理员">
          <el-tag type="danger">admin</el-tag>
          <span style="margin-left: 10px;">拥有系统所有权限，可以管理用户、权限、数据等</span>
        </el-descriptions-item>
        <el-descriptions-item label="普通管理员">
          <el-tag type="warning">manager</el-tag>
          <span style="margin-left: 10px;">拥有部分管理权限，可以查看和管理部分数据</span>
        </el-descriptions-item>
        <el-descriptions-item label="普通用户">
          <el-tag type="info">user</el-tag>
          <span style="margin-left: 10px;">基础访问权限，只能查看和使用基础功能</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import request from '@/utils/request'

const router = useRouter()
const user = ref(JSON.parse(window.sessionStorage.getItem('user') || '{}'))
const isAdmin = computed(() => {
  return user.value?.role === 'admin'
})

const userList = ref([])

onMounted(() => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员才能访问权限管理')
    router.push('/')
    return
  }
  getRightsList()
})

const getRightsList = async () => {
  const res = await request.get('/rights/list')
  if (res.flag === 200) {
    userList.value = res.users
  }
}

const updateRole = async (id, role) => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员才能修改角色')
    return
  }
  const res = await request.put(`/rights/updateRole/${id}`, { role })
  if (res.flag === 200) {
    ElMessage.success('角色修改成功')
  } else {
    ElMessage.error('角色修改失败')
    getRightsList()
  }
}

const updateState = async (id, state) => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员才能修改状态')
    return
  }
  const res = await request.put(`/rights/updateState/${id}`, { state })
  if (res.flag === 200) {
    ElMessage.success('状态修改成功')
  } else {
    ElMessage.error('状态修改失败')
    getRightsList()
  }
}
</script>

<style scoped>
.rights-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-card {
  margin-top: 20px;
}
</style>
