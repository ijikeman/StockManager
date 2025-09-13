<template src="./templates/AddEdit.html"></template>

<script>
import axios from 'axios';

export default {
  name: 'TransactionAddEdit',
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
        type: 'buy',
        stock_code: '',
        owner_id: null,
        quantity: null,
        price: null,
        fees: null,
        nisa: false
      },
      stocks: [],
      owners: []
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
    async fetchOwners() {
      try {
        const response = await axios.get('/api/owner');
        this.owners = response.data;
      } catch (error) {
        console.error('Error fetching owners:', error);
      }
    },
    async fetchTransaction() {
      if (!this.isEditing) return;
      try {
        const response = await axios.get(`/api/transaction/${this.id}`);
        this.formData = response.data;
        if (this.formData.stock) {
          this.formData.stock_code = this.formData.stock.code;
        }
      } catch (error) {
        console.error('Error fetching transaction:', error);
      }
    },
    async saveTransaction() {
      try {
        const transactionData = { ...this.formData };
        if (this.isEditing) {
          await axios.put(`/api/transaction/${this.id}`, transactionData);
        } else {
          await axios.post('/api/transaction', transactionData);
        }
        this.$router.push('/transaction');
      } catch (error) {
        console.error('Error saving transaction:', error);
        alert('Failed to save transaction.');
      }
    },
    cancel() {
      this.$router.push('/transaction');
    }
  },
  created() {
    this.fetchStocks();
    this.fetchOwners();
    this.fetchTransaction();
  },
  watch: {
    '$route.params.id'() {
      this.fetchTransaction();
    }
  }
};
</script>
