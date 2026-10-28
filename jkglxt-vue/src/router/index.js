import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/Layout.vue'),
    redirect: '/calories',
    children: [
      {
        path: 'home',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue')
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/User.vue')
      },
      {
        path: 'rights',
        name: 'Rights',
        component: () => import('@/views/Rights.vue')
      },
      {
        path: 'health',
        name: 'Health',
        component: () => import('@/views/Health.vue')
      },
      {
        path: 'goods',
        name: 'Goods',
        component: () => import('@/views/Goods.vue')
      },
      {
        path: 'introduction',
        name: 'Introduction',
        component: () => import('@/views/Introduction.vue')
      },
      {
        path: 'calories',
        name: 'Calories',
        component: () => import('@/views/Calories.vue')
      },
      {
        path: 'food',
        name: 'Food',
        component: () => import('@/views/Food.vue')
      },
      {
        path: 'knowledge',
        name: 'Knowledge',
        component: () => import('@/views/Knowledge.vue')
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue')
      },
      {
        path: 'project',
        name: 'Project',
        component: () => import('@/views/Project.vue')
      },
      {
        path: 'foodManage',
        name: 'FoodManage',
        component: () => import('@/views/FoodManage.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const user = window.sessionStorage.getItem('user')
  if (to.path === '/login' || to.path === '/register') {
    if (user) {
      next('/calories')
    } else {
      next()
    }
  } else {
    if (user) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router
