<template src="./templates/Add.html"></template>

<script>
import axios from 'axios';

export default {
  name: 'BenefitAdd',
  data() {
    return {
      formData: {
        paymentDate: new Date().toISOString().slice(0, 10),
        lotId: null,
        benefit: null
      },
      benefit_per_unit: null,
      stockLots: []
    };
  },
  watch: {
    benefit_per_unit: 'calculateTotal',
    'formData.lotId': 'calculateTotal'
  },
  methods: {
    calculateTotal() {
      if (this.formData.lotId && this.benefit_per_unit) {
        const selectedLot = this.stockLots.find(lot => lot.id === this.formData.lotId);
        if (selectedLot) {
          // API returns currentUnit and stock.minimalUnit; accept multiple possible shapes for backward compat
          const unit = Number(selectedLot.currentUnit ?? selectedLot.unit ?? selectedLot.stock?.unit ?? 0);
          const minUnit = Number(selectedLot.stock?.minimalUnit ?? selectedLot.minimalUnit ?? selectedLot.stock?.minimal_unit ?? 1) || 1;
          if (unit > 0) {
            this.formData.benefit = this.benefit_per_unit * unit * minUnit;
          } else {
            this.formData.benefit = null;
          }
        }
      } else {
        this.formData.benefit = null;
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
    async saveBenefit() {
      try {
        // The backend expects `benefit`, but you asked to send the entered per-unit value as-is.
        // Override `formData.benefit` with the raw benefit_per_unit value if provided.
        if (this.benefit_per_unit != null) {
          this.formData.benefit = Number(this.benefit_per_unit);
        }

        if (this.formData.benefit == null) {
          alert('優待金（合計）が計算されていません。ロットと単元あたり優待、または金額を入力してください。');
          return;
        }

        await axios.post('/api/benefithistory', this.formData);
        this.$router.push('/benefit');
      } catch (error) {
        console.error('Error saving benefit:', error);
        alert('Failed to save benefit.');
      }
    },
    cancel() {
      this.$router.push('/benefit');
    }
  },
  created() {
    this.fetchStockLots();
  }
};
</script>
