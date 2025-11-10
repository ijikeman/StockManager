<template src="./templates/List.html"></template>

<script>
import axios from 'axios';
import { formatDecimal, formatNumber } from '@/utils/formatters';

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
    // Format stock prices and dividend amounts with 2 decimal places
    fmtPrice(value) {
      return formatDecimal(value);
    },
    // Format incoming (dividend) amounts with 2 decimal places
    fmtIncoming(value) {
      return formatDecimal(value);
    },
    // Format evaluation (profit/loss) as integer
    fmt(value) {
      return formatNumber(value);
    },
    goToAddStock() {
      this.$router.push('/stocklot/add');
    },
    goToSellPage(id) {
      this.$router.push(`/stocklot/sell/${id}`);
    },
    /**
     * 購入株価配当利回りを計算
     * @param {Object} lot - ストックロットオブジェクト
     * @returns {string} - フォーマットされた配当利回り
     */
    purchasePriceDividendYield(lot) {
      const purchasePrice = lot.averagePrice ?? lot.price;
      if (!purchasePrice || !lot.incoming || purchasePrice <= 0) {
        return '0.00';
      }
      const yieldRate = (lot.incoming / purchasePrice) * 100;
      return yieldRate.toFixed(2);
    },
    /**
     * 現在株価配当利回りを計算
     * @param {Object} lot - ストックロットオブジェクト
     * @returns {string} - フォーマットされた配当利回り
     */
    currentPriceDividendYield(lot) {
      if (!lot.stock?.currentPrice || !lot.incoming || lot.stock.currentPrice <= 0) {
        return '0.00';
      }
      const yieldRate = (lot.incoming / lot.stock.currentPrice) * 100;
      return yieldRate.toFixed(2);
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