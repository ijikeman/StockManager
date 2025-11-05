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
  computed: {
    // Calculate displayed income with tax deduction for non-NISA accounts
    displayIncomes() {
      return this.incomes.map(income => ({
        ...income,
        displayAmount: this.calculateDisplayAmount(income)
      }));
    }
  },
  methods: {
    calculateDisplayAmount(income) {
      // If NISA flag is false, apply tax (20.315% in Japan)
      if (income.isNisa === false) {
        const taxRate = 0.20315;
        const afterTax = income.incoming * (1 - taxRate);
        return Math.floor(afterTax); // Round down to integer
      }
      return income.incoming;
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
