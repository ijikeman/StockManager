<template src="./templates/HoldingList.html"></template>

<script>
import axios from 'axios';

export default {
  // コンポーネント名を'HoldingList'に設定
  name: 'HoldingList',
  // コンポーネントのデータ
  data() {
    return {
      // 保有ロットのリスト
      holdings: [],
    };
  },
  // メソッド
  methods: {
    // APIから保有ロットのリストを取得
    async fetchHoldings() {
      try {
        const response = await axios.get('/api/stock-lot');
        this.holdings = response.data;
      } catch (error) {
        console.error('保有ロットの取得中にエラーが発生しました:', error);
      }
    },
    // 売却ボタンがクリックされた時の処理
    sellHolding(id) {
      this.$router.push(`/transaction/sell/${id}`);
    },
    // 配当登録ボタンがクリックされた時の処理
    registerDividend(id) {
      this.$router.push(`/income/add/lot/${id}`);
    },
  },

  mounted() {
    this.fetchHoldings();
  }
};
</script>
