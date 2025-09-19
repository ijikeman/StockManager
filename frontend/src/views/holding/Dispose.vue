<template src="./templates/Dispose.html"></template>

<script>
import axios from 'axios';

export default {
  name: 'DisposeLot',
  props: {
    lot_id: {
      type: [String, Number],
      required: true,
    },
  },
  data() {
    return {
      lot: null,
      unitsToDispose: null,
    };
  },
  methods: {
    async fetchLotDetails() {
      try {
        const response = await axios.get(`/api/holding/${this.lot_id}`);
        this.lot = response.data;
        this.unitsToDispose = this.lot.unit; // Default to full amount
      } catch (error) {
        console.error('Error fetching lot details:', error);
        alert('ロット情報の取得に失敗しました。');
        this.$router.push('/holding');
      }
    },
    async confirmDisposal() {
      if (!this.unitsToDispose || this.unitsToDispose <= 0) {
        alert('処分する単元数を入力してください。');
        return;
      }
      if (this.unitsToDispose > this.lot.unit) {
        alert('処分単元数が保有単元数を超えています。');
        return;
      }

      try {
        if (this.unitsToDispose === this.lot.unit) {
          // Full disposal
          await axios.delete(`/api/holding/${this.lot_id}`);
          alert('ロットを完全に削除しました。');
        } else {
          // Partial disposal
          await axios.post(`/api/holding/${this.lot_id}/dispose`, { unit: this.unitsToDispose });
          alert('ロットの一部を処分しました。');
        }
        this.$router.push('/holding');
      } catch (error) {
        console.error('Error disposing lot:', error);
        alert('ロットの処分に失敗しました。');
      }
    },
    cancel() {
      this.$router.push('/holding');
    },
  },
  created() {
    this.fetchLotDetails();
  },
};
</script>
