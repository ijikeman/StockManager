import { createRouter, createWebHistory } from 'vue-router';
import Dashboard from '../views/dashboard/Dashboard.vue';
import OwnerList from '../views/owner/List.vue';
import OwnerAddEdit from '../views/owner/AddEdit.vue';
import StockList from '../views/stock/List.vue';
import StockAddEdit from '../views/stock/AddEdit.vue';
import SectorList from '../views/sector/List.vue';
import SectorAddEdit from '../views/sector/AddEdit.vue';
import StockLotList from '../views/stocklot/StockLotList.vue';
import StockLotAdd from '../views/stocklot/Add.vue';
import StockLotSell from '../views/stocklot/Sell.vue';
import IncomeList from '../views/income/List.vue';
import IncomeAdd from '../views/income/Add.vue';
import IncomeEdit from '../views/income/Edit.vue';
import BenefitList from '../views/benefit/List.vue';
import BenefitAdd from '../views/benefit/Add.vue';
import BenefitEdit from '../views/benefit/Edit.vue';
import ProfitLossList from '../views/profitloss/List.vue';

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: Dashboard
  },
  {
    path: '/profitloss',
    name: 'ProfitLossList',
    component: ProfitLossList
  },
  {
    path: '/stocklot',
    name: 'StockLotList',
    component: StockLotList
  },
  {
    path: '/stocklot/add',
    name: 'StockLotAdd',
    component: StockLotAdd
  },
  {
    path: '/stocklot/sell/:id',
    name: 'StockLotSell',
    component: StockLotSell,
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
    path: '/benefit',
    name: 'BenefitList',
    component: BenefitList
  },
  {
    path: '/benefit/add',
    name: 'BenefitAdd',
    component: BenefitAdd
  },
  {
    path: '/benefit/edit/:id',
    name: 'BenefitEdit',
    component: BenefitEdit,
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
