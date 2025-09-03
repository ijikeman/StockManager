<template src="./templates/App.html"></template>

<script>
// ヘッダーとフッターの共通コンポーネントをインポート
import Header from './components/common/Header.vue';
import Footer from './components/common/Footer.vue';

export default {
  // コンポーネント名を'App'に設定
  name: 'App',
  // 使用するコンポーネントを登録
  components: {
    Header,
    Footer
  }
};
</script>

<style src="./App.css"></style>
