<template src="./templates/StockList.html"></template>

<script>
import axios from 'axios';

export default {
  // コンポーネント名を'StockList'に設定
  name: 'StockList',
  // コンポーネントのデータ
  data() {
    return {
      // 銘柄のリスト
      stocks: [],
      selectedStocks: [],
    };
  },
  // メソッド
  methods: {
    // APIから銘柄のリストを取得
    async fetchStocks() {
      try {
        const response = await axios.get('/api/stock');
        this.stocks = response.data;
      } catch (error) {
        console.error('銘柄の取得中にエラーが発生しました:', error);
      }
    },
    goToAddStock() {
      this.$router.push('/stock/add');
    },
    editStock(id) {
      this.$router.push(`/stock/edit/${id}`);
    },
    async deleteStock(id) {
      if (confirm('本当にこの銘柄を削除しますか？')) {
        try {
          await axios.delete(`/api/stock/${id}`);
          this.fetchStocks(); // リストを再読み込み
        } catch (error) {
          console.error('銘柄の削除中にエラーが発生しました:', error);
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
          axios.post(`/api/stock/${code}/update`)
        ));
        alert('選択した銘柄の株価を更新しました。');
        this.fetchStocks(); // データを再取得して表示を更新
        this.selectedStocks = []; // 選択をクリア
      } catch (error) {
        console.error('株価の更新中にエラーが発生しました:', error);
        alert('株価の更新に失敗しました。');
      }
    },
    exportCSV() {
      if (this.stocks.length === 0) {
        alert('出力するデータがありません。');
        return;
      }

      const header = 'code,price\n';
      const csvRows = this.stocks.map(stock => {
        return `${stock.code},${stock.current_price}`;
      });

      const csvString = header + csvRows.join('\n');

      const bom = new Uint8Array([0xEF, 0xBB, 0xBF]);
      const blob = new Blob([bom, csvString], { type: 'text/csv;charset=utf-8;' });
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.setAttribute('href', url);
      link.setAttribute('download', 'stocks.csv');
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);
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
