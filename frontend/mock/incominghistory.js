import { MockMethod } from 'vite-plugin-mock';
import Mock from 'mockjs';

// Generate mock data for incoming history
const incomingHistories = Mock.mock({
  'list|5-10': [{
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
        incoming: '@float(1, 50, 2, 2)',
        minimalUnit: 100
      },
      currentUnit: '@integer(1, 10)'
    },
    incoming: '@float(100, 5000, 2, 2)',
    paymentDate: '@date("yyyy-MM-dd")'
  }]
}).list;

export default [
  {
    url: '/api/incominghistory',
    method: 'get',
    response: () => {
      console.log('HITTING INCOMING HISTORY MOCK');
      return incomingHistories;
    },
  },
  {
    url: '/api/incominghistory/:id',
    method: 'get',
    response: ({ url }) => {
      const id = parseInt(url.split('/').pop());
      const history = incomingHistories.find(h => h.id === id);
      return history || { error: 'Not found' };
    }
  },
  {
    url: '/api/incominghistory/:id',
    method: 'delete',
    response: ({ url }) => {
      const id = parseInt(url.split('/').pop());
      const index = incomingHistories.findIndex(h => h.id === id);
      if (index !== -1) {
        incomingHistories.splice(index, 1);
      }
      return { success: true };
    }
  },
  {
    url: '/api/incominghistory',
    method: 'post',
    response: ({ body }) => {
      const newHistory = {
        id: Mock.Random.integer(100, 1000),
        stockLot: incomingHistories[0]?.stockLot || {},
        incoming: body.incoming,
        paymentDate: body.paymentDate
      };
      incomingHistories.push(newHistory);
      return newHistory;
    }
  }
];