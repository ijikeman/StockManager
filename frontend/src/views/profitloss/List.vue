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

      // 銘柄コードでソート
      return items.sort((a, b) => {
        return a.stockCode.localeCompare(b.stockCode);
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

      // 銘柄コードと売却日でソート
      return items.sort((a, b) => {
        const codeCompare = a.stockCode.localeCompare(b.stockCode);
        if (codeCompare !== 0) return codeCompare;
        
        // 同じ銘柄コードの場合、売却日でソート
        const dateA = a.sellTransactionDate || '';
        const dateB = b.sellTransactionDate || '';
        return dateB.localeCompare(dateA); // 最新の日付を先に
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
      // 売却益合計
      const totalBenefit = this.realizedData.reduce((sum, item) => {
        const benefitAmount = (item.totalBenefit || 0) * (item.sellUnit || 0) * (item.minimalUnit || 1);
        return sum + benefitAmount;
      }, 0);
      // 損益合計
      const totalProfitLoss = this.realizedData.reduce((sum, item) => {
        return sum + (Number(item.profitLoss) || 0);
      }, 0);

      return {
        totalIncoming,
        totalBenefit,
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
      // 例: 1234.56 -> "1,234.56"
      const n = Number(value) || 0;
      return n.toLocaleString(undefined, {minimumFractionDigits: 2, maximumFractionDigits: 2});
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
</style>
