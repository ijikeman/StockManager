<template src="./templates/List.html"></template>

<script>
import axios from 'axios';

export default {
  name: 'StockLotList',
  data() {
    return {
      stockLots: [],
    };
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
  },
  mounted() {
    this.fetchStockLots();
  }
};
</script>