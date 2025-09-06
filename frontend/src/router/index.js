import { createRouter, createWebHistory } from 'vue-router';
import OwnerList from '../views/owner/List.vue';
import OwnerAddEdit from '../views/owner/AddEdit.vue';
import StockList from '../views/stock/List.vue';
import StockAddEdit from '../views/stock/AddEdit.vue';
import SectorList from '../views/sector/List.vue';
import SectorAddEdit from '../views/sector/AddEdit.vue';

const routes = [
  {
    path: '/',
    redirect: '/sector'
  },
  {
    path: '/owner',
    name: 'OwnerList',
    component: OwnerList
  },
  {
    path: '/owner/add',
    name: 'OwnerAdd',
    component: OwnerAddEdit
  },
  {
    path: '/owner/edit/:id',
    name: 'OwnerEdit',
    component: OwnerAddEdit,
    props: true
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
  },
  {
    path: '/sector',
    name: 'SectorList',
    component: SectorList
  },
  {
    path: '/sector/add',
    name: 'SectorAdd',
    component: SectorAddEdit
  },
  {
    path: '/sector/edit/:id',
    name: 'SectorEdit',
    component: SectorAddEdit,
    props: true
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
