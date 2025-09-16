<template src="./templates/Edit.html"></template>

<script>
import axios from 'axios';

export default {
  name: 'IncomeEdit',
  props: {
    id: {
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      formData: {
        payment_date: '',
        lot_id: null,
        incoming: null
      }
    };
  },
  methods: {
    async fetchIncome() {
      try {
        const response = await axios.get(`/api/incoming-history/${this.id}`);
        this.formData = response.data;
      } catch (error) {
        console.error('Error fetching income:', error);
      }
    },
    async saveIncome() {
      try {
        await axios.put(`/api/incoming-history/${this.id}`, this.formData);
        this.$router.push('/income');
      } catch (error) {
        console.error('Error saving income:', error);
        alert('Failed to save income.');
      }
    },
    cancel() {
      this.$router.push('/income');
    }
  },
  created() {
    this.fetchIncome();
  }
};
</script>
