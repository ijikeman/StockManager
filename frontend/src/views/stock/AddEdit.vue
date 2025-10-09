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
        sectorId: null,
        currentPrice: null,
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
      return this.isEditing ? '銘柄編集' : '新規銘柄追加';
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
          sectorId: this.stock.sector ? this.stock.sector.id : null,
          currentPrice: this.stock.currentPrice
        };
      } catch (error) {
        console.error('銘柄の取得中にエラーが発生しました:', error);
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
          ,
          currentPrice: this.formData.currentPrice
        };

        if (this.isEditing) {
          await axios.put(`/api/stock/${this.id}`, { ...stockData, id: parseInt(this.id) });
        } else {
          await axios.post('/api/stock', stockData);
        }

        this.$router.push('/stock');
      } catch (error) {
        console.error('銘柄の保存中にエラーが発生しました:', error);
        alert('保存に失敗しました。もう一度お試しください。');
      }
    },
    cancel() {
      this.$router.push('/stock');
    },
    async fetchStockNameFromApi() {
      if (!this.formData.code) return;
      try {
        const response = await axios.get(`/api/stock/name/${this.formData.code}`);
        if (response.data) {
          this.formData.name = response.data;
        }
      } catch (error) {
        console.error('銘柄名の取得中にエラーが発生しました:', error);
      }
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
