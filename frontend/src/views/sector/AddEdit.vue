<template src="./templates/SectorAddEdit.html"></template>

<script>
import axios from 'axios';

export default {
  name: 'SectorAddEdit',
  props: {
    id: {
      type: [String, Number],
      default: null
    }
  },
  data() {
    return {
      formData: {
        name: ''
      },
      sector: null
    };
  },
  computed: {
    isEditing() {
      return this.id != null;
    },
    pageTitle() {
      return this.isEditing ? 'セクター編集' : '新規セクター追加';
    }
  },
  methods: {
    async fetchSector() {
      if (!this.isEditing) {
        this.sector = {}; // 新規作成モード
        return;
      }
      try {
        const response = await axios.get(`/api/sector/${this.id}`);
        this.sector = response.data;
        this.formData.name = this.sector.name;
      } catch (error) {
        console.error('セクターの取得中にエラーが発生しました:', error);
        this.$router.push('/sectors'); // エラー時はリストにリダイレクト
      }
    },
    async saveSector() {
      try {
        const sectorData = {
          name: this.formData.name
        };

        if (this.isEditing) {
          await axios.put(`/api/sector/${this.id}`, { ...sectorData, id: parseInt(this.id) });
        } else {
          await axios.post('/api/sector', sectorData);
        }

        this.$router.push('/sectors');
      } catch (error) {
        console.error('セクターの保存中にエラーが発生しました:', error);
        alert('保存に失敗しました。もう一度お試しください。');
      }
    },
    cancel() {
      this.$router.push('/sectors');
    }
  },
  created() {
    this.fetchSector();
  },
  watch: {
    // ルートのIDが変更された場合（例：編集ページから別の編集ページへ移動）、データを再取得
    '$route.params.id'() {
        this.fetchSector();
    }
  }
};
</script>
