import { createRouter, createWebHistory } from 'vue-router';
import OwnerList from '../views/owner/List.vue';
import StockList from '../views/stock/List.vue';
import StockAddEdit from '../views/stock/AddEdit.vue';

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
  },
  {
    path: '/stock/add',
    name: 'StockAdd',
    component: StockAddEdit
  },
  {
    path: '/stock/edit/:id',
    name: 'StockEdit',
    component: StockAddEdit,
    props: true
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
