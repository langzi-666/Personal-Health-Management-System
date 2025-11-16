import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../store/auth.js'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { title: '首页', requiresAuth: false }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { title: '注册', requiresAuth: false }
  },
  {
    path: '/profile/:id',
    name: 'Profile',
    component: () => import('../views/Profile.vue'),
    meta: { title: '个人信息', requiresAuth: true }
  },
  {
    path: '/layout',
    component: () => import('../views/Layout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '仪表板', requiresAuth: true }
      },
      {
        path: 'weight-record',
        name: 'WeightRecord',
        component: () => import('../views/WeightRecord.vue'),
        meta: { title: '体重记录', requiresAuth: true }
      },
      {
        path: 'blood-pressure-record',
        name: 'BloodPressureRecord',
        component: () => import('../views/BloodPressureRecord.vue'),
        meta: { title: '血压记录', requiresAuth: true }
      },
      {
        path: 'glucose-record',
        name: 'GlucoseRecord',
        component: () => import('../views/GlucoseRecord.vue'),
        meta: { title: '血糖记录', requiresAuth: true }
      },
      {
        path: 'exercise-record',
        name: 'ExerciseRecord',
        component: () => import('../views/ExerciseRecord.vue'),
        meta: { title: '运动记录', requiresAuth: true }
      },
      {
        path: 'sleep-record',
        name: 'SleepRecord',
        component: () => import('../views/SleepRecord.vue'),
        meta: { title: '睡眠记录', requiresAuth: true }
      },
      {
        path: 'diet-record',
        name: 'DietRecord',
        component: () => import('../views/DietRecord.vue'),
        meta: { title: '饮食记录', requiresAuth: true }
      },
      {
        path: 'mental-health-record',
        name: 'MentalHealthRecord',
        component: () => import('../views/MentalHealthRecord.vue'),
        meta: { title: '心理健康', requiresAuth: true }
      },
      {
        path: 'analysis',
        name: 'Analysis',
        component: () => import('../views/Analysis.vue'),
        meta: { title: '数据分析', requiresAuth: true }
      },
      {
        path: 'reminders',
        name: 'Reminders',
        component: () => import('../views/Reminders.vue'),
        meta: { title: '提醒管理', requiresAuth: true }
      },
      {
        path: 'alerts',
        name: 'Alerts',
        component: () => import('../views/Alerts.vue'),
        meta: { title: '告警管理', requiresAuth: true }
      },
      {
        path: 'notifications',
        name: 'NotificationCenter',
        component: () => import('../views/NotificationCenter.vue'),
        meta: { title: '通知中心', requiresAuth: true }
      },
      {
        path: 'health-advice',
        name: 'HealthAdvice',
        component: () => import('../views/HealthAdvice.vue'),
        meta: { title: '健康建议', requiresAuth: true }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('../views/Profile.vue'),
        meta: { title: '设置', requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title || '个人健康管理系统'
  
  const authStore = useAuthStore()
  const isLoggedIn = authStore.isLoggedIn()
  
  // 如果路由需要认证
  if (to.meta.requiresAuth) {
    if (isLoggedIn) {
      next()
    } else {
      // 未登录，重定向到登录页面
      next({ name: 'Login', query: { redirect: to.fullPath } })
    }
  } else if ((to.name === 'Login' || to.name === 'Register') && isLoggedIn) {
    // 已登录用户访问登录/注册页面，重定向到首页
    next({ name: 'Home' })
  } else {
    next()
  }
})

export default router
