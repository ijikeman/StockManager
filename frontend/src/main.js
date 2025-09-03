// VueからcreateAppをインポートします
import { createApp } from 'vue'
// App.vueをインポートします
import App from './App.vue'
// router.jsからルーターをインポートします
import router from './router'
// axiosをインポートします
import axios from 'axios'
// 共通CSSをインポート
import './assets/css/common.css'

// axiosのデフォルト設定
// axios.defaults.baseURL = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080'
axios.defaults.timeout = 10000
axios.defaults.headers.common['Content-Type'] = 'application/json'

// axiosのレスポンスインターセプター（エラーハンドリング）
axios.interceptors.response.use(
  response => response,
  error => {
    console.error('API Error:', error)
    
    // エラーメッセージの統一化
    let errorMessage = 'サーバーエラーが発生しました'
    
    if (error.response) {
      switch (error.response.status) {
        case 400:
          errorMessage = '入力内容に問題があります'
          break
        case 401:
          errorMessage = '認証が必要です'
          break
        case 403:
          errorMessage = 'アクセス権限がありません'
          break
        case 404:
          errorMessage = 'データが見つかりません'
          break
        case 500:
          errorMessage = 'サーバー内部エラーが発生しました'
          break
        default:
          errorMessage = `エラーが発生しました (${error.response.status})`
      }
    } else if (error.request) {
      errorMessage = 'サーバーに接続できません'
    }
    
    alert(errorMessage)
    return Promise.reject(error)
  }
)

// Vueアプリケーションを作成
const app = createApp(App)

// axiosをグローバルプロパティとして追加（オプション）
app.config.globalProperties.$http = axios

// グローバルエラーハンドラー
app.config.errorHandler = (err, instance, info) => {
  console.error('Vue Error:', err, info)
}

// 開発環境での詳細なログ出力
if (process.env.NODE_ENV === 'development') {
  app.config.performance = true
}

// ルーターを適用して、idが'app'の要素にマウント
app.use(router).mount('#app')
