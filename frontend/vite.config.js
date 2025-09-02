// node:urlからfileURLToPathとURLをインポート
import { fileURLToPath, URL } from 'node:url'

// viteからdefineConfigをインポート
import { defineConfig } from 'vite'
// @vitejs/plugin-vueからvueをインポート
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
// 設定をエクスポート
export default defineConfig({
  // プラグインの設定
  plugins: [
    // vueプラグインを使用
    vue(),
  ],
  // パスの解決に関する設定
  resolve: {
    // エイリアスの設定
    alias: {
      // '@'を'./src'へのパスに設定
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  // 開発サーバーの設定
  server: {
    // プロキシの設定
    proxy: {
      // '/api'へのリクエストをプロキシする
      '/api': {
        // プロキシ先のターゲット
        target: 'http://localhost:8080',
        // オリジンを変更
        changeOrigin: true,
        // パスを書き換える
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
