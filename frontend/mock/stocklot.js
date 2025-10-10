import { MockMethod } from 'vite-plugin-mock';
import Mock from 'mockjs';

// モックデータを生成
const stocklots = Mock.mock({
  'list|20-30': [{
    'id|+1': 1,
    owner: {
      'id|+1': 1,
      name: '@name', // Using a more generic name generator
    },
    stock: {
      'id|+1': 1,
      code: '@string("number", 4)',
      name: '@word(3, 6)', // Using a more generic word generator
      currentPrice: '@float(1000, 5000, 2, 2)',
    },
    'currentUnit': '@integer(100, 1000)',
    'averagePrice': '@float(800, 4500, 2, 2)',
    'purchaseDate': '@date("yyyy-MM-dd")',
  }]
}).list;

export default [
  {
    url: '/api/stocklot',
    method: 'get',
    response: () => {
      return stocklots;
    },
  },
  {
    url: '/api/stocklot/add',
    method: 'post',
    response: ({ body }) => {
      const newStocklot = {
        id: Mock.Random.integer(100, 1000),
        owner: { id: body.ownerId, name: 'Mock Owner' }, // Assuming a mock owner name
        stock: { id: body.stockId, name: 'Mock Stock', code: 'MSFT', currentPrice: 450.0 }, // Assuming mock stock details
        currentUnit: body.unit,
        averagePrice: body.price, // Use the purchase price as the initial average price
      };
      stocklots.push(newStocklot);
      return newStocklot;
    }
  },
  {
    url: '/api/stocklot/:id',
    method: 'get',
    response: ({ url }) => {
      const id = parseInt(url.split('/').pop());
      const stocklot = stocklots.find(t => t.id === id);
      return stocklot;
    }
  }
];