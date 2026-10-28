<template>
  <div class="login_container">
    <div class="login_box">
      <div class="avatar_box">
        <img src="../assets/logo.png" alt />
      </div>
      <h2 class="title">欢迎登录</h2>
      <p class="subtitle">健康管理系统</p>
      <el-form ref="loginFormRef" :rules="loginRules" :model="loginForm" class="login_form" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" size="large">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" size="large" @keyup.enter="login">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item class="btns">
          <el-button type="primary" size="large" @click="login" class="login-btn">登录</el-button>
        </el-form-item>
        <el-form-item class="btns">
          <el-button size="large" @click="resetLoginFrom" class="reset-btn">重置</el-button>
        </el-form-item>
      </el-form>
      <div class="register-link">
        没有账号？<el-link type="primary" @click="goToRegister">立即注册</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const loginFormRef = ref(null)

const loginForm = ref({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ]
}

const resetLoginFrom = () => {
  loginFormRef.value.resetFields()
}

const login = async () => {
  const valid = await loginFormRef.value.validate()
  if (!valid) return
  
  const res = await request.post('/user/login', loginForm.value)
  if (res.flag === 403) {
    ElMessage.error(res.message || '账号异常')
  } else if (res.flag === 'ok') {
    window.sessionStorage.setItem('user', JSON.stringify(res.user))
    ElMessage.success('登录成功')
    router.push({ path: '/calories' })
  } else {
    ElMessage.error('用户名或密码错误')
  }
}

const goToRegister = () => {
  router.push({ path: '/register' })
}
</script>

<style scoped>
.login_container {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f7fa;
  position: relative;
  overflow: hidden;
}

.login_container::before {
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

.login_box {
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
  margin: 0 0 30px 0;
  color: #909399;
  font-size: 14px;
}

.login_form {
  width: 100%;
}

.login_form :deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.login_form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.login_form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.login_form :deep(.el-form-item) {
  margin-bottom: 22px;
}

.login_form :deep(.el-icon) {
  color: #667eea;
  font-size: 18px;
}

.btns {
  margin-bottom: 10px;
}

.login-btn {
  width: 100%;
  border-radius: 10px;
  background: #667eea;
  border: none;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 2px;
  transition: all 0.3s ease;
}

.login-btn:hover {
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

.register-link {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  color: #909399;
}

.register-link .el-link {
  font-size: 14px;
  font-weight: 500;
}
</style>
