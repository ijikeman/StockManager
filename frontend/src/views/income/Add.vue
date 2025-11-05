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
        incoming: null,
        isNisa: false
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
          // API returns currentUnit and stock.minimalUnit; accept multiple possible shapes for backward compat
          const unit = Number(selectedLot.currentUnit ?? selectedLot.unit ?? selectedLot.stock?.unit ?? 0);
          const minUnit = Number(selectedLot.stock?.minimalUnit ?? selectedLot.minimalUnit ?? selectedLot.stock?.minimal_unit ?? 1) || 1;
          if (unit > 0) {
            this.formData.incoming = this.dividend_per_unit * unit * minUnit;
          } else {
            this.formData.incoming = null;
          }
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
        // The backend expects `incoming`, but you asked to send the entered per-unit value as-is.
        // Override `formData.incoming` with the raw dividend_per_unit value if provided.
        if (this.dividend_per_unit != null) {
          this.formData.incoming = Number(this.dividend_per_unit);
        }

        if (this.formData.incoming == null) {
          alert('配当金（合計）が計算されていません。ロットと単元あたり配当、または金額を入力してください。');
          return;
        }

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
