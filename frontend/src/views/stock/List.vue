<template src="./templates/StockList.html"></template>

<style>
.earnings-future {
  color: #28a745; /* 緑色 */
}
.earnings-past {
  color: #dc3545; /* 赤色 */
}
.earnings-today {
  color: #007bff; /* 青色 */
}
</style>

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
      priceChange: { label: '前日比', formatter: (value, stock) => this.calculatePriceChange(stock) },
      priceChangeRate: { label: '前日比率(%)', formatter: (value, stock) => this.calculatePriceChangeRate(stock) },
      incoming: { label: '配当金' },
      earningsDate: { 
        label: '業績発表日',
        formatter: (value) => this.formatEarningsDate(value)
      },
      latestDisclosureDate: {
        label: '適時開示日',
        formatter: (value) => this.formatDisclosureDate(value)
      },
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
    // 前日比を計算
    calculatePriceChange(stock) {
      if (stock.currentPrice && stock.previousPrice) {
        return stock.currentPrice - stock.previousPrice;
      }
      return null;
    },
    // 前日比率を計算
    calculatePriceChangeRate(stock) {
      if (stock.currentPrice && stock.previousPrice && stock.previousPrice !== 0) {
        return ((stock.currentPrice - stock.previousPrice) / stock.previousPrice) * 100;
      }
      return null;
    },
    // 前日比のフォーマット
    formatPriceChange(stock) {
      const change = this.calculatePriceChange(stock);
      const rate = this.calculatePriceChangeRate(stock);
      
      if (change === null || rate === null) {
        return '-';
      }
      
      const sign = change >= 0 ? '+' : '';
      const arrow = change >= 0 ? '↑' : '↓';
      return `${arrow} ${sign}${change.toFixed(1)} (${sign}${rate.toFixed(2)}%)`;
    },
    // 前日比の色を取得
    getPriceChangeClass(stock) {
      const change = this.calculatePriceChange(stock);
      if (change === null) {
        return '';
      }
      return change >= 0 ? 'price-increase' : 'price-decrease';
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
    getEarningDateClass(date) {
      if (!date) return '';
      
      const earningsDate = new Date(date);
      const today = new Date();
      
      earningsDate.setHours(0, 0, 0, 0);
      today.setHours(0, 0, 0, 0);
      
      const diffTime = earningsDate - today;
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      
      if (diffDays > 0) {
        return 'earnings-future';
      } else if (diffDays < 0) {
        return 'earnings-past';
      } else {
        return 'earnings-today';
      }
    },

    formatEarningsDate(date) {
      if (!date) return '-';
      
      const earningsDate = new Date(date);
      const today = new Date();
      
      // 時間を無視して日付のみを比較
      earningsDate.setHours(0, 0, 0, 0);
      today.setHours(0, 0, 0, 0);
      
      const diffTime = earningsDate - today;
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      
      let suffix = '';
      if (diffDays > 0) {
        suffix = `(+${diffDays}日)`;
      } else if (diffDays < 0) {
        suffix = `(${diffDays}日)`;
      } else {
        suffix = '(当日)';
      }
      
      // YYY-MM-DD形式の日付を YYYY/MM/DD に変換
      const formattedDate = date.split('-').join('/');
      return `${formattedDate} ${suffix}`;
    },

    formatDisclosureDate(date) {
      if (!date) return '-';
      
      // YYYY-MM-DD形式の日付を YYYY/MM/DD に変換
      const formattedDate = date.split('-').join('/');
      return formattedDate;
    },

    getDisclosureUrl(stock) {
      // URLが保存されている場合はそれを使用、なければデフォルトの開示ページへのリンクを返す
      if (stock.latestDisclosureUrl) {
        return stock.latestDisclosureUrl;
      } else if (stock.code) {
        return `https://finance.yahoo.co.jp/quote/${stock.code}.T/disclosure`;
      }
      return null;
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
          if (column.formatter) {
            return column.formatter(value, stock);
          }
          return value;
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

.disclosure-link {
  color: #007bff;
  text-decoration: none;
}

.disclosure-link:hover {
  text-decoration: underline;
}
</style>
