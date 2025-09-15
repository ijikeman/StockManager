<template src="./templates/HoldingList.html"></template>

<script>
import axios from 'axios';

export default {
  // コンポーネント名を'HoldingList'に設定
  name: 'HoldingList',
  // コンポーネントのデータ
  data() {
    return {
      // 保有ロットのマップ (NISA: true/false)
      holdings: {},
      // オーナーのリスト
      owners: [],
      // 選択されたオーナーのID
      selectedOwnerId: null,
    };
  },
  //算出プロパティ
  computed: {
    // 表示用の保有リストを生成
    holdingList() {
      // holdingsが空か、キーがない場合は空の配列を返す
      if (!this.holdings || Object.keys(this.holdings).length === 0) {
        return [];
      }
      // NISAと通常の両方のリストを結合
      const nisaHoldings = this.holdings['true'] || [];
      const normalHoldings = this.holdings['false'] || [];
      return [...nisaHoldings, ...normalHoldings];
    }
  },
  // メソッド
  methods: {
    // APIからオーナーのリストを取得
    async fetchOwners() {
      try {
        const response = await axios.get('/api/owner');
        this.owners = response.data;
      } catch (error) {
        console.error('オーナーの取得中にエラーが発生しました:', error);
      }
    },
    // APIから保有ロットのリストを取得
    async fetchHoldings() {
      try {
        let url = '/api/holding';
        if (this.selectedOwnerId) {
          url += `/${this.selectedOwnerId}`;
        }
        const response = await axios.get(url);
        this.holdings = response.data;
      } catch (error) {
        console.error('保有ロットの取得中にエラーが発生しました:', error);
      }
    },
    // 売却ボタンがクリックされた時の処理
    sellHolding(id) {
      this.$router.push(`/transaction/sell/${id}`);
    },
  },

  watch: {
    // selectedOwnerIdが変更されたら、保有ロットを再取得
    selectedOwnerId() {
      this.fetchHoldings();
    }
  },

  mounted() {
    this.fetchOwners();
    this.fetchHoldings();
  }
};
</script>
