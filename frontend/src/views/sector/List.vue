<template src="./templates/SectorList.html"></template>

<script>
import axios from 'axios';

export default {
  // コンポーネント名を'SectorList'に設定
  name: 'SectorList',
  // コンポーネントのデータ
  data() {
    return {
      // セクターのリスト
      sectors: [],
    };
  },
  // メソッド
  methods: {
    // APIからセクターのリストを取得
    async fetchSectors() {
      try {
        const response = await axios.get('/api/sector');
        this.sectors = response.data;
      } catch (error) {
        console.error('セクターの取得中にエラーが発生しました:', error);
      }
    },
    goToAddPage() {
      this.$router.push('/sector/add');
    },
    goToEditPage(id) {
      this.$router.push(`/sector/edit/${id}`);
    },
    async deleteSector(id) {
      if (confirm('このセクターを削除しますか？')) {
        try {
          // バックエンドAPIのパスを修正
          await axios.delete(`/api/sector/${id}`);
          this.fetchSectors(); // リストを再読み込み
        } catch (error) {
          console.error('セクターの削除中にエラーが発生しました:', error);
          alert('削除に失敗しました。');
        }
      }
    }
  },

  mounted() {
    this.fetchSectors();
  }
};
</script>
