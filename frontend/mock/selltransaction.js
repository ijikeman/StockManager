import { MockMethod } from 'vite-plugin-mock';
import Mock from 'mockjs';

// Generate mock data for sell transactions
const sellTransactions = Mock.mock({
  'list|5-10': [{
    'id|+1': 1,
    buyTransaction: {
      'id|+1': 1,
      stockLot: {
        'id|+1': 1,
        owner: {
          'id|+1': 1,
          name: '@name'
        },
        stock: {
          'id|+1': 1,
          code: '@string("upper", 4)',
          name: '@word(3, 6)',
          currentPrice: '@float(10, 2000, 2, 2)',
          minimalUnit: 100
        },
        currentUnit: '@integer(1, 10)',
        averagePrice: '@float(10, 2000, 2, 2)'
      },
      unit: '@integer(1, 10)',
      price: '@float(10, 2000, 2, 2)',
      fee: '@float(0, 500, 2, 2)',
      transactionDate: '@date("yyyy-MM-dd")'
    },
    unit: '@integer(1, 5)',
    price: '@float(10, 2000, 2, 2)',
    fee: '@float(0, 500, 2, 2)',
    transactionDate: '@date("yyyy-MM-dd")'
  }]
}).list;

export default [
  {
    url: '/api/selltransaction',
    method: 'get',
    response: () => {
      console.log('HITTING SELL TRANSACTION MOCK');
      return sellTransactions;
    },
  },
  {
    url: '/api/selltransaction/:id',
    method: 'get',
    response: ({ url }) => {
      const id = parseInt(url.split('/').pop());
      const transaction = sellTransactions.find(t => t.id === id);
      return transaction || { error: 'Not found' };
    }
  }
];
