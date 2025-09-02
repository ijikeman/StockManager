// VueからcreateAppをインポートします
import { createApp } from 'vue'
// App.vueをインポートします
import App from './App.vue'
// router.jsからルーターをインポートします
import router from './router'

// Appコンポーネントをルートコンポーネントとしてアプリケーションを作成し、
// ルーターを適用して、idが'app'の要素にマウントします
createApp(App).use(router).mount('#app')
