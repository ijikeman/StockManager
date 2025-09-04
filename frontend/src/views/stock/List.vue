<template src="./templates/StockList.html"></template>

<script>
import axios from 'axios';

export default {
  // コンポーネント名を'StockList'に設定
  name: 'StockList',
  // コンポーネントのデータ
  data() {
    return {
      // 在庫のリスト
      stocks: [],
    };
  },
  // メソッド
  methods: {
    // APIから在庫のリストを取得
    async fetchStocks() {
      try {
        const response = await axios.get('/api/stocks');
        this.stocks = response.data;
      } catch (error) {
        console.error('在庫の取得中にエラーが発生しました:', error);
      }
    },
  },
  
  mounted() {
    this.fetchStocks();
  }
};
</script>
