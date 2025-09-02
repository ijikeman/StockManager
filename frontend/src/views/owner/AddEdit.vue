<template src="./templates/OwnerAddEdit.html"></template>

<script>
import axios from 'axios';

export default {
  props: {
    owner: {
      type: Object,
      required: true
    }
  },
  
  emits: ['saved', 'cancelled'],
  
  data() {
    return {
      formData: {
        name: ''
      }
    };
  },
  
  computed: {
    isEditing() {
      // IDが存在し、かつnullでもundefinedでもない場合のみ編集モード
      return this.owner && this.owner.id != null && this.owner.id !== undefined;
    }
  },
  
  methods: {
    // オーナー情報を保存
    async saveOwner() {
      try {
        if (this.isEditing) {
          // 既存のオーナーを更新（IDを含める）
          const updateData = {
            id: this.owner.id,
            name: this.formData.name
          };
          await axios.put(`/api/owners/${this.owner.id}`, updateData);
        } else {
          // 新しいオーナーを追加（IDは含めない）
          const createData = {
            name: this.formData.name
          };
          await axios.post('/api/owners', createData);
        }
        
        this.$emit('saved');
      } catch (error) {
        console.error('オーナーの保存中にエラーが発生しました:', error);
        alert('保存に失敗しました。もう一度お試しください。');
      }
    },
    
    // キャンセル処理
    cancel() {
      this.$emit('cancelled');
    }
  },
  
  watch: {
    owner: {
      handler(newOwner) {
        if (newOwner) {
          // 編集時はnameのみをコピー（IDはpropsから直接参照）
          this.formData = {
            name: newOwner.name || ''
          };
        } else {
          // 新規作成時は空の状態に
          this.formData = {
            name: ''
          };
        }
      },
      immediate: true
    }
  }
};
</script>

<style scoped>
.form-container {
  padding: 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #f9f9f9;
  margin-bottom: 20px;
}

.form-title {
  margin-bottom: 16px;
  color: #333;
}

input[type="text"] {
  padding: 8px;
  margin-right: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 200px;
}

button {
  padding: 8px 16px;
  margin-right: 8px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button[type="submit"] {
  background-color: #28a745;
  color: white;
}

button[type="button"] {
  background-color: #6c757d;
  color: white;
}
</style>
