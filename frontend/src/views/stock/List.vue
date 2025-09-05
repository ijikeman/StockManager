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
      selectedStocks: [],
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
    goToAddStock() {
      this.$router.push('/stock/add');
    },
    editStock(id) {
      this.$router.push(`/stock/edit/${id}`);
    },
    async deleteStock(id) {
      if (confirm('本当にこの在庫を削除しますか？')) {
        try {
          await axios.delete(`/api/stocks/${id}`);
          this.fetchStocks(); // リストを再読み込み
        } catch (error) {
          console.error('在庫の削除中にエラーが発生しました:', error);
          alert('削除に失敗しました。');
        }
      }
    },
    async updateSelectedStocks() {
      if (this.selectedStocks.length === 0) {
        alert('更新する銘柄を選択してください。');
        return;
      }
      try {
        await Promise.all(this.selectedStocks.map(code =>
          axios.post(`/api/stocks/${code}/update`)
        ));
        alert('選択した銘柄の株価を更新しました。');
        this.fetchStocks(); // データを再取得して表示を更新
        this.selectedStocks = []; // 選択をクリア
      } catch (error) {
        console.error('株価の更新中にエラーが発生しました:', error);
        alert('株価の更新に失敗しました。');
      }
    },
    toggleSelectAll(event) {
      if (event.target.checked) {
        this.selectedStocks = this.stocks.map(stock => stock.code);
      } else {
        this.selectedStocks = [];
      }
    }
  },
  
  mounted() {
    this.fetchStocks();
  }
};
</script>
