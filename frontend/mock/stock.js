import { MockMethod } from 'vite-plugin-mock';
import Mock from 'mockjs';

let stocks = Mock.mock({
  'list|5-10': [{
    'id|+1': 1,
    'productName': '@word(3, 5)',
    'quantity': '@integer(0, 100)',
    'price': '@float(100, 1000, 2, 2)',
    'sector': {
      'id|+1': 1,
      'name': '@word(1)'
    },
    'owner': {
      'id|+1': 1,
      'name': '@name'
    },
    'description': '@sentence(10, 20)',
    'lastUpdated': '@datetime("yyyy-MM-dd HH:mm:ss")'
  }]
}).list;

export default [
  {
    url: '/api/stocks',
    method: 'get',
    response: () => {
      return stocks;
    },
  },
  {
    url: '/api/stocks/:id',
    method: 'delete',
    response: ({ url }) => {
      const id = parseInt(url.split('/').pop());
      stocks = stocks.filter(stock => stock.id !== id);
      return { success: true };
    },
  },
  {
    url: '/api/stocks/:id',
    method: 'get',
    response: ({ url }) => {
      const id = parseInt(url.split('/').pop());
      const stock = stocks.find(stock => stock.id === id);
      return stock;
    },
  },
  {
    url: '/api/stocks',
    method: 'post',
    response: ({ body }) => {
      const newStock = { ...body, id: Mock.Random.integer(100, 1000) };
      stocks.push(newStock);
      return newStock;
    },
  },
  {
    url: '/api/stocks/:id',
    method: 'put',
    response: ({ url, body }) => {
      const id = parseInt(url.split('/').pop());
      const index = stocks.findIndex(stock => stock.id === id);
      if (index !== -1) {
        stocks[index] = { ...stocks[index], ...body };
        return stocks[index];
      }
      return null;
    },
  },
  {
    url: '/api/sectors',
    method: 'get',
    response: () => {
        return [
            { id: 1, name: 'Technology' },
            { id: 2, name: 'Finance' },
            { id: 3, name: 'Healthcare' },
        ];
    },
  }
] as MockMethod[];
