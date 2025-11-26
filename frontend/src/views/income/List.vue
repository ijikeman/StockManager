<template src="./templates/List.html"></template>

<script>
import axios from 'axios';
import { formatDecimal } from '@/utils/formatters';

export default {
  name: 'IncomeList',
  data() {
    return {
      incomes: [],
      sortKey: 'paymentDate',
      sortOrders: { 'paymentDate': -1 } // Default to descending order
    };
  },
  computed: {
    sortedIncomes() {
      return [...this.incomes].sort((a, b) => {
        const aValue = a[this.sortKey];
        const bValue = b[this.sortKey];
        if (aValue === bValue) return 0;
        const order = this.sortOrders[this.sortKey] || 1;
        return (aValue > bValue ? 1 : -1) * order;
      });
    }
  },
  methods: {
    // Format dividend amounts with 2 decimal places
    fmt(value) {
      return formatDecimal(value);
    },
    sortBy(key) {
      if (this.sortKey === key) {
        this.sortOrders[key] = this.sortOrders[key] * -1;
      } else {
        this.sortKey = key;
        this.sortOrders = { [key]: -1 };
      }
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
