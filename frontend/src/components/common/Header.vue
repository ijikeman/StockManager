<template src="./templates/Header.html"></template>

<script>
export default {
  // コンポーネント名を'Header'に設定
  name: 'Header',
  // コンポーネントのデータ
  data() {
    return {
      // ナビゲーションアイテムの配列
      navItems: [
        { name: 'オーナー管理', path: '/owner' },
        { name: '銘柄管理', path: '/stock' }
      ]
    };
  },
  // メソッド
  methods: {
    // 指定されたパスに遷移する
    navigateTo(path) {
      this.$router.push(path);
    }
  }
};
</script>

<style scoped src="./Header.css"></style>
