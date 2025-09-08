<template src="./templates/HoldingList.html"></template>

<script>
import axios from 'axios';

export default {
  // コンポーネント名を'HoldingList'に設定
  name: 'HoldingList',
  // コンポーネントのデータ
  data() {
    return {
      // 保有銘柄のリスト
      holdings: [],
    };
  },
  // メソッド
  methods: {
    // APIから保有銘柄のリストを取得
    async fetchHoldings() {
      try {
        const response = await axios.get('/api/holding');
        this.holdings = response.data;
      } catch (error) {
        console.error('保有銘柄の取得中にエラーが発生しました:', error);
      }
    },
    goToAddHolding() {
      this.$router.push('/holding/add');
    },
    editHolding(id) {
      this.$router.push(`/holding/edit/${id}`);
    },
    async deleteHolding(id) {
      if (confirm('本当にこの保有銘柄を削除しますか？')) {
        try {
          await axios.delete(`/api/holding/${id}`);
          this.fetchHoldings(); // リストを再読み込み
        } catch (error) {
          console.error('保有銘柄の削除中にエラーが発生しました:', error);
          alert('削除に失敗しました。');
        }
      }
    },
  },

  mounted() {
    this.fetchHoldings();
  }
};
</script>
