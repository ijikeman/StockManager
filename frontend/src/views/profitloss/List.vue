<template src="./templates/List.html"></template>

<script>
import axios from 'axios';

export default {
  name: 'ProfitLossList',
  data() {
    return {
      profitLossData: null,
      filterType: 'all',
      filterOwner: '',
    };
  },
  computed: {
    filteredItems() {
      if (!this.profitLossData || !this.profitLossData.items) {
        return [];
      }

      let items = this.profitLossData.items;

      // Filter by type
      if (this.filterType !== 'all') {
        items = items.filter(item => item.type === this.filterType);
      }

      // Filter by owner
      if (this.filterOwner) {
        items = items.filter(item => item.ownerName === this.filterOwner);
      }

      // Sort by date (most recent first) for items that have dates
      return items.sort((a, b) => {
        if (!a.date && !b.date) return 0;
        if (!a.date) return 1;
        if (!b.date) return -1;
        return new Date(b.date) - new Date(a.date);
      });
    },
    uniqueOwners() {
      if (!this.profitLossData || !this.profitLossData.items) {
        return [];
      }
      const owners = this.profitLossData.items.map(item => item.ownerName);
      return [...new Set(owners)].sort();
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
    getTypeLabel(type) {
      const labels = {
        income: '配当金',
        benefit: '優待',
        sell: '売却',
        evaluation: '含み損益'
      };
      return labels[type] || type;
    },
    fmt(value) {
      const n = Number(value) || 0;
      return n.toLocaleString(undefined, {minimumFractionDigits: 2, maximumFractionDigits: 2});
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

.amount-cell {
  text-align: right;
  font-weight: bold;
}

.amount-cell.positive {
  color: #28a745;
}

.amount-cell.negative {
  color: #dc3545;
}
</style>
