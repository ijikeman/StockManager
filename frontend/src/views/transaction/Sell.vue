<template src="./templates/Sell.html"></template>

<script>
import axios from 'axios';

export default {
  name: 'SellTransaction',
  props: {
    lot_id: {
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      lot: null,
      error: null,
      formData: {
        date: new Date().toISOString().slice(0, 10),
        type: 'sell',
        lot_id: this.lot_id,
        unit: null,
        price: null,
        fees: null,
      }
    };
  },
  methods: {
    async fetchLotDetails() {
      try {
        const response = await axios.get(`/api/holding/${this.lot_id}`);
        if (response.data) {
          this.lot = response.data;
          this.formData.unit = this.lot.unit;
        } else {
          this.error = '指定されたロットが見つかりません。';
        }
      } catch (error) {
        console.error('Error fetching lot details:', error);
        this.error = 'ロット情報の取得に失敗しました。';
      }
    },
    async saveTransaction() {
      if (this.formData.unit > this.lot.unit) {
        alert('売却単元数が保有単元数を超えています。');
        return;
      }

      try {
        const payload = {
          ...this.formData,
          stock_code: this.lot.stock.code,
          owner_id: this.lot.owner.id,
        }
        await axios.post('/api/transaction', payload);
        this.$router.push('/holding');
      } catch (error) {
        console.error('Error saving transaction:', error);
        alert('取引の保存に失敗しました。');
      }
    },
    cancel() {
      this.$router.push('/holding');
    }
  },
  created() {
    this.fetchLotDetails();
  }
};
</script>
