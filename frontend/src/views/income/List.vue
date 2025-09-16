<template src="./templates/List.html"></template>

<script>
import axios from 'axios';

export default {
  name: 'IncomeList',
  data() {
    return {
      incomes: [],
    };
  },
  methods: {
    async fetchIncomes() {
      try {
        const response = await axios.get('/api/incoming-history');
        this.incomes = response.data;
      } catch (error) {
        console.error('Error fetching incomes:', error);
      }
    },
    goToAddIncome() {
      this.$router.push('/income/add');
    },
    editIncome(id) {
      this.$router.push(`/income/edit/${id}`);
    },
    async deleteIncome(id) {
      if (confirm('Are you sure you want to delete this income?')) {
        try {
          await axios.delete(`/api/incoming-history/${id}`);
          this.fetchIncomes();
        } catch (error) {
          console.error('Error deleting income:', error);
          alert('Failed to delete income.');
        }
      }
    },
  },
  mounted() {
    this.fetchIncomes();
  }
};
</script>
