import { MockMethod } from 'vite-plugin-mock';
import Mock from 'mockjs';

// モックデータを生成
const stocklots = Mock.mock({
  'list|20-30': [{
    'id|+1': 1,
    'date': '@date("yyyy-MM-dd")',
    'type|1': ['BUY', 'SELL'],
    'stock': {
      'code': '@string("number", 4)',
      'name': '@ctitle(3, 6)'
    },
    'owner_name': '@cname',
    'unit': '@integer(100, 1000)',
    'price': '@float(1000, 5000, 2, 2)',
    'fees': '@integer(100, 500)',
    'lot_id': '@integer(1, 20)'
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
    url: '/api/stocklot',
    method: 'post',
    response: ({ body }) => {
      const newStocklot = { ...body, id: Mock.Random.integer(100, 1000) };
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