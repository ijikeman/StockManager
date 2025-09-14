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
        quantity: null,
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
      if (this.formData.quantity % 100 !== 0) {
        alert('数量は100の倍数である必要があります。');
        return;
      }
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
