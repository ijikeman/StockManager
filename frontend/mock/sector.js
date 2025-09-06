import { MockMethod } from 'vite-plugin-mock';
import Mock from 'mockjs';

// モックデータを生成
let sectors = Mock.mock({
  'list|3-5': [{
    'id|+1': 1,
    'name|1': ['Technology', 'Finance', 'Healthcare', 'Energy', 'Consumer', 'Industrials', 'Utilities', 'Real Estate']
  }]
}).list;

export default [
  // 全てのセクターを取得するAPIエンドポイント
  {
    url: '/api/sector',
    method: 'get',
    response: () => {
      console.log('Returning sector:', sectors);
      return sectors;
    },
  },
  // 指定されたIDのセクターを取得するAPIエンドポイント
  {
    url: '/api/sector/:id',
    method: 'get',
    response: ({ url }) => {
      const id = parseInt(url.split('/').pop());
      const sector = sectors.find(sector => sector.id === id);
      return sector;
    },
  },
  // 指定されたIDのセクターを更新するAPIエンドポイント
  {
    url: '/api/sector/:id',
    method: 'put',
    response: ({ url, body }) => {
      const id = parseInt(url.split('/').pop());
      const updatedName = body.name;
      const sectorIndex = sectors.findIndex(sector => sector.id === id);
      if (sectorIndex !== -1) {
        sectors[sectorIndex].name = updatedName;
        return sectors[sectorIndex];
      }
      return null; // Or an error response if not found
    },
  },
  // 指定されたIDのセクターを削除するAPIエンドポイント
  {
    url: '/api/sector/:id',
    method: 'delete',
    response: ({ url }) => {
      const id = parseInt(url.split('/').pop());
      const initialLength = sectors.length;
      sectors = sectors.filter(sector => sector.id !== id);
      if (sectors.length < initialLength) {
        return { message: `Sector with ID ${id} deleted successfully.` };
      }
      return { message: `Sector with ID ${id} not found.`, status: 404 }; // Or an appropriate error response
    },
  },
];
