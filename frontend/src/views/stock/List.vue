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
        const response = await axios.get('/api/stock');
        this.stocks = response.data;
      } catch (error) {
        console.error('在庫の取得中にエラーが発生しました:', error);
      }
    },
    goToAddStock() {
      this.$router.push('/stock/add');
    },
    editStock(id) {
      this.$router.push(`/stock/edit/${id}`);
    },
    async deleteStock(id) {
      if (confirm('本当にこの在庫を削除しますか？')) {
        try {
          await axios.delete(`/api/stock/${id}`);
          this.fetchStocks(); // リストを再読み込み
        } catch (error) {
          console.error('在庫の削除中にエラーが発生しました:', error);
          alert('削除に失敗しました。');
        }
      }
    }
  },
  
  mounted() {
    this.fetchStocks();
  }
};
</script>
