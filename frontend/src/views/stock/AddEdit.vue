<template src="./templates/StockAddEdit.html"></template>

<script>
import axios from 'axios';

export default {
  name: 'StockAddEdit',
  props: {
    id: {
      type: [String, Number],
      default: null
    }
  },
  data() {
    return {
      formData: {
        code: '',
        name: '',
        sectorId: null
      },
      sectors: [],
      stock: null
    };
  },
  computed: {
    isEditing() {
      return this.id != null;
    },
    pageTitle() {
      return this.isEditing ? '在庫編集' : '新規在庫追加';
    }
  },
  methods: {
    async fetchSectors() {
      try {
        const response = await axios.get('/api/sector');
        this.sectors = response.data;
      } catch (error) {
        console.error('セクターの取得中にエラーが発生しました:', error);
      }
    },
    async fetchStock() {
      if (!this.isEditing) {
        this.stock = {}; // 新規作成モード
        return;
      }
      try {
        const response = await axios.get(`/api/stock/${this.id}`);
        this.stock = response.data;
        this.formData = {
          code: this.stock.code,
          name: this.stock.name,
          sectorId: this.stock.sector ? this.stock.sector.id : null
        };
      } catch (error) {
        console.error('在庫の取得中にエラーが発生しました:', error);
        this.$router.push('/stock'); // エラー時はリストにリダイレクト
      }
    },
    async saveStock() {
      try {
        const stockData = {
          code: this.formData.code,
          name: this.formData.name,
          sector: {
            id: this.formData.sectorId,
            // バックエンドでsectorIdからsectorオブジェクトを構築することを期待
          }
        };

        if (this.isEditing) {
          await axios.put(`/api/stock/${this.id}`, { ...stockData, id: parseInt(this.id) });
        } else {
          await axios.post('/api/stock', stockData);
        }

        this.$router.push('/stock');
      } catch (error) {
        console.error('在庫の保存中にエラーが発生しました:', error);
        alert('保存に失敗しました。もう一度お試しください。');
      }
    },
    cancel() {
      this.$router.push('/stock');
    }
  },
  created() {
    this.fetchSectors();
    this.fetchStock();
  },
  watch: {
    '$route.params.id'() {
        this.fetchStock();
    }
  }
};
</script>
