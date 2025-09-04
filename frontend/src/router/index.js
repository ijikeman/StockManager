import { createRouter, createWebHistory } from 'vue-router';
import OwnerList from '../views/owner/List.vue';
import StockList from '../views/stock/List.vue';

const routes = [
  {
    path: '/',
    redirect: '/owner'
  },
  {
    path: '/owner',
    name: 'OwnerList',
    component: OwnerList
  },
  {
    path: '/stock',
    name: 'StockList',
    component: StockList
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
