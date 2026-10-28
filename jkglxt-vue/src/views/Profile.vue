<template>
  <div class="profile-container">
    <div class="page-header">
      <h2>个人中心</h2>
      <p class="subtitle">管理您的个人信息和账号设置</p>
    </div>
    
    <el-row :gutter="24">
      <el-col :xs="24" :md="12">
        <el-card class="profile-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-icon class="header-icon"><User /></el-icon>
                <span>基础信息</span>
              </div>
              <el-button type="primary" size="small" @click="editMode = true" v-if="!editMode">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
            </div>
          </template>
          
          <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="80px">
            <div class="avatar-section">
              <div class="avatar-wrapper">
                <el-upload
                  class="avatar-uploader"
                  :show-file-list="false"
                  :action="uploadUrl"
                  :on-success="handleAvatarSuccess"
                  :before-upload="beforeAvatarUpload"
                  :disabled="!editMode"
                >
                  <img v-if="profileForm.avatar" :src="profileForm.avatar" class="avatar" />
                  <div v-else class="avatar-placeholder">
                    <el-icon class="avatar-uploader-icon" :size="48"><Plus /></el-icon>
                  </div>
                </el-upload>
                <div class="avatar-tip" v-if="editMode">
                  <span>点击上传头像</span>
                  <el-button 
                    v-if="profileForm.avatar" 
                    type="danger" 
                    size="small" 
                    text 
                    @click="deleteAvatar"
                  >
                    <el-icon><Delete /></el-icon>
                    删除头像
                  </el-button>
                </div>
              </div>
            </div>
            
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="姓名" prop="realName">
                  <el-input v-model="profileForm.realName" :disabled="!editMode" placeholder="请输入姓名" clearable></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="年龄" prop="age">
                  <el-input-number v-model="profileForm.age" :disabled="!editMode" :min="1" :max="150" style="width: 100%"></el-input-number>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="性别" prop="gender">
                  <el-radio-group v-model="profileForm.gender" :disabled="!editMode">
                    <el-radio label="男">
                      <el-icon><Male /></el-icon>
                      男
                    </el-radio>
                    <el-radio label="女">
                      <el-icon><Female /></el-icon>
                      女
                    </el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="身高" prop="height">
                  <el-input v-model="profileForm.height" :disabled="!editMode" placeholder="请输入身高" clearable>
                    <template #append>cm</template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="体重" prop="weight">
                  <el-input v-model="profileForm.weight" :disabled="!editMode" placeholder="请输入体重" clearable>
                    <template #append>kg</template>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-form-item v-if="editMode" class="button-group">
              <el-button type="primary" @click="saveProfile">
                <el-icon><Check /></el-icon>
                保存
              </el-button>
              <el-button @click="cancelEdit">
                <el-icon><Close /></el-icon>
                取消
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :md="12">
        <el-card class="info-card" shadow="hover" style="margin-bottom: 24px">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-icon class="header-icon" style="color: #67c23a"><DataLine /></el-icon>
                <span>健康指标</span>
              </div>
            </div>
          </template>
          <el-row :gutter="12">
            <el-col :span="8">
              <div class="metric-item">
                <div class="metric-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
                  <el-icon><Odometer /></el-icon>
                </div>
                <div class="metric-label">BMI指数</div>
                <div class="metric-value">{{ bmi }}</div>
                <div class="metric-status" :class="bmiStatus">{{ bmiText }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="metric-item">
                <div class="metric-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
                  <el-icon><Goods /></el-icon>
                </div>
                <div class="metric-label">标准体重</div>
                <div class="metric-value">{{ standardWeight }}</div>
                <div class="metric-unit">kg</div>
                <div class="metric-status">参考值</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="metric-item">
                <div class="metric-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
                  <el-icon><DataAnalysis /></el-icon>
                </div>
                <div class="metric-label">体重状态</div>
                <div class="metric-value">{{ weightDiff }}</div>
                <div class="metric-status" :class="weightStatus">{{ weightText }}</div>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <el-card class="account-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-icon class="header-icon" style="color: #409eff"><Lock /></el-icon>
                <span>账号设置</span>
              </div>
            </div>
          </template>
          
          <div class="account-section">
            <div class="section-item">
              <div class="section-header">
                <div class="section-icon" style="background: #667eea">
                  <el-icon><User /></el-icon>
                </div>
                <div class="section-title">
                  <h4>修改用户名</h4>
                  <p>更改您的登录用户名</p>
                </div>
              </div>
              <el-form ref="usernameFormRef" :model="usernameForm" :rules="usernameRules" label-width="100px" class="account-form">
                <el-form-item label="当前用户名">
                  <div class="current-value">
                    <el-tag type="info" size="large">{{ user.username }}</el-tag>
                  </div>
                </el-form-item>
                <el-form-item label="新用户名" prop="newUsername">
                  <el-input 
                    v-model="usernameForm.newUsername" 
                    placeholder="请输入新用户名" 
                    clearable 
                    prefix-icon="User"
                    size="large"
                  ></el-input>
                </el-form-item>
                <el-form-item class="form-button">
                  <el-button type="primary" size="large" @click="changeUsername">
                    <el-icon><Edit /></el-icon>
                    修改用户名
                  </el-button>
                </el-form-item>
              </el-form>
            </div>

            <div class="section-divider"></div>

            <div class="section-item">
              <div class="section-header">
                <div class="section-icon" style="background: #f093fb">
                  <el-icon><Lock /></el-icon>
                </div>
                <div class="section-title">
                  <h4>修改密码</h4>
                  <p>保障您的账户安全</p>
                </div>
              </div>
              <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px" class="account-form">
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input 
                    v-model="passwordForm.oldPassword" 
                    type="password" 
                    placeholder="请输入原密码" 
                    show-password 
                    prefix-icon="Lock"
                    size="large"
                  ></el-input>
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input 
                    v-model="passwordForm.newPassword" 
                    type="password" 
                    placeholder="请输入新密码（6-20位）" 
                    show-password 
                    prefix-icon="Key"
                    size="large"
                  ></el-input>
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input 
                    v-model="passwordForm.confirmPassword" 
                    type="password" 
                    placeholder="请再次输入新密码" 
                    show-password 
                    prefix-icon="Key"
                    size="large"
                  ></el-input>
                </el-form-item>
                <el-form-item class="form-button">
                  <el-button type="primary" size="large" @click="changePassword">
                    <el-icon><Lock /></el-icon>
                    修改密码
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, Edit, Delete, Check, Close, User, Lock, Key, 
  DataLine, Odometer, Goods, DataAnalysis, Male, Female 
} from '@element-plus/icons-vue'
import request from '@/utils/request'

const profileFormRef = ref(null)
const usernameFormRef = ref(null)
const passwordFormRef = ref(null)
const editMode = ref(false)

const profileForm = ref({
  realName: '',
  age: null,
  gender: '',
  height: '',
  weight: '',
  avatar: ''
})

const usernameForm = ref({
  newUsername: ''
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const uploadUrl = 'http://localhost:9000/upload/avatar'

const user = ref(JSON.parse(window.sessionStorage.getItem('user') || '{}'))

const profileRules = {
  realName: [
    { max: 50, message: '姓名不能超过50个字符', trigger: 'blur' }
  ],
  age: [
    { type: 'number', min: 1, max: 150, message: '年龄在1-150之间', trigger: 'blur' }
  ],
  height: [
    { pattern: /^\d+(\.\d{1,2})?$/, message: '请输入正确的身高', trigger: 'blur' }
  ],
  weight: [
    { pattern: /^\d+(\.\d{1,2})?$/, message: '请输入正确的体重', trigger: 'blur' }
  ]
}

const usernameRules = {
  newUsername: [
    { required: true, message: '请输入新用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在2-20个字符', trigger: 'blur' }
  ]
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const bmi = computed(() => {
  const height = parseFloat(profileForm.value.height)
  const weight = parseFloat(profileForm.value.weight)
  if (!height || !weight || height <= 0) return '--'
  const heightM = height / 100
  return (weight / (heightM * heightM)).toFixed(1)
})

const bmiText = computed(() => {
  const bmiValue = parseFloat(bmi.value)
  if (isNaN(bmiValue)) return ''
  if (bmiValue < 18.5) return '偏瘦'
  if (bmiValue < 24) return '正常'
  if (bmiValue < 28) return '偏胖'
  return '肥胖'
})

const bmiStatus = computed(() => {
  const bmiValue = parseFloat(bmi.value)
  if (isNaN(bmiValue)) return ''
  if (bmiValue < 18.5 || bmiValue >= 28) return 'danger'
  if (bmiValue < 24) return 'success'
  return 'warning'
})

const standardWeight = computed(() => {
  const height = parseFloat(profileForm.value.height)
  if (!height || height <= 0) return '--'
  return ((height - 100) * 0.9).toFixed(1)
})

const weightDiff = computed(() => {
  const weight = parseFloat(profileForm.value.weight)
  const standard = parseFloat(standardWeight.value)
  if (isNaN(weight) || isNaN(standard)) return '--'
  const diff = weight - standard
  return diff > 0 ? `+${diff.toFixed(1)}` : `${diff.toFixed(1)}`
})

const weightText = computed(() => {
  const diff = parseFloat(weightDiff.value)
  if (isNaN(diff)) return ''
  if (Math.abs(diff) <= 5) return '正常范围'
  return diff > 0 ? '超重' : '偏轻'
})

const weightStatus = computed(() => {
  const diff = parseFloat(weightDiff.value)
  if (isNaN(diff)) return ''
  if (Math.abs(diff) <= 5) return 'success'
  return 'warning'
})

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

const handleAvatarSuccess = (res) => {
  if (res.flag === 200) {
    profileForm.value.avatar = res.avatarUrl
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(res.message)
  }
}

const deleteAvatar = async () => {
  try {
    await ElMessageBox.confirm('确定要删除头像吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    profileForm.value.avatar = ''
    ElMessage.success('头像删除成功')
  } catch {
  }
}

const getProfile = async () => {
  const res = await request.get(`/profile/get/${user.value.id}`)
  if (res.flag === 200 && res.profile) {
    profileForm.value = {
      realName: res.profile.realName || '',
      age: res.profile.age || null,
      gender: res.profile.gender || '',
      height: res.profile.height || '',
      weight: res.profile.weight || '',
      avatar: res.profile.avatar || ''
    }
  }
}

const saveProfile = async () => {
  const valid = await profileFormRef.value.validate()
  if (!valid) return
  
  const res = await request.post('/profile/save', profileForm.value)
  if (res.flag === 200) {
    ElMessage.success(res.message)
    editMode.value = false
    getProfile()
  } else {
    ElMessage.error(res.message)
  }
}

const cancelEdit = () => {
  editMode.value = false
  getProfile()
}

const changeUsername = async () => {
  const valid = await usernameFormRef.value.validate()
  if (!valid) return
  
  try {
    await ElMessageBox.confirm('确定要修改用户名吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await request.post('/user/updateUsername', {
      id: user.value.id,
      username: usernameForm.value.newUsername
    })
    
    if (res.flag === 200) {
      ElMessage.success(res.message)
      user.value.username = usernameForm.value.newUsername
      window.sessionStorage.setItem('user', JSON.stringify(user.value))
      usernameForm.value.newUsername = ''
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('修改用户名失败')
    }
  }
}

const changePassword = async () => {
  const valid = await passwordFormRef.value.validate()
  if (!valid) return
  
  try {
    await ElMessageBox.confirm('确定要修改密码吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await request.post('/user/updatePassword', {
      id: user.value.id,
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    
    if (res.flag === 200) {
      ElMessage.success(res.message)
      passwordForm.value.oldPassword = ''
      passwordForm.value.newPassword = ''
      passwordForm.value.confirmPassword = ''
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('修改密码失败')
    }
  }
}

onMounted(() => {
  getProfile()
})
</script>

<style scoped>
.profile-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  color: #303133;
  font-size: 28px;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.subtitle {
  color: #909399;
  font-size: 14px;
  margin: 0;
}

.profile-card,
.info-card,
.account-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-icon {
  font-size: 20px;
  color: #409eff;
}

.avatar-section {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.avatar-wrapper {
  text-align: center;
}

.avatar-uploader {
  border: 2px dashed #dcdfe6;
  border-radius: 50%;
  cursor: pointer;
  width: 140px;
  height: 140px;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
}

.avatar-uploader:hover {
  border-color: #409eff;
  transform: scale(1.02);
}

.avatar-placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
}

.avatar-uploader-icon {
  color: #c0c4cc;
  transition: color 0.3s;
}

.avatar-uploader:hover .avatar-uploader-icon {
  color: #409eff;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.button-group {
  margin-top: 8px;
}

.metric-item {
  text-align: center;
  padding: 20px 12px;
  background: linear-gradient(135deg, #f5f7fa 0%, #fafafa 100%);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.metric-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.metric-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 12px;
  color: white;
  font-size: 24px;
}

.metric-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.metric-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.metric-unit {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.metric-status {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 20px;
  background: #e4e7ed;
  color: #606266;
  margin-top: 8px;
  display: inline-block;
  font-weight: 500;
}

.metric-status.success {
  background: #f0f9ff;
  color: #67c23a;
}

.metric-status.warning {
  background: #fdf6ec;
  color: #e6a23c;
}

.metric-status.danger {
  background: #fef0f0;
  color: #f56c6c;
}

.account-section {
  padding: 8px 0;
}

.section-item {
  padding: 24px 0;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.section-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.section-title h4 {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 4px 0;
}

.section-title p {
  font-size: 13px;
  color: #909399;
  margin: 0;
}

.section-divider {
  height: 1px;
  background: linear-gradient(90deg, transparent 0%, #e4e7ed 50%, transparent 100%);
  margin: 0;
}

.account-form {
  padding-left: 72px;
}

.current-value {
  padding: 8px 0;
}

.form-button {
  margin-top: 8px;
}
</style>
