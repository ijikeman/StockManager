import { MockMethod } from 'vite-plugin-mock';
import Mock from 'mockjs';

const mockSectors = [
  { id: 1, name: 'Technology' },
  { id: 2, name: 'Finance' },
  { id: 3, name: 'Healthcare' },
  { id: 4, name: 'Energy' },
  { id: 5, name: 'Consumer' },
];

// モックデータを生成
let stocks = Mock.mock({
  // 5〜10個の在庫アイテムを生成
  'list|5-10': [{
    'id|+1': 1, // IDを1からインクリメント
    'productName': '@word(3, 5)', // 3〜5文字の単語で商品名を生成
    'quantity': '@integer(0, 100)', // 0〜100の整数で数量を生成
    'price': '@float(100, 1000, 2, 2)', // 100〜1000の浮動小数点数で価格を生成（小数点以下2桁）
    'sector|1': mockSectors, // mockSectorsからランダムに選択
    'owner': {
      'id|+1': 1, // オーナーIDを1からインクリメント
      'name': '@name' // 名前を生成
    },
    'description': '@sentence(10, 20)', // 10〜20単語の文章で説明を生成
    'lastUpdated': '@datetime("yyyy-MM-dd HH:mm:ss")' // 日時を生成
  }]
}).list;

export default [
  // 全ての在庫を取得するAPIエンドポイント
  {
    url: '/api/stock',
    method: 'get',
    response: () => {
      return stocks;
    },
  },
  // 指定されたIDの在庫を削除するAPIエンドポイント
  {
    url: '/api/stock/:id',
    method: 'delete',
    response: ({ url }) => {
      const id = parseInt(url.split('/').pop()); // URLからIDを抽出
      stocks = stocks.filter(stock => stock.id !== id); // IDに一致する在庫を削除
      return { success: true };
    },
  },
  // 指定されたIDの在庫を取得するAPIエンドポイント
  {
    url: '/api/stock/:id',
    method: 'get',
    response: ({ url }) => {
      const id = parseInt(url.split('/').pop()); // URLからIDを抽出
      const stock = stocks.find(stock => stock.id === id); // IDに一致する在庫を検索
      return stock;
    },
  },
  // 新しい在庫を追加するAPIエンドポイント
  {
    url: '/api/stock',
    method: 'post',
    response: ({ body }) => {
      // 新しい在庫にランダムなIDを割り当てて追加
      const newStock = { ...body, id: Mock.Random.integer(100, 1000) };
      stocks.push(newStock);
      return newStock;
    },
  },
  // 指定されたIDの在庫を更新するAPIエンドポイント
  {
    url: '/api/stock/:id',
    method: 'put',
    response: ({ url, body }) => {
      const id = parseInt(url.split('/').pop()); // URLからIDを抽出
      const index = stocks.findIndex(stock => stock.id === id); // IDに一致する在庫のインデックスを検索
      if (index !== -1) {
        stocks[index] = { ...stocks[index], ...body }; // 在庫を更新
        return stocks[index];
      }
      return null; // 見つからない場合はnullを返す
    },
  },
  ];