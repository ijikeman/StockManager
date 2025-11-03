<template src="./templates/Edit.html"></template>

<script>
import axios from 'axios';

export default {
  name: 'BenefitEdit',
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
        benefit: null,
        stockLot: null
      },
      benefit_per_unit: null
    };
  },
  watch: {
    benefit_per_unit(newValue) {
      if (this.formData.stockLot && newValue) {
        this.formData.benefit = newValue;
      } else {
        this.formData.benefit = null;
      }
    }
  },
  methods: {
    async fetchBenefit() {
      try {
        const response = await axios.get(`/api/benefithistory/${this.id}`);
        this.formData = {
          paymentDate: response.data.paymentDate,
          benefit: response.data.benefit,
          stockLot: response.data.stockLot
        };
        if (this.formData.benefit) {
          this.benefit_per_unit = this.formData.benefit;
        }
      } catch (error) {
        console.error('Error fetching benefit:', error);
        alert('優待情報の取得に失敗しました。');
      }
    },
    async updateBenefit() {
      try {
        // データを更新用に整形
        const updateData = {
          paymentDate: this.formData.paymentDate,
          lotId: this.formData.stockLot.id,
          benefit: Number(this.benefit_per_unit)
        };

        await axios.put(`/api/benefithistory/${this.id}`, updateData);
        this.$router.push('/benefit');
      } catch (error) {
        console.error('Error updating benefit:', error);
        alert('優待情報の更新に失敗しました。');
      }
    },
    cancel() {
      this.$router.push('/benefit');
    }
  },
  created() {
    this.fetchBenefit();
  }
};
</script>
