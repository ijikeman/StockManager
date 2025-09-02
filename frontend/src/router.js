// vue-routerからcreateRouterとcreateWebHistoryをインポートします
import { createRouter, createWebHistory } from 'vue-router'
// Owner.vueとHome.vueをインポートします
import Owner from './Owner.vue'
import Home from './Home.vue'

// ルートの配列を定義します
const routes = [
  {
    // パスが'/'の場合
    path: '/',
    // ルートの名前は'Home'
    name: 'Home',
    // Homeコンポーネントをレンダリングします
    component: Home
  },
  {
    // パスが'/owner'の場合
    path: '/owner',
    // ルートの名前は'Owner'
    name: 'Owner',
    // Ownerコンポーネントをレンダリングします
    component: Owner
  }
]

// ルーターを作成します
const router = createRouter({
  // HTML5のHistory APIを使用します
  history: createWebHistory(),
  // 上で定義したルートを使用します
  routes
})

// ルーターをエクスポートします
export default router