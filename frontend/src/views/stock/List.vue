<template src="./templates/StockList.html"></template>

<script>
import axios from 'axios';

export default {
  // コンポーネント名を'StockList'に設定
  name: 'StockList',
  // コンポーネントのデータ
  data() {
    const csvColumns = {
      code: { label: 'コード' },
      name: { label: '名前' },
      currentPrice: { label: '現在の株価' },
      previousPrice: { label: '前日終値' },
      priceChange: { label: '前日比' },
      priceChangeRate: { label: '前日比率(%)' },
      incoming: { label: '配当金' },
      earningsDate: { label: '業績発表日' },
      sector: { label: 'セクター', formatter: (value) => value.name },
    };

    return {
      // 銘柄のリスト
      stocks: [],
      selectedStocks: [],
      csvDelimiter: ',',
      csvColumns,
      selectedCsvColumns: Object.keys(csvColumns),
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
    // 前日比のフォーマット
    formatPriceChange(stock) {
      if (stock.priceChange === null || stock.priceChange === undefined) {
        return '-';
      }
      const change = stock.priceChange;
      const rate = stock.priceChangeRate !== null && stock.priceChangeRate !== undefined 
        ? stock.priceChangeRate.toFixed(2) 
        : '0.00';
      const sign = change >= 0 ? '+' : '';
      const arrow = change >= 0 ? '↑' : '↓';
      return `${arrow} ${sign}${change} (${sign}${rate}%)`;
    },
    // 前日比の色を取得
    getPriceChangeClass(priceChange) {
      if (priceChange === null || priceChange === undefined) {
        return '';
      }
      return priceChange >= 0 ? 'price-increase' : 'price-decrease';
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
      if (this.selectedCsvColumns.length === 0) {
        alert('出力する項目を1つ以上選択してください。');
        return;
      }

      const delimiter = this.csvDelimiter;
      const header = this.selectedCsvColumns.map(key => this.csvColumns[key].label).join(delimiter) + '\n';

      const csvRows = this.stocks.map(stock => {
        return this.selectedCsvColumns.map(key => {
          const column = this.csvColumns[key];
          const value = stock[key];
          return column.formatter ? column.formatter(value) : value;
        }).join(delimiter);
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

<style scoped>
.price-increase {
  color: green;
  font-weight: bold;
}

.price-decrease {
  color: red;
  font-weight: bold;
}
</style>
