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
    'code': '@string("number", 3, 5)', // 3文字から5文字の数字でコードを生成
    'name': '@word(3, 5)', // 3〜5文字の単語で商品名を生成
    'current_price': '@float(100, 1000, 2, 2)', // 100〜1000の浮動小数点数で現在価格を生成（小数点以下2桁）
    'dividend': '@float(0, 10, 2, 2)', // 0〜10の浮動小数点数で配当を生成（小数点以下2桁）
    'release_date': '@date("yyyy-MM-dd")', // YYYY-MM-DD形式でリリース日を生成
    'sector_id|1': mockSectors.map(s => s.id), // mockSectorsからランダムにIDを選択
  }]
}).list;

export default [
  // 全ての在庫を取得するAPIエンドポイント
  {
    url: '/api/stock',
    method: 'get',
    response: () => {
      // 各在庫にセクター情報を追加
      const detailedStocks = stocks.map(stock => {
        const sector = mockSectors.find(s => s.id === stock.sector_id);
        return {
          ...stock,
          sector: sector || null, // セクターが見つからない場合はnull
        };
      });
      return detailedStocks;
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
      if (stock) {
        const sector = mockSectors.find(s => s.id === stock.sector_id);
        return {
          ...stock,
          sector: sector || null,
        };
      }
      return null; // 在庫が見つからない場合はnullを返す
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
  // 銘柄の株価を更新するAPIエンドポイント
  {
    url: '/api/stocks/:code/update',
    method: 'post',
    response: ({ url }) => {
      const code = url.split('/')[3]; // URLから銘柄コードを抽出
      const stock = stocks.find(s => s.code === code);
      if (stock) {
        // 現在の価格をランダムに更新
        stock.current_price = Mock.Random.float(100, 1000, 2, 2);
        // 対応するセクター情報を取得
        const sector = mockSectors.find(s => s.id === stock.sector_id);
        return {
          ...stock,
          sector: sector || null,
        };
      }
      return null; // 銘柄が見つからない場合はnullを返す
    },
  },
  ];