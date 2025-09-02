<template src="./templates/OwnerList.html"></template>

<script>
import axios from 'axios';
import OwnerAddEdit from './AddEdit.vue';

export default {
  components: {
    OwnerAddEdit
  },
  data() {
    return {
      owners: [],
      showAddEditForm: false,
      editingOwner: null
    };
  },
  methods: {
    // APIからオーナーのリストを取得
    async fetchOwners() {
      try {
        const response = await axios.get('/api/owners');
        this.owners = response.data;
      } catch (error) {
        console.error('オーナーの取得中にエラーが発生しました:', error);
      }
    },
    
    // 新規追加フォームを表示
    showAddForm() {
      // 新規作成用の空のオブジェクト（IDは含めない）
      this.editingOwner = {
        name: ''
      };
      this.showAddEditForm = true;
    },
    
    // 編集フォームを表示
    editOwner(owner) {
      // 編集用のオーナー情報（IDを含む）
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
        await axios.delete(`/api/owners/${id}`);
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

<style scoped>
table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

th, td {
  border: 1px solid #ddd;
  padding: 8px;
}

th {
  background-color: #f2f2f2;
}

.add-button {
  margin-bottom: 20px;
  padding: 8px 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.form-section {
  margin-bottom: 20px;
}
</style>
