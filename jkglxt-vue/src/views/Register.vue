<template>
  <div class="register_container">
    <div class="register_box">
      <div class="avatar_box">
        <img src="../assets/logo.png" alt />
      </div>
      <h2 class="title">用户注册</h2>
      <p class="subtitle">创建您的健康管理系统账号</p>
      <el-form ref="registerFormRef" :rules="registerRules" :model="registerForm" class="register_form" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" size="large">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" size="large">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请确认密码" size="large">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱（选填）" size="large">
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item class="btns">
          <el-button type="primary" size="large" @click="register" class="register-btn">立即注册</el-button>
        </el-form-item>
        <el-form-item class="btns">
          <el-button size="large" @click="resetRegisterForm" class="reset-btn">重置</el-button>
        </el-form-item>
      </el-form>
      <div class="login-link">
        已有账号？<el-link type="primary" @click="goToLogin">立即登录</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const registerFormRef = ref(null)

const registerForm = ref({
  username: '',
  password: '',
  confirmPassword: '',
  email: ''
})

const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入用户名'))
  } else if (value.length < 3 || value.length > 20) {
    callback(new Error('用户名长度在 3 到 20 个字符'))
  } else if (!/^[a-zA-Z0-9_]+$/.test(value)) {
    callback(new Error('用户名只能包含字母、数字和下划线'))
  } else {
    callback()
  }
}

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else if (value.length < 6 || value.length > 20) {
    callback(new Error('密码长度在 6 到 20 个字符'))
  } else if (!/(?=.*[a-zA-Z])(?=.*[0-9])/.test(value)) {
    callback(new Error('密码必须包含字母和数字'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.value.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const validateEmail = (rule, value, callback) => {
  if (value === '') {
    callback()
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
    callback(new Error('请输入正确的邮箱格式'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, validator: validateUsername, trigger: 'blur' }
  ],
  password: [
    { required: true, validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ],
  email: [
    { validator: validateEmail, trigger: 'blur' }
  ]
}

const resetRegisterForm = () => {
  registerFormRef.value.resetFields()
}

const register = async () => {
  const valid = await registerFormRef.value.validate()
  if (!valid) return

  const userData = {
    username: registerForm.value.username,
    password: registerForm.value.password,
    email: registerForm.value.email
  }

  const res = await request.post('/user/register', userData)
  if (res.flag === 200) {
    ElMessage.success('注册成功，请登录')
    router.push({ path: '/login' })
  } else {
    ElMessage.error(res.message || '注册失败')
  }
}

const goToLogin = () => {
  router.push({ path: '/login' })
}
</script>

<style scoped>
.register_container {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f7fa;
  position: relative;
  overflow: hidden;
}

.register_container::before {
  content: '';
  position: absolute;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
  animation: pulse 15s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: translate(-25%, -25%) scale(1);
  }
  50% {
    transform: translate(-25%, -25%) scale(1.1);
  }
}

.register_box {
  width: 420px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 35px;
  position: relative;
  z-index: 1;
  backdrop-filter: blur(10px);
  animation: slideUp 0.5s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.avatar_box {
  width: 90px;
  height: 90px;
  margin-bottom: 15px;
  border-radius: 50%;
  background: #667eea;
  padding: 4px;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.avatar_box img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: #fff;
}

.title {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 28px;
  font-weight: 600;
  letter-spacing: 2px;
}

.subtitle {
  margin: 0 0 25px 0;
  color: #909399;
  font-size: 14px;
}

.register_form {
  width: 100%;
}

.register_form :deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.register_form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.register_form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.register_form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.register_form :deep(.el-icon) {
  color: #667eea;
  font-size: 18px;
}

.btns {
  margin-bottom: 10px;
}

.register-btn {
  width: 100%;
  border-radius: 10px;
  background: #667eea;
  border: none;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 2px;
  transition: all 0.3s ease;
}

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.reset-btn {
  width: 100%;
  border-radius: 10px;
  border: 2px solid #dcdfe6;
  background: transparent;
  font-size: 16px;
  transition: all 0.3s ease;
}

.reset-btn:hover {
  border-color: #667eea;
  color: #667eea;
  background: rgba(102, 126, 234, 0.05);
}

.login-link {
  margin-top: 20px;
  font-size: 14px;
  color: #909399;
}

.login-link .el-link {
  font-size: 14px;
  font-weight: 500;
}
</style>
