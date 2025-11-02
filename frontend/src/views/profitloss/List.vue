<template src="./templates/List.html"></template>

<script>
import axios from 'axios';

export default {
  name: 'ProfitLossList',
  data() {
    return {
      profitLossData: [],
      filterOwner: '',
    };
  },
  computed: {
    filteredItems() {
      if (!this.profitLossData || this.profitLossData.length === 0) {
        return [];
      }

      let items = this.profitLossData;

      // Filter by owner
      if (this.filterOwner) {
        items = items.filter(item => item.ownerName === this.filterOwner);
      }

      // Sort by stock code
      return items.sort((a, b) => {
        return a.stockCode.localeCompare(b.stockCode);
      });
    },
    uniqueOwners() {
      if (!this.profitLossData || this.profitLossData.length === 0) {
        return [];
      }
      const owners = this.profitLossData.map(item => item.ownerName).filter(Boolean);
      return [...new Set(owners)].sort();
    },
    summary() {
      if (!this.profitLossData || this.profitLossData.length === 0) {
        return {
          totalIncoming: 0,
          totalBenefit: 0,
          totalEvaluation: 0,
          totalProfitLoss: 0
        };
      }

      const totalIncoming = this.profitLossData.reduce((sum, item) => sum + (Number(item.totalIncoming) || 0), 0);
      const totalBenefit = this.profitLossData.reduce((sum, item) => sum + (Number(item.totalBenefit) || 0), 0);
      
      // Calculate evaluation profit/loss: (currentPrice - purchasePrice) * currentUnit * minimalUnit
      const totalEvaluation = this.profitLossData.reduce((sum, item) => {
        if (item.currentPrice && item.currentUnit && item.minimalUnit) {
          const evaluation = (item.currentPrice - item.purchasePrice) * item.currentUnit * item.minimalUnit;
          return sum + evaluation;
        }
        return sum;
      }, 0);
      
      const totalProfitLoss = totalIncoming + totalBenefit + totalEvaluation;

      return {
        totalIncoming,
        totalBenefit,
        totalEvaluation,
        totalProfitLoss
      };
    }
  },
  methods: {
    async fetchProfitLoss() {
      try {
        const response = await axios.get('/api/profitloss');
        this.profitLossData = response.data;
      } catch (error) {
        console.error('Error fetching profit/loss data:', error);
      }
    },
    fmt(value) {
      const n = Number(value) || 0;
      return n.toLocaleString(undefined, {minimumFractionDigits: 2, maximumFractionDigits: 2});
    },
    calculateEvaluation(item) {
      if (item.currentPrice && item.currentUnit && item.minimalUnit) {
        // Calculate evaluation using minimalUnit from API
        const evaluation = (item.currentPrice - item.purchasePrice) * item.currentUnit * item.minimalUnit;
        return evaluation;
      }
      return 0;
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
</style>
