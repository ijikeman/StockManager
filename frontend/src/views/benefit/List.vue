<template src="./templates/List.html"></template>

<script>
import axios from 'axios';
import { formatNumber } from '@/utils/formatters';

export default {
  name: 'BenefitList',
  data() {
    return {
      benefits: [],
    };
  },
  methods: {
    fmt(value) {
      return formatNumber(value);
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
