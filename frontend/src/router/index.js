import { createRouter, createWebHistory } from 'vue-router';
import OwnerList from '../views/owner/List.vue';
// 将来的にStockListも追加予定

const routes = [
  {
    path: '/',
    redirect: '/owner'
  },
  {
    path: '/owner',
    name: 'OwnerList',
    component: OwnerList
  }
  // 将来的に以下を追加
  // {
  //   path: '/stock',
  //   name: 'StockList',
  //   component: StockList
  // }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
