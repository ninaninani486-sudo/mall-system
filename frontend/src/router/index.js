import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/pay/wechat/:orderNo',
    name: 'WechatPay',
    component: () => import('../views/WechatPay.vue')
  },
  {
    path: '/pay/alipay/:orderNo',
    name: 'Alipay',
    component: () => import('../views/Alipay.vue')
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../views/Layout.vue'),
    redirect: '/products',
    children: [
      {
        path: 'products',
        name: 'Products',
        component: () => import('../views/Products.vue')
      },
      {
        path: 'product/:id',
        name: 'ProductDetail',
        component: () => import('../views/ProductDetail.vue')
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('../views/Cart.vue')
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('../views/Orders.vue')
      },
      {
        path: 'order/:orderNo',
        name: 'OrderDetail',
        component: () => import('../views/OrderDetail.vue')
      },
      {
        path: 'pay/:orderNo',
        name: 'Pay',
        component: () => import('../views/Pay.vue')
      },
      {
        path: 'account',
        name: 'Account',
        component: () => import('../views/Account.vue')
      },
      {
        path: 'admin',
        name: 'Admin',
        component: () => import('../views/Admin.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
