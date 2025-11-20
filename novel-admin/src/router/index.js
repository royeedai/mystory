import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('../views/layout/Layout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/Dashboard.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'novel',
        name: 'NovelList',
        component: () => import('../views/novel/NovelList.vue'),
        meta: { title: '小说管理', roles: ['ADMIN', 'AUTHOR'] }
      },
      {
        path: 'novel/edit/:id?',
        name: 'NovelEdit',
        component: () => import('../views/novel/NovelEdit.vue'),
        meta: { title: '编辑小说', roles: ['ADMIN', 'AUTHOR'] }
      },
      {
        path: 'novel/:id/chapters',
        name: 'ChapterList',
        component: () => import('../views/novel/ChapterList.vue'),
        meta: { title: '章节管理', roles: ['ADMIN', 'AUTHOR'] }
      },
      {
        path: 'audit',
        name: 'AuditList',
        component: () => import('../views/audit/AuditList.vue'),
        meta: { title: '审核管理', roles: ['ADMIN'] }
      },
      {
        path: 'user',
        name: 'UserList',
        component: () => import('../views/user/UserList.vue'),
        meta: { title: '用户管理', roles: ['ADMIN'] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL), // 使用Vite的BASE_URL，自动适配/admin/路径
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.token) {
    next('/login')
  } else {
    next()
  }
})

export default router
