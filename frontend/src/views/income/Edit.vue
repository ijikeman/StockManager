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
        incoming: null,
        stockLot: null
      },
      dividend_per_unit: null
    };
  },
  watch: {
    dividend_per_unit(newValue) {
      if (this.formData.stockLot && newValue) {
        this.formData.incoming = newValue;
      } else {
        this.formData.incoming = null;
      }
    }
  },
  methods: {
    async fetchIncome() {
      try {
        const response = await axios.get(`/api/incominghistory/${this.id}`);
        this.formData = response.data;
        if (this.formData.incoming && this.formData.stockLot) {
          this.dividend_per_unit = this.formData.incoming;
        }
      } catch (error) {
        console.error('Error fetching income:', error);
      }
    },
    async saveIncome() {
      try {
        // Override `incoming` with raw per-unit value if provided
        if (this.dividend_per_unit != null) {
          this.formData.incoming = Number(this.dividend_per_unit);
        }

        await axios.put(`/api/incominghistory/${this.id}`, this.formData);
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
