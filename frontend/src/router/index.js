import { createRouter, createWebHistory } from 'vue-router';
import OwnerList from '../views/owner/List.vue';
import OwnerAddEdit from '../views/owner/AddEdit.vue';
import StockList from '../views/stock/List.vue';
import StockAddEdit from '../views/stock/AddEdit.vue';
import SectorList from '../views/sector/List.vue';
import SectorAddEdit from '../views/sector/AddEdit.vue';
import HoldingList from '../views/holding/List.vue';
import TransactionList from '../views/transaction/List.vue';
import BuyTransaction from '../views/transaction/Buy.vue';
import SellTransaction from '../views/transaction/Sell.vue';
import DisposeLot from '../views/holding/Dispose.vue';
import IncomeList from '../views/income/List.vue';
import IncomeAdd from '../views/income/Add.vue';
import IncomeEdit from '../views/income/Edit.vue';

const routes = [
  {
    path: '/',
    redirect: '/sector'
  },
  {
    path: '/holding',
    name: 'HoldingList',
    component: HoldingList
  },
  {
    path: '/holding/dispose/:lot_id',
    name: 'DisposeLot',
    component: DisposeLot,
    props: true
  },
  {
    path: '/transaction',
    name: 'TransactionList',
    component: TransactionList
  },
  {
    path: '/transaction/add',
    name: 'TransactionAdd',
    component: BuyTransaction
  },
  {
    path: '/transaction/sell/:lot_id',
    name: 'TransactionSell',
    component: SellTransaction,
    props: true
  },
  {
    path: '/income',
    name: 'IncomeList',
    component: IncomeList
  },
  {
    path: '/income/add',
    name: 'IncomeAdd',
    component: IncomeAdd
  },
  {
    path: '/income/edit/:id',
    name: 'IncomeEdit',
    component: IncomeEdit,
    props: true
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
