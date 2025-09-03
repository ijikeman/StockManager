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
      return this.owner && this.owner.id != null && this.owner.id !== undefined;
    }
  },
  
  methods: {
    async saveOwner() {
      try {
        if (this.isEditing) {
          const updateData = {
            id: this.owner.id,
            name: this.formData.name
          };
          await axios.put(`/api/owners/${this.owner.id}`, updateData);
        } else {
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
          this.formData = {
            name: newOwner.name || ''
          };
        } else {
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

<!-- スタイルは共通CSSを使用するため削除 -->
