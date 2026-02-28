import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/systemd',
      name: 'Systemd',
      component: () => import('@/components/Systemd.vue'),
    },
  ],
})

export default router
