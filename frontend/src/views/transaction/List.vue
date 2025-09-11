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
    editTransaction(id) {
      this.$router.push(`/transaction/edit/${id}`);
    },
    async deleteTransaction(id) {
      if (confirm('Are you sure you want to delete this transaction?')) {
        try {
          await axios.delete(`/api/transaction/${id}`);
          this.fetchTransactions();
        } catch (error) {
          console.error('Error deleting transaction:', error);
          alert('Failed to delete transaction.');
        }
      }
    },
  },
  mounted() {
    this.fetchTransactions();
  }
};
</script>
