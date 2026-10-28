<template>
  <div class="user-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <el-button v-if="isAdmin" type="primary" @click="showAddDialog">添加用户</el-button>
        </div>
      </template>
      
      <el-table :data="userList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="password" label="密码" width="150" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.role === 'admin'" type="danger">管理员</el-tag>
            <el-tag v-else-if="scope.row.role === 'manager'" type="warning">普通管理员</el-tag>
            <el-tag v-else type="info">普通用户</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="state" label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.state === 1" type="success">启用</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="isAdmin" label="操作" width="200">
          <template #default="scope">
            <el-button size="small" type="primary" @click="showEditDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteUser(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="userForm" :rules="rules" ref="userFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" :disabled="!isAdmin" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="userForm.password" type="password" :disabled="!isAdmin" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" :disabled="!isAdmin" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="请选择角色" style="width: 100%" :disabled="!isAdmin">
            <el-option label="管理员" value="admin" />
            <el-option label="普通管理员" value="manager" />
            <el-option label="普通用户" value="user" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="state">
          <el-radio-group v-model="userForm.state" :disabled="!isAdmin">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button v-if="isAdmin" type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const userList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const userFormRef = ref(null)
const isEdit = ref(false)
const user = ref(JSON.parse(window.sessionStorage.getItem('user') || '{}'))

const isAdmin = computed(() => {
  return user.value?.role === 'admin'
})

const userForm = ref({
  id: null,
  username: '',
  password: '',
  email: '',
  role: 'user',
  state: 1
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  state: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

onMounted(() => {
  getUserList()
})

const getUserList = async () => {
  const res = await request.get('/user/list')
  if (res.flag === 200) {
    userList.value = res.users
  }
}

const showAddDialog = () => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员才能添加用户')
    return
  }
  isEdit.value = false
  dialogTitle.value = '添加用户'
  userForm.value = {
    id: null,
    username: '',
    password: '',
    email: '',
    role: 'user',
    state: 1
  }
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员才能编辑用户')
    return
  }
  isEdit.value = true
  dialogTitle.value = '编辑用户'
  userForm.value = { ...row }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员才能操作')
    return
  }
  const valid = await userFormRef.value.validate()
  if (!valid) return

  if (isEdit.value) {
    const res = await request.post('/user/update', userForm.value)
    if (res.flag === 200) {
      ElMessage.success('修改成功')
      dialogVisible.value = false
      getUserList()
    } else {
      ElMessage.error('修改失败')
    }
  } else {
    const res = await request.post('/user/add', userForm.value)
    if (res.flag === 200) {
      ElMessage.success('添加成功')
      dialogVisible.value = false
      getUserList()
    } else {
      ElMessage.error('添加失败')
    }
  }
}

const deleteUser = (id) => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员才能删除用户')
    return
  }
  ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await request.delete(`/user/delete/${id}`)
    if (res.flag === 200) {
      ElMessage.success('删除成功')
      getUserList()
    } else {
      ElMessage.error('删除失败')
    }
  })
}
</script>

<style scoped>
.user-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
