<template src="./templates/OwnerAddEdit.html"></template>

<script>
import axios from 'axios';

export default {
  // コンポーネント名を'OwnerAddEdit'に設定
  name: 'OwnerAddEdit',
  // 親コンポーネントから受け取るプロパティ
  props: {
    // 編集対象のオーナー情報
    owner: {
      type: Object,
      required: true
    }
  },
  
  // 親コンポーネントに通知するイベント
  emits: ['saved', 'cancelled'],
  
  // コンポーネントのデータ
  data() {
    return {
      // フォームのデータ
      formData: {
        name: ''
      }
    };
  },
  
  // 算出プロパティ
  computed: {
    // 編集モードかどうかを判定
    isEditing() {
      return this.owner && this.owner.id != null && this.owner.id !== undefined;
    }
  },
  
  // メソッド
  methods: {
    // オーナー情報を保存
    async saveOwner() {
      try {
        if (this.isEditing) {
          const updateData = {
            id: this.owner.id,
            name: this.formData.name
          };
          await axios.put(`/api/owner/${this.owner.id}`, updateData);
        } else {
          const createData = {
            name: this.formData.name
          };
          await axios.post('/api/owner', createData);
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
