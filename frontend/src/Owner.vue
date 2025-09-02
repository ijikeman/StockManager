<template src="./owner.html"></template>

<script>
// axiosをインポートしてHTTPリクエストを送信できるようにします
import axios from 'axios';

export default {
  data() {
    return {
      // オーナーのリストを格納する配列
      owners: [],
      // 現在のオーナー情報を格納するオブジェクト
      owner: {
        id: null,
        name: ''
      }
    };
  },
  methods: {
    // APIからオーナーのリストを取得します
    fetchOwners() {
      axios.get('/api/owners')
        .then(response => {
          // 取得したデータをowners配列に設定します
          this.owners = response.data;
        })
        .catch(error => {
          console.error('オーナーの取得中にエラーが発生しました:', error);
        });
    },
    // オーナー情報を保存します
    saveOwner() {
      if (this.owner.id) {
        // IDが存在する場合は、既存のオーナーを更新します
        axios.put(`/api/owners/${this.owner.id}`, this.owner)
          .then(() => {
            // オーナーリストを再取得し、フォームをリセットします
            this.fetchOwners();
            this.resetForm();
          })
          .catch(error => {
            console.error('オーナーの更新中にエラーが発生しました:', error);
          });
      } else {
        // IDが存在しない場合は、新しいオーナーを追加します
        axios.post('/api/owners', this.owner)
          .then(() => {
            // オーナーリストを再取得し、フォームをリセットします
            this.fetchOwners();
            this.resetForm();
          })
          .catch(error => {
            console.error('オーナーの追加中にエラーが発生しました:', error);
          });
      }
    },
    // 編集するオーナーの情報をフォームに設定します
    editOwner(owner) {
      this.owner = { ...owner };
    },
    // 指定されたIDのオーナーを削除します
    deleteOwner(id) {
      axios.delete(`/api/owners/${id}`)
        .then(() => {
          // オーナーリストを再取得します
          this.fetchOwners();
        })
        .catch(error => {
          console.error('オーナーの削除中にエラーが発生しました:', error);
        });
    },
    // フォームをリセットします
    resetForm() {
      this.owner = {
        id: null,
        name: ''
      };
    }
  },
  // コンポーネントがマウントされたときに実行されます
  mounted() {
    // オーナーのリストを初期表示のために取得します
    this.fetchOwners();
  }
};
</script>

<style>
/* テーブルのスタイル */
table {
  width: 100%;
  border-collapse: collapse;
}
/* テーブルのヘッダーとセルのスタイル */
th, td {
  border: 1px solid #ddd;
  padding: 8px;
}
/* テーブルヘッダーの背景色 */
th {
  background-color: #f2f2f2;
}
/* フォームの下の余白 */
form {
  margin-bottom: 20px;
}
</style>