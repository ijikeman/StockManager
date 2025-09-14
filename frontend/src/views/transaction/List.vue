<template src="./templates/List.html"></template>

<script>
import axios from 'axios';

export default {
  name: 'TransactionList',
  data() {
    return {
      transactions: [],
    };
  },
  methods: {
    async fetchTransactions() {
      try {
        const response = await axios.get('/api/transaction');
        this.transactions = response.data;
      } catch (error) {
        console.error('Error fetching transactions:', error);
      }
    },
    goToAddTransaction() {
      this.$router.push('/transaction/add');
    },
    goToSellPage(lotId) {
      this.$router.push(`/transaction/sell/${lotId}`);
    },
  },
  mounted() {
    this.fetchTransactions();
  }
};
</script>
