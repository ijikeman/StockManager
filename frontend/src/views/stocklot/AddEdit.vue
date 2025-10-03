<template src="./templates/StockLotAddEdit.html"></template>

<script>
import axios from 'axios';

export default {
  name: 'StockLotAddEdit',
  props: {
    stockLot: {
      type: Object,
      required: true
    }
  },
  emits: ['saved', 'cancelled'],
  data() {
    return {
      formData: {
        ownerId: null,
        stockId: null,
        unit: 0,
        isNisa: false
      },
      owners: [],
      stocks: []
    };
  },
  computed: {
    isEditing() {
      return this.stockLot && this.stockLot.id != null;
    }
  },
  methods: {
    async fetchOwners() {
      try {
        const response = await axios.get('/api/owner');
        this.owners = response.data;
      } catch (error) {
        console.error('Error fetching owners:', error);
      }
    },
    async fetchStocks() {
      try {
        const response = await axios.get('/api/stock');
        this.stocks = response.data;
      } catch (error) {
        console.error('Error fetching stocks:', error);
      }
    },
    async saveStockLot() {
      try {
        if (this.isEditing) {
          const updateData = {
            unit: this.formData.unit,
            is_nisa: this.formData.isNisa
          };
          await axios.put(`/api/stock-lot/${this.stockLot.id}`, updateData);
        } else {
          const createData = {
            ownerId: this.formData.ownerId,
            stockId: this.formData.stockId,
            unit: this.formData.unit,
            isNisa: this.formData.isNisa
          };
          await axios.post('/api/stock-lot', createData);
        }
        this.$emit('saved');
      } catch (error) {
        console.error('Error saving stock lot:', error);
        alert('Failed to save. Please try again.');
      }
    },
    cancel() {
      this.$emit('cancelled');
    }
  },
  watch: {
    stockLot: {
      handler(newStockLot) {
        if (newStockLot) {
          this.formData = {
            ownerId: newStockLot.owner_id || null,
            stockId: newStockLot.stock_id || null,
            unit: newStockLot.unit || 0,
            isNisa: newStockLot.is_nisa || false
          };
        } else {
          this.formData = {
            ownerId: null,
            stockId: null,
            unit: 0,
            isNisa: false
          };
        }
      },
      immediate: true,
      deep: true,
    }
  },
  created() {
    this.fetchOwners();
    this.fetchStocks();
  }
};
</script>