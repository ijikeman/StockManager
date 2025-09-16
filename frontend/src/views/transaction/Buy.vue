<template src="./templates/Buy.html"></template>

<script>
import axios from 'axios';

export default {
  name: 'BuyTransaction',
  data() {
    return {
      formData: {
        date: new Date().toISOString().slice(0, 10),
        type: 'buy',
        stock_code: '',
        owner_id: null,
        unit: null,
        price: null,
        fees: null,
        nisa: false
      },
      stocks: [],
      owners: []
    };
  },
  methods: {
    async fetchStocks() {
      try {
        const response = await axios.get('/api/stock');
        this.stocks = response.data;
      } catch (error) {
        console.error('Error fetching stocks:', error);
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
    async saveTransaction() {
      try {
        await axios.post('/api/transaction', this.formData);
        this.$router.push('/transaction');
      } catch (error) {
        console.error('Error saving transaction:', error);
        alert('Failed to save transaction.');
      }
    },
    cancel() {
      this.$router.push('/transaction');
    }
  },
  created() {
    this.fetchStocks();
    this.fetchOwners();
  }
};
</script>
