<template src="./templates/OwnerList.html"></template>

<script>
import axios from 'axios';
import OwnerAddEdit from './AddEdit.vue';

export default {
  // コンポーネント名を'OwnerList'に設定
  name: 'OwnerList',
  // 使用するコンポーネントを登録
  components: {
    OwnerAddEdit
  },
  // コンポーネントのデータ
  data() {
    return {
      // オーナーのリスト
      owners: [],
      // 追加・編集フォームの表示状態
      showAddEditForm: false,
      // 編集中のオーナー情報
      editingOwner: null
    };
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
    
    // 新規追加フォームを表示
    showAddForm() {
      this.editingOwner = {
        name: ''
      };
      this.showAddEditForm = true;
    },
    
    // 編集フォームを表示
    editOwner(owner) {
      this.editingOwner = { 
        id: owner.id,
        name: owner.name 
      };
      this.showAddEditForm = true;
    },
    
    // フォームを非表示
    hideForm() {
      this.showAddEditForm = false;
      this.editingOwner = null;
    },
    
    // 保存完了時の処理
    onOwnerSaved() {
      this.fetchOwners();
      this.hideForm();
    },
    
    // オーナーを削除
    async deleteOwner(id) {
      if (!confirm('このオーナーを削除しますか？')) {
        return;
      }
      
      try {
        await axios.delete(`/api/owner/${id}`);
        this.fetchOwners();
      } catch (error) {
        console.error('オーナーの削除中にエラーが発生しました:', error);
      }
    }
  },
  
  mounted() {
    this.fetchOwners();
  }
};
</script>

<!-- スタイルは共通CSSを使用するため削除 -->
