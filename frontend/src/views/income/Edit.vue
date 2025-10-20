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
        paymentDate: '',
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
        this.formData = {
          paymentDate: response.data.paymentDate,
          incoming: response.data.incoming,
          stockLot: response.data.stockLot
        };
        if (this.formData.incoming) {
          this.dividend_per_unit = this.formData.incoming;
        }
      } catch (error) {
        console.error('Error fetching income:', error);
        alert('配当情報の取得に失敗しました。');
      }
    },
    async updateIncome() {
      try {
        // データを更新用に整形
        const updateData = {
          paymentDate: this.formData.paymentDate,
          lotId: this.formData.stockLot.id,
          incoming: Number(this.dividend_per_unit)
        };

        await axios.put(`/api/incominghistory/${this.id}`, updateData);
        this.$router.push('/income');
      } catch (error) {
        console.error('Error updating income:', error);
        alert('配当情報の更新に失敗しました。');
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
