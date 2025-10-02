import { MockMethod } from 'vite-plugin-mock';
import Mock from 'mockjs';

// モックデータを生成
const transactions = Mock.mock({
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
    url: '/api/transaction',
    method: 'get',
    response: () => {
      return transactions;
    },
  },
  {
    url: '/api/transaction',
    method: 'post',
    response: ({ body }) => {
      const newTransaction = { ...body, id: Mock.Random.integer(100, 1000) };
      transactions.push(newTransaction);
      return newTransaction;
    }
  },
  {
    url: '/api/transaction/:id',
    method: 'get',
    response: ({ url }) => {
      const id = parseInt(url.split('/').pop());
      const transaction = transactions.find(t => t.id === id);
      return transaction;
    }
  }
];