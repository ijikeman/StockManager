import { MockMethod } from 'vite-plugin-mock';
import Mock from 'mockjs';

// モックデータを生成
let owners = Mock.mock({
  // 3〜5個のオーナーアイテムを生成
  'list|3-5': [{
    'id|+1': 1, // IDを1からインクリメント
    'name': '@name', // 名前を生成
  }]
}).list;

export default [
  // 全てのオーナーを取得するAPIエンドポイント
  {
    url: '/api/owners',
    method: 'get',
    response: () => {
      return owners;
    },
  },
  // 指定されたIDのオーナーを削除するAPIエンドポイント
  {
    url: '/api/owners/:id',
    method: 'delete',
    response: ({ url }) => {
      const id = parseInt(url.split('/').pop()); // URLからIDを抽出
      owners = owners.filter(owner => owner.id !== id); // IDに一致するオーナーを削除
      return { success: true };
    },
  },
  // 新しいオーナーを追加するAPIエンドポイント
  {
    url: '/api/owners',
    method: 'post',
    response: ({ body }) => {
        // 新しいオーナーにランダムなIDを割り当てて追加
        const newOwner = { ...body, id: Mock.Random.integer(100, 1000) };
        owners.push(newOwner);
        return newOwner;
    }
  },
  // 指定されたIDのオーナーを更新するAPIエンドポイント
  {
      url: '/api/owners/:id',
      method: 'put',
      response: ({ url, body }) => {
        const id = parseInt(url.split('/').pop()); // URLからIDを抽出
        const index = owners.findIndex(owner => owner.id === id); // IDに一致するオーナーのインデックスを検索
        if (index !== -1) {
          owners[index] = { ...owners[index], ...body }; // オーナーを更新
          return owners[index];
        }
        return null; // 見つからない場合はnullを返す
      }
  }
];