<template src="./templates/Add.html"></template>

<script>
import axios from 'axios';

export default {
  name: 'IncomeAdd',
  data() {
    return {
      formData: {
        paymentDate: new Date().toISOString().slice(0, 10),
        lotId: null,
        incoming: null
      },
      dividend_per_unit: null,
      stockLots: []
    };
  },
  watch: {
    dividend_per_unit: 'calculateTotal',
    'formData.lotId': 'calculateTotal'
  },
  methods: {
    calculateTotal() {
      if (this.formData.lotId && this.dividend_per_unit) {
        const selectedLot = this.stockLots.find(lot => lot.id === this.formData.lotId);
        if (selectedLot) {
          this.formData.incoming = this.dividend_per_unit * selectedLot.unit * selectedLot.minimalUnit;
        }
      } else {
        this.formData.incoming = null;
      }
    },
    async fetchStockLots() {
      try {
        const response = await axios.get('/api/stocklot');
        this.stockLots = response.data;
      } catch (error) {
        console.error('Error fetching stock lots:', error);
      }
    },
    async saveIncome() {
      try {
        await axios.post('/api/incominghistory', this.formData);
        this.$router.push('/income');
      } catch (error) {
        console.error('Error saving income:', error);
        alert('Failed to save income.');
      }
    },
    cancel() {
      this.$router.push('/income');
    }
  },
  created() {
    this.fetchStockLots();
  }
};
</script>
