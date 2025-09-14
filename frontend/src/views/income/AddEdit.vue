<template src="./templates/AddEdit.html"></template>

<script>
import axios from 'axios';

export default {
  name: 'IncomeAddEdit',
  props: {
    id: {
      type: [String, Number],
      default: null
    },
    lotId: {
      type: [String, Number],
      default: null
    }
  },
  data() {
    return {
      formData: {
        payment_date: new Date().toISOString().slice(0, 10),
        stock_code: '',
        incoming: null,
        lot_id: null
      },
      stocks: [],
      holding: null
    };
  },
  computed: {
    isEditing() {
      return this.id != null;
    },
    isForLot() {
      return this.lotId != null;
    }
  },
  methods: {
    async fetchHolding() {
      if (!this.isForLot) return;
      try {
        const response = await axios.get(`/api/stock-lot/${this.lotId}`);
        this.holding = response.data;
        this.formData.stock_code = this.holding.stock.code;
        this.formData.lot_id = this.lotId;
      } catch (error) {
        console.error('Error fetching holding:', error);
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
    async fetchIncome() {
      if (!this.isEditing) return;
      try {
        const response = await axios.get(`/api/incoming-history/${this.id}`);
        this.formData = response.data;
        if (this.formData.stock) {
          this.formData.stock_code = this.formData.stock.code;
        }
      } catch (error) {
        console.error('Error fetching income:', error);
      }
    },
    async saveIncome() {
      try {
        const incomeData = { ...this.formData };
        if (this.isEditing) {
          await axios.put(`/api/incoming-history/${this.id}`, incomeData);
        } else {
          await axios.post('/api/incoming-history', incomeData);
        }
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
    if (this.isForLot) {
      this.fetchHolding();
    } else {
      this.fetchStocks();
    }
    this.fetchIncome();
  },
  watch: {
    '$route.params.id'() {
      this.fetchIncome();
    },
    '$route.params.lotId'() {
      this.fetchHolding();
    }
  }
};
</script>
