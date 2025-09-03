<template src="./templates/Footer.html"></template>

<script>
export default {
  // コンポーネント名を'Footer'に設定
  name: 'Footer',
  // コンポーネントのデータ
  data() {
    return {
      // 現在の年
      currentYear: new Date().getFullYear(),
      // システム情報
      systemInfo: {
        name: '管理システム',
        version: '1.0.0'
      }
    };
  }
};
</script>

<style scoped src="./Footer.css"></style>
