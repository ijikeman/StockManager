
<template src="./templates/OwnerAddEdit.html"></template>

<script>
import axios from 'axios';

export default {
  props: {
    owner: {
      type: Object,
      default: () => ({ id: null, name: '' })
    }
  },
  
  emits: ['saved', 'cancelled'],
  
  data() {
    return {
      formData: {
        id: null,
        name: ''
      }
    };
  },
  
  computed: {
    isEditing() {
      return this.formData.id !== null;
    }
  },
  
  methods: {
    // オーナー情報を保存
    async saveOwner() {
      try {
        if (this.isEditing) {
          // 既存のオーナーを更新
          await axios.put(`/api/owners/${this.formData.id}`, this.formData);
        } else {
          // 新しいオーナーを追加
          await axios.post('/api/owners', this.formData);
        }
        
        this.$emit('saved');
        this.resetForm();
      } catch (error) {
        console.error('オーナーの保存中にエラーが発生しました:', error);
      }
    },
    
    // フォームをリセット
    resetForm() {
      this.formData = {
        id: null,
        name: ''
      };
      this.$emit('cancelled');
    }
  },
  
  watch: {
    owner: {
      handler(newOwner) {
        if (newOwner) {
          this.formData = { ...newOwner };
        } else {
          this.resetForm();
        }
      },
      immediate: true
    }
  }
};
</script>

<style scoped>
form {
  margin-bottom: 20px;
  padding: 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #f9f9f9;
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
