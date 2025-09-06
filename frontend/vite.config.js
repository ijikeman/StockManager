// node:urlからfileURLToPathとURLをインポート
import { fileURLToPath, URL } from 'node:url'

// viteからdefineConfigをインポート
import { defineConfig } from 'vite'
// @vitejs/plugin-vueからvueをインポート
import vue from '@vitejs/plugin-vue'
// vite-plugin-mockからviteMockServeをインポート
import { viteMockServe } from 'vite-plugin-mock'

// https://vitejs.dev/config/
// 設定をエクスポート
export default defineConfig(({ command }) => ({
  // プラグインの設定
  plugins: [
    // vueプラグインを使用
    vue(),
    // vite-plugin-mockプラグインを使用
    viteMockServe({
      mockPath: 'mock',
      localEnabled: command === 'serve',
    }),
  ],
  // パスの解決に関する設定
  resolve: {
    // エイリアスの設定
    alias: {
      // '@'を'./src'へのパスに設定
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  }
}))
