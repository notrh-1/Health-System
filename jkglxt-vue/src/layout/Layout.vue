<template>
  <el-container class="home_container">
    <el-header>
      <div class="logo">
        <img src="../assets/logo2.png" alt />
        <span>健康管理系统</span>
      </div>
      <el-button type="info" @click="logout">退出登录</el-button>
    </el-header>
    <el-container>
      <el-aside :width="iscollapse ? '64px' : '200px'">
        <div class="toggle-button" @click="toggleCollapase">|||</div>
        <el-menu
          background-color="#545c64"
          text-color="#fff"
          active-text-color="#409eff"
          unique-opened
          :collapse="iscollapse"
          collapse-transition="false"
          router
        >
          <el-sub-menu :index="item.id + ''" v-for="item in menuList" :key="item.id">
            <template #title>
              <i :class="iconsObject[item.id]"></i>
              <span>{{ item.title }}</span>
            </template>
            <el-menu-item :index="it.path" v-for="it in item.subMenus" :key="it.id">
              <template #title>
                <i :class="iconsObject[it.id]"></i>
                <span>{{ it.title }}</span>
              </template>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const menuList = ref([])
const iscollapse = ref(false)
const user = ref(JSON.parse(window.sessionStorage.getItem('user') || '{}'))

const isAdmin = computed(() => {
  return user.value?.role === 'admin'
})

const iconsObject = {
  100: 'iconfont icon-guanliyuan',
  200: 'iconfont icon-yundong',
  101: 'iconfont icon-denglu-copy',
  102: 'iconfont icon-mima',
  103: 'iconfont icon-yundong',
  104: 'iconfont icon-liebiao',
  105: 'iconfont icon-guanliyuan',
  106: 'iconfont icon-caipin-',
  201: 'iconfont icon-shu',
  202: 'iconfont icon-kaluli',
  203: 'iconfont icon-shiwu',
  204: 'iconfont icon-yundong',
  205: 'iconfont icon-yundong'
}

onMounted(() => {
  const userData = window.sessionStorage.getItem('user')
  if (!userData) {
    router.push('/login')
  } else {
    getMenuList()
  }
})

const getMenuList = async () => {
    const res = await request.get('/menu/getMenus')
    if (res.flag !== 200) {
      return ElMessage.error('获取菜单数据失败')
    }
    let menus = res.menus
    
    const adminMenu = menus.find(m => m.id === 100)
    const sportMenu = menus.find(m => m.id === 200)
    
    if (adminMenu) {
      adminMenu.title = '功能区'
    }
    
    if (adminMenu && sportMenu && adminMenu.subMenus) {
      const chatMenu = adminMenu.subMenus.find(s => s.title === '聊天区')
      const profileMenu = adminMenu.subMenus.find(s => s.title === '个人中心')
      
      if (chatMenu) {
        adminMenu.subMenus = adminMenu.subMenus.filter(s => s.title !== '聊天区')
        sportMenu.subMenus.push(chatMenu)
      }
      if (profileMenu) {
        adminMenu.subMenus = adminMenu.subMenus.filter(s => s.title !== '个人中心')
        sportMenu.subMenus.push(profileMenu)
      }
      
      adminMenu.subMenus.push({
        id: 106,
        title: '食物库管理',
        path: '/foodManage',
        icon: 'iconfont icon-caipin-'
      })
    }
    
    if (!isAdmin.value) {
      menus = menus.filter(menu => menu.id === 200)
    }
    menuList.value = menus
  }

const logout = () => {
  window.sessionStorage.removeItem('user')
  router.push('/login')
}

const toggleCollapase = () => {
  iscollapse.value = !iscollapse.value
}
</script>

<style scoped>
.home_container {
  height: 100vh;
}

.el-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-left: 0;
  background-color: #14041f;
  color: #fff;
  font-size: 20px;
}

.el-header > div {
  display: flex;
  align-items: center;
  height: 100%;
}

.el-header .logo {
  display: flex;
  align-items: center;
}

.el-header .logo img {
  width: 64px;
  height: 60px;
}

.el-header .logo span {
  margin-left: 15px;
}

.el-aside {
  background-color: #545c64;
}

.el-aside .el-menu {
  border-right: none;
}

.el-main {
  background-color: #f5efef;
}

.toggle-button {
  background-color: #545c64;
  font-size: 10px;
  line-height: 24px;
  color: #fff;
  text-align: center;
  letter-spacing: 0.2em;
  cursor: pointer;
}
</style>
