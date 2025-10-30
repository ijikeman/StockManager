import { MockMethod } from 'vite-plugin-mock';
import Mock from 'mockjs';

// Generate mock data for benefit history
const benefitHistories = Mock.mock({
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
        minimalUnit: 100
      },
      currentUnit: '@integer(1, 10)'
    },
    benefit: '@float(500, 10000, 2, 2)',
    paymentDate: '@date("yyyy-MM-dd")'
  }]
}).list;

export default [
  {
    url: '/api/benefithistory',
    method: 'get',
    response: () => {
      console.log('HITTING BENEFIT HISTORY MOCK');
      return benefitHistories;
    },
  },
  {
    url: '/api/benefithistory/:id',
    method: 'get',
    response: ({ url }) => {
      const id = parseInt(url.split('/').pop());
      const history = benefitHistories.find(h => h.id === id);
      return history || { error: 'Not found' };
    }
  },
  {
    url: '/api/benefithistory/:id',
    method: 'delete',
    response: ({ url }) => {
      const id = parseInt(url.split('/').pop());
      const index = benefitHistories.findIndex(h => h.id === id);
      if (index !== -1) {
        benefitHistories.splice(index, 1);
      }
      return { success: true };
    }
  },
  {
    url: '/api/benefithistory',
    method: 'post',
    response: ({ body }) => {
      const newHistory = {
        id: Mock.Random.integer(100, 1000),
        stockLot: benefitHistories[0]?.stockLot || {},
        benefit: body.benefit,
        paymentDate: body.paymentDate
      };
      benefitHistories.push(newHistory);
      return newHistory;
    }
  }
];
