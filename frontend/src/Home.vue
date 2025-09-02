<template>
  <div>
    <!-- メッセージを表示 -->
    <h1>{{ message }}</h1>
  </div>
</template>

<script>
// axiosをインポート
import axios from 'axios';

export default {
  // コンポーネントのデータ
  data() {
    return {
      // 表示するメッセージ
      message: 'Loading...'
    };
  },
  // メソッド
  methods: {
    // メッセージをフェッチするメソッド
    fetchMessage() {
      // メッセージをローディング中に設定
      this.message = 'Loading...';
      // APIのURL
      const url = `/api/hello`;
      // axiosでGETリクエストを送信
      axios.get(url)
        .then(response => {
          // レスポンスのデータをメッセージに設定
          this.message = response.data;
        })
        .catch(error => {
          // エラーをコンソールに出力
          console.error('Error fetching data:', error);
          // エラーメッセージを設定
          this.message = `Failed to load message from backend.`;
        });
    }
  },
  // コンポーネントがマウントされたときに呼び出される
  mounted() {
    // usのメッセージをフェッチ
    this.fetchMessage();
  }
};
</script>

<style>
/* ボタンのスタイル */
button {
  margin-right: 10px;
}
</style>
