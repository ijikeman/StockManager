<template src="./templates/List.html"></template>

<script>
import axios from 'axios';

export default {
  name: 'StockLotList',
  data() {
    return {
      stockLots: [],
      owners: [],
      selectedOwner: '',
    };
  },
  methods: {
    async fetchStockLots() {
      try {
        let url = '/api/stocklot';
        if (this.selectedOwner) {
          url += `?ownerId=${this.selectedOwner}`;
        }
        const response = await axios.get(url);
        this.stockLots = response.data;
      } catch (error) {
        console.error('Error fetching stock lots:', error);
      }
    },
    async fetchOwners() {
      try {
        const response = await axios.get('/api/owner');
        this.owners = response.data;
      } catch (error) {
        console.error('Error fetching owners:', error);
      }
    },
    // Compute evaluation (profit/loss) for a lot.
    // Uses (currentPrice - averagePrice) * currentUnit and returns a number (can be negative).
    evaluation(lot) {
      if (!lot) return 0;
      const unit = Number(lot.currentUnit) || 0;
      // averagePrice may be named averagePrice or price depending on backend; prefer averagePrice then price
      const avg = Number(lot.averagePrice ?? lot.price) || 0;
      const current = Number(lot.stock && lot.stock.currentPrice) || 0;
      // minimum unit may be named minimalUnit, minimal_unit, minimumUnit, or minimum_unit
      const minUnit = Number(lot.stock && (lot.stock.minimalUnit ?? lot.stock.minimal_unit ?? lot.stock.minimumUnit ?? lot.stock.minimum_unit)) || 1;
      return unit * minUnit * (current - avg);
    },
    // Format a number as localized currency/number with 2 decimals.
    fmt(value) {
      const n = Number(value) || 0;
      return n.toLocaleString(undefined, {minimumFractionDigits: 2, maximumFractionDigits: 2});
    },
    goToAddStock() {
      this.$router.push('/stocklot/add');
    },
  },
  watch: {
    selectedOwner() {
      this.fetchStockLots();
    }
  },
  mounted() {
    this.fetchStockLots();
    this.fetchOwners();
  }
};
</script>