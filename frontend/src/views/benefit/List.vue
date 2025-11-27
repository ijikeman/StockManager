<template src="./templates/List.html"></template>

<script>
import axios from 'axios';
import { formatNumber } from '@/utils/formatters';

export default {
  name: 'BenefitList',
  data() {
    return {
      benefits: [],
      sortKey: 'paymentDate',
      sortOrders: { 'paymentDate': -1 } // Default to descending order
    };
  },
  computed: {
    sortedBenefits() {
      return [...this.benefits].sort((a, b) => {
        const aValue = a[this.sortKey];
        const bValue = b[this.sortKey];
        if (aValue === bValue) return 0;
        const order = this.sortOrders[this.sortKey] || 1;
        return (aValue > bValue ? 1 : -1) * order;
      });
    }
  },
  methods: {
    fmt(value) {
      return formatNumber(value);
    },
    sortBy(key) {
      if (this.sortKey === key) {
        this.sortOrders[key] = this.sortOrders[key] * -1;
      } else {
        this.sortKey = key;
        this.sortOrders = { [key]: -1 };
      }
    },
    async fetchBenefits() {
      try {
        const response = await axios.get('/api/benefithistory');
        this.benefits = response.data;
      } catch (error) {
        console.error('Error fetching benefits:', error);
      }
    },
    goToAddBenefit() {
      this.$router.push('/benefit/add');
    },
    editBenefit(id) {
      this.$router.push(`/benefit/edit/${id}`);
    },
    async deleteBenefit(id) {
      if (confirm('Are you sure you want to delete this benefit?')) {
        try {
          await axios.delete(`/api/benefithistory/${id}`);
          this.fetchBenefits();
        } catch (error) {
          console.error('Error deleting benefit:', error);
          alert('Failed to delete benefit.');
        }
      }
    },
  },
  mounted() {
    this.fetchBenefits();
  }
};
</script>
