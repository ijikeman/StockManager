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
        unit: 0,
        isNisa: false
      }
    };
  },
  computed: {
    isEditing() {
      return this.stockLot && this.stockLot.id != null;
    }
  },
  methods: {
    async saveStockLot() {
      try {
        if (this.isEditing) {
          const updateData = {
            unit: this.formData.unit,
            isNisa: this.formData.isNisa
          };
          await axios.put(`/api/stock-lot/${this.stockLot.id}`, updateData);
        }
        // Create functionality is not implemented in this step
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
            unit: newStockLot.unit || 0,
            isNisa: newStockLot.is_nisa || false,
          };
        } else {
          this.formData = {
            unit: 0,
            isNisa: false,
          };
        }
      },
      immediate: true
    }
  }
};
</script>