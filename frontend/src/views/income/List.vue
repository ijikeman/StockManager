<template src="./templates/List.html"></template>

<script>
import axios from 'axios';
import { formatDecimal } from '@/utils/formatters';

export default {
  name: 'IncomeList',
  data() {
    return {
      incomes: [],
    };
  },
  methods: {
    // Format dividend amounts with 2 decimal places
    fmt(value) {
      return formatDecimal(value);
    },
    async fetchIncomes() {
      try {
        const response = await axios.get('/api/incominghistory');
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
          await axios.delete(`/api/incominghistory/${id}`);
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
