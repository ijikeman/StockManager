<template src="./templates/AddEdit.html"></template>

<script>
import axios from 'axios';

export default {
  name: 'IncomeAddEdit',
  props: {
    id: {
      type: [String, Number],
      default: null
    }
  },
  data() {
    return {
      formData: {
        date: new Date().toISOString().slice(0, 10),
        stock_code: '',
        amount: null
      },
      stocks: []
    };
  },
  computed: {
    isEditing() {
      return this.id != null;
    }
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
    async fetchIncome() {
      if (!this.isEditing) return;
      try {
        const response = await axios.get(`/api/income/${this.id}`);
        this.formData = response.data;
        if (this.formData.stock) {
          this.formData.stock_code = this.formData.stock.code;
        }
      } catch (error) {
        console.error('Error fetching income:', error);
      }
    },
    async saveIncome() {
      try {
        const incomeData = { ...this.formData };
        if (this.isEditing) {
          await axios.put(`/api/income/${this.id}`, incomeData);
        } else {
          await axios.post('/api/income', incomeData);
        }
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
    this.fetchStocks();
    this.fetchIncome();
  },
  watch: {
    '$route.params.id'() {
      this.fetchIncome();
    }
  }
};
</script>

<style src="./AddEdit.css" scoped></style>
