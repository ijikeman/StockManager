<template src="./templates/List.html"></template>

<script>
import axios from 'axios'; // HTTPリクエストを行うためのライブラリをインポート

export default {
  name: 'ProfitLossList', // コンポーネント名を定義
  data() {
    return {
      unrealizedData: [],  // 含み損益データ (getProfitStockLotLoss)
      realizedData: [],    // 確定損益データ (getProfitLoss)
      filterOwner: '', // 所有者でフィルタリングするための変数
      activeTab: 'unrealized', // 現在のタブ ('unrealized' または 'realized')
      // Sorting state for unrealized table
      unrealizedSortBy: 'stockCode', // デフォルトは銘柄コードでソート
      unrealizedSortOrder: 'asc', // 'asc' or 'desc'
      // Sorting state for realized table
      realizedSortBy: 'stockCode', // デフォルトは銘柄コードでソート
      realizedSortOrder: 'asc', // 'asc' or 'desc'
    };
  },
  computed: {
    // 後方互換性のため、profitLossDataを含み損益データに紐付け
    profitLossData() {
      return this.unrealizedData;
    },
    filteredUnrealizedItems() {
      if (!this.unrealizedData || this.unrealizedData.length === 0) {
        return [];
      }

      let items = this.unrealizedData;

      // 所有者でフィルタリング
      if (this.filterOwner) {
        items = items.filter(item => item.ownerName === this.filterOwner);
      }

      // ソート処理
      return items.sort((a, b) => {
        let aValue, bValue;

        switch(this.unrealizedSortBy) {
          case 'stockCode':
            aValue = a.stockCode || '';
            bValue = b.stockCode || '';
            return this.unrealizedSortOrder === 'asc' 
              ? aValue.localeCompare(bValue)
              : bValue.localeCompare(aValue);
          
          case 'stockName':
            aValue = a.stockName || '';
            bValue = b.stockName || '';
            return this.unrealizedSortOrder === 'asc'
              ? aValue.localeCompare(bValue)
              : bValue.localeCompare(aValue);
          
          case 'buyTransactionDate':
            aValue = a.buyTransactionDate || '';
            bValue = b.buyTransactionDate || '';
            return this.unrealizedSortOrder === 'asc'
              ? aValue.localeCompare(bValue)
              : bValue.localeCompare(aValue);
          
          case 'purchasePrice':
            aValue = Number(a.purchasePrice) || 0;
            bValue = Number(b.purchasePrice) || 0;
            return this.unrealizedSortOrder === 'asc' ? aValue - bValue : bValue - aValue;
          
          case 'currentPrice':
            aValue = Number(a.currentPrice) || 0;
            bValue = Number(b.currentPrice) || 0;
            return this.unrealizedSortOrder === 'asc' ? aValue - bValue : bValue - aValue;
          
          case 'currentUnit':
            aValue = Number(a.currentUnit) || 0;
            bValue = Number(b.currentUnit) || 0;
            return this.unrealizedSortOrder === 'asc' ? aValue - bValue : bValue - aValue;
          
          case 'totalIncoming':
            aValue = (a.totalIncoming * a.currentUnit * a.minimalUnit) || 0;
            bValue = (b.totalIncoming * b.currentUnit * b.minimalUnit) || 0;
            return this.unrealizedSortOrder === 'asc' ? aValue - bValue : bValue - aValue;
          
          case 'totalBenefit':
            aValue = (a.totalBenefit * a.currentUnit * a.minimalUnit) || 0;
            bValue = (b.totalBenefit * b.currentUnit * b.minimalUnit) || 0;
            return this.unrealizedSortOrder === 'asc' ? aValue - bValue : bValue - aValue;
          
          case 'evaluationProfitloss':
            aValue = this.calculateEvaluation(a);
            bValue = this.calculateEvaluation(b);
            return this.unrealizedSortOrder === 'asc' ? aValue - bValue : bValue - aValue;
          
          default:
            return a.stockCode.localeCompare(b.stockCode);
        }
      });
    },
    filteredRealizedItems() {
      if (!this.realizedData || this.realizedData.length === 0) {
        return [];
      }

      let items = this.realizedData;

      // 所有者でフィルタリング
      if (this.filterOwner) {
        items = items.filter(item => item.ownerName === this.filterOwner);
      }

      // ソート処理
      return items.sort((a, b) => {
        let aValue, bValue;

        switch(this.realizedSortBy) {
          case 'stockCode':
            aValue = a.stockCode || '';
            bValue = b.stockCode || '';
            return this.realizedSortOrder === 'asc'
              ? aValue.localeCompare(bValue)
              : bValue.localeCompare(aValue);
          
          case 'stockName':
            aValue = a.stockName || '';
            bValue = b.stockName || '';
            return this.realizedSortOrder === 'asc'
              ? aValue.localeCompare(bValue)
              : bValue.localeCompare(aValue);
          
          case 'buyTransactionDate':
            aValue = a.buyTransactionDate || '';
            bValue = b.buyTransactionDate || '';
            return this.realizedSortOrder === 'asc'
              ? aValue.localeCompare(bValue)
              : bValue.localeCompare(aValue);
          
          case 'purchasePrice':
            aValue = Number(a.purchasePrice) || 0;
            bValue = Number(b.purchasePrice) || 0;
            return this.realizedSortOrder === 'asc' ? aValue - bValue : bValue - aValue;
          
          case 'sellTransactionDate':
            aValue = a.sellTransactionDate || '';
            bValue = b.sellTransactionDate || '';
            return this.realizedSortOrder === 'asc'
              ? aValue.localeCompare(bValue)
              : bValue.localeCompare(aValue);
          
          case 'sellPrice':
            aValue = Number(a.sellPrice) || 0;
            bValue = Number(b.sellPrice) || 0;
            return this.realizedSortOrder === 'asc' ? aValue - bValue : bValue - aValue;
          
          case 'sellUnit':
            aValue = Number(a.sellUnit) || 0;
            bValue = Number(b.sellUnit) || 0;
            return this.realizedSortOrder === 'asc' ? aValue - bValue : bValue - aValue;
          
          case 'profitLoss':
            aValue = Number(a.profitLoss) || 0;
            bValue = Number(b.profitLoss) || 0;
            return this.realizedSortOrder === 'asc' ? aValue - bValue : bValue - aValue;
          
          case 'totalIncoming':
            aValue = (a.totalIncoming * a.sellUnit * a.minimalUnit) || 0;
            bValue = (b.totalIncoming * b.sellUnit * b.minimalUnit) || 0;
            return this.realizedSortOrder === 'asc' ? aValue - bValue : bValue - aValue;
          
          case 'totalBenefit':
            aValue = (a.totalBenefit * a.sellUnit * a.minimalUnit) || 0;
            bValue = (b.totalBenefit * b.sellUnit * b.minimalUnit) || 0;
            return this.realizedSortOrder === 'asc' ? aValue - bValue : bValue - aValue;
          
          case 'totalProfitLoss':
            aValue = (Number(a.profitLoss) || 0) + (a.totalIncoming * a.sellUnit * a.minimalUnit || 0) + (a.totalBenefit * a.sellUnit * a.minimalUnit || 0);
            bValue = (Number(b.profitLoss) || 0) + (b.totalIncoming * b.sellUnit * b.minimalUnit || 0) + (b.totalBenefit * b.sellUnit * b.minimalUnit || 0);
            return this.realizedSortOrder === 'asc' ? aValue - bValue : bValue - aValue;
          
          default:
            return a.stockCode.localeCompare(b.stockCode);
        }
      });
    },
    // 後方互換性のため
    filteredItems() {
      return this.filteredUnrealizedItems;
    },
    uniqueOwners() {
      const unrealizedOwners = (this.unrealizedData || []).map(item => item.ownerName).filter(Boolean);
      const realizedOwners = (this.realizedData || []).map(item => item.ownerName).filter(Boolean);
      const allOwners = [...unrealizedOwners, ...realizedOwners];
      return [...new Set(allOwners)].sort(); // 重複を削除してソート
    },
    // 含み損益集計
    unrealizedSummary() {
      if (!this.unrealizedData || this.unrealizedData.length === 0) {
        return {
          totalIncoming: 0,
          totalBenefit: 0,
          totalEvaluation: 0,
          totalProfitLoss: 0
        };
      }
      // 各項目の合計を計算
      // 配当金合計
      const totalIncoming = this.unrealizedData.reduce((sum, item) => {
        const incomingAmount = item.totalIncoming * item.currentUnit * item.minimalUnit;
        return sum + (incomingAmount || 0);
      }, 0);
      // 売却益合計s
      const totalBenefit = this.unrealizedData.reduce((sum, item) => {
        const benefitAmount = item.totalBenefit * item.currentUnit * item.minimalUnit;
        return sum + (benefitAmount || 0);
      }, 0);
      // 含み損益合計
      const totalEvaluation = this.unrealizedData.reduce((sum, item) => {
        return sum + (item.evaluationProfitloss || 0);
      }, 0);
      // 総合計
      const totalProfitLoss = totalIncoming + totalBenefit + totalEvaluation;

      return {
        totalIncoming,
        totalBenefit,
        totalEvaluation,
        totalProfitLoss
      };
    },

    // 確定損益集計
    realizedSummary() {
      if (!this.realizedData || this.realizedData.length === 0) {
        return {
          totalIncoming: 0,
          totalBenefit: 0,
          totalEvaluation: 0,
          totalProfitLoss: 0,
          count: 0
        };
      }

      // 各項目の合計を計算
      // 配当金合計 
      const totalIncoming = this.realizedData.reduce((sum, item) => {
        const incomingAmount = (item.totalIncoming || 0) * (item.sellUnit || 0) * (item.minimalUnit || 1);
        return sum + incomingAmount;
      }, 0);
      // 優待金合計
      const totalBenefit = this.realizedData.reduce((sum, item) => {
        const benefitAmount = (item.totalBenefit || 0) * (item.sellUnit || 0) * (item.minimalUnit || 1);
        return sum + benefitAmount;
      }, 0);
      // 株式損益合計
      const totalEvaluation = this.realizedData.reduce((sum, item) => {
        return sum + (Number(item.profitLoss) || 0);
      }, 0);

      // 総合計
      const totalProfitLoss = totalIncoming + totalBenefit + totalEvaluation;

      return {
        totalIncoming,
        totalBenefit,
        totalEvaluation,
        totalProfitLoss,
        count: this.realizedData.length
      };
    },
    // 後方互換性のため
    summary() {
      return this.unrealizedSummary;
    }
  },
  methods: {
    async fetchProfitLoss() {
      try {
        // 含み損益データを取得するAPI呼び出し
        // Unrealized profit/loss (未実現損益) のデータをバックエンドから取得
        const unrealizedResponse = await axios.get('/api/profitloss');
        this.unrealizedData = unrealizedResponse.data; // 取得したデータを `unrealizedData` に格納

        // 確定損益データを取得するAPI呼び出し
        // Realized profit/loss (確定損益) のデータをバックエンドから取得
        const realizedResponse = await axios.get('/api/profitloss/realized', {
          params: {
            ownerId: this.filterOwner || null // フィルタリング条件としてオーナーIDを渡す (未指定の場合は null)
          }
        });
        this.realizedData = realizedResponse.data; // 取得したデータを `realizedData` に格納
      } catch (error) {
        // API呼び出し中にエラーが発生した場合のログ出力
        console.error('Error fetching profit/loss data:', error); // エラー内容をコンソールに出力
      }
    },
    fmt(value) {
      // 数値をローカライズされた形式でフォーマットするヘルパー関数
      // 例: 1234.56 -> "1,235" (四捨五入して整数表示)
      const n = Number(value) || 0;
      return Math.round(n).toLocaleString(undefined, {minimumFractionDigits: 0, maximumFractionDigits: 0});
    },
    calculateEvaluation(item) {
      // 含み損益の計算を行う
      // backendから提供されるevaluationProfitlossを優先的に使用
      if (item.evaluationProfitloss !== null && item.evaluationProfitloss !== undefined) {
        return Number(item.evaluationProfitloss); // backendからの値をそのまま使用
      }
      // Fallbackとして、現在価格と購入価格を使用して計算
      if (item.currentPrice && item.currentUnit && item.minimalUnit) {
        const evaluation = (item.currentPrice - item.purchasePrice) * item.currentUnit * item.minimalUnit;
        return evaluation; // 計算結果を返す
      }
      return 0; // データが不足している場合は0を返す
    },
    setActiveTab(tab) {
      // タブの切り替え処理
      // ユーザーが選択したタブ ('unrealized' または 'realized') を設定
      this.activeTab = tab;
    },
    sortUnrealizedBy(column) {
      // 同じカラムをクリックした場合は昇順/降順を切り替え
      if (this.unrealizedSortBy === column) {
        this.unrealizedSortOrder = this.unrealizedSortOrder === 'asc' ? 'desc' : 'asc';
      } else {
        // 新しいカラムの場合は昇順から開始
        this.unrealizedSortBy = column;
        this.unrealizedSortOrder = 'asc';
      }
    },
    sortRealizedBy(column) {
      // 同じカラムをクリックした場合は昇順/降順を切り替え
      if (this.realizedSortBy === column) {
        this.realizedSortOrder = this.realizedSortOrder === 'asc' ? 'desc' : 'asc';
      } else {
        // 新しいカラムの場合は昇順から開始
        this.realizedSortBy = column;
        this.realizedSortOrder = 'asc';
      }
    }
  },
  mounted() {
    this.fetchProfitLoss();
  }
};
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.tab-container {
  margin-bottom: 20px;
  border-bottom: 2px solid #dee2e6;
}

.tab-buttons {
  display: flex;
  gap: 10px;
}

.tab-button {
  padding: 10px 20px;
  border: none;
  background: none;
  cursor: pointer;
  font-size: 16px;
  color: #6c757d;
  border-bottom: 3px solid transparent;
  transition: all 0.3s ease;
}

.tab-button.active {
  color: #007bff;
  border-bottom-color: #007bff;
  font-weight: bold;
}

.tab-button:hover {
  color: #007bff;
}

.summary-section {
  margin-bottom: 30px;
}

.summary-card {
  background-color: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  padding: 20px;
}

.summary-card h3 {
  margin-bottom: 15px;
  color: #495057;
  font-size: 18px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  padding: 10px;
  background-color: white;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.summary-item.total {
  grid-column: 1 / -1;
  background-color: #e7f3ff;
  border: 2px solid #007bff;
  font-weight: bold;
}

.summary-label {
  color: #6c757d;
  font-size: 14px;
}

.summary-value {
  font-size: 18px;
  font-weight: bold;
}

.summary-value.positive {
  color: #28a745;
}

.summary-value.negative {
  color: #dc3545;
}

.filter-section {
  margin-bottom: 20px;
  display: flex;
  gap: 20px;
  align-items: center;
}

.filter-select {
  margin-left: 10px;
  padding: 5px 10px;
  border: 1px solid #ced4da;
  border-radius: 4px;
  font-size: 14px;
}

.profit-loss-table {
  margin-top: 20px;
}

.type-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
  text-align: center;
  min-width: 60px;
}

.type-badge.type-income {
  background-color: #d1ecf1;
  color: #0c5460;
}

.type-badge.type-benefit {
  background-color: #d4edda;
  color: #155724;
}

.type-badge.type-sell {
  background-color: #fff3cd;
  color: #856404;
}

.type-badge.type-evaluation {
  background-color: #e2e3e5;
  color: #383d41;
}

.price-cell {
  text-align: right;
  font-weight: bold;
}

.price-cell.positive {
  color: #28a745;
}

.price-cell.negative {
  color: #dc3545;
}

.text-center {
  text-align: center;
}

.nisa-row {
  background-color: #c8f7c5 !important;
}

.nisa-row td {
  background-color: #c8f7c5 !important;
}

/* Sortable header styles */
.sortable-header {
  cursor: pointer;
  user-select: none;
  position: relative;
  padding-right: 20px;
}

.sortable-header:hover {
  background-color: #e9ecef;
}

.sort-indicator {
  position: absolute;
  right: 5px;
  font-size: 12px;
  color: #6c757d;
}

.sort-indicator.active {
  color: #007bff;
  font-weight: bold;
}
</style>
