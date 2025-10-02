<template src="./templates/StockLotList.html"></template>

<script>
import axios from 'axios';
import StockLotAddEdit from './AddEdit.vue';

export default {
  name: 'StockLotList',
  components: {
    StockLotAddEdit
  },
  data() {
    return {
      stockLots: [],
      showAddEditForm: false,
      editingStockLot: null
    };
  },
  methods: {
    async fetchStockLots() {
      try {
        const response = await axios.get('/api/stock-lot');
        this.stockLots = response.data;
      } catch (error) {
        console.error('Error fetching stock lots:', error);
      }
    },
    showAddForm() {
      this.editingStockLot = {
        unit: 0,
        isNisa: false
      };
      this.showAddEditForm = true;
    },
    editStockLot(stockLot) {
      this.editingStockLot = { ...stockLot };
      this.showAddEditForm = true;
    },
    hideForm() {
      this.showAddEditForm = false;
      this.editingStockLot = null;
    },
    onStockLotSaved() {
      this.fetchStockLots();
      this.hideForm();
    },
    async deleteStockLot(id) {
      if (!confirm('Are you sure you want to delete this stock lot?')) {
        return;
      }

      try {
        await axios.delete(`/api/stock-lot/${id}`);
        this.fetchStockLots();
      } catch (error) {
        console.error('Error deleting stock lot:', error);
      }
    }
  },
  mounted() {
    this.fetchStockLots();
  }
};
</script>