<template src="./templates/List.html"></template>

<script>
import axios from 'axios';

export default {
  name: 'StockLotList',
  data() {
    return {
      stockLots: [],
      selectedOwner: '',
    };
  },
  computed: {
    // ユニット数が1以上のストックロットのみをフィルタリング
    filteredStockLots() {
      return this.stockLots.filter(lot => lot.unit > 0);
    },
    // オーナーの重複しないリストを作成
    owners() {
      const ownerNames = this.filteredStockLots.map(lot => lot.owner_name);
      return [...new Set(ownerNames)];
    },
    // 選択されたオーナーに基づいてストックロットをフィルタリング
    displayStockLots() {
      if (!this.selectedOwner) {
        return this.filteredStockLots;
      }
      return this.filteredStockLots.filter(lot => lot.owner_name === this.selectedOwner);
    }
  },
  methods: {
    async fetchStockLots() {
      try {
        const response = await axios.get('/api/stocklot');
        this.stockLots = response.data;
      } catch (error) {
        console.error('Error fetching stock lots:', error);
      }
    },
    goToAddStock() {
      this.$router.push('/stocklot/add');
    },
    // フィルタをクリア
    clearFilter() {
      this.selectedOwner = '';
    }
  },
  mounted() {
    this.fetchStockLots();
  }
};
</script>