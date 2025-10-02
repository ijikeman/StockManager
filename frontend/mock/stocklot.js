import { MockMethod } from 'vite-plugin-mock';
import Mock from 'mockjs';

// モックデータを生成
const stocklots = Mock.mock({
  'list|10-20': [{
    'id|+1': 1,
    'stock_code': '@string("number", 4)',
    'stock_name': '@ctitle(3, 6)',
    'owner_id|1-5': 1,
    'owner_name': '@cname',
    'is_nisa|1': true,
    'unit': '@integer(100, 1000)',
    'acquisition_price': '@float(1000, 5000, 2, 2)',
    'current_price': '@float(1000, 5000, 2, 2)',
    'profit_loss': '@float(-1000, 1000, 2, 2)',
    'dividend': '@float(0, 100, 2, 2)',
    'status': '保有中'
  }]
}).list;

export default [
  {
    url: '/api/holding',
    method: 'get',
    response: () => {
      const holdings = {
        'true': [],
        'false': []
      };
      stocklots.forEach(lot => {
        if (lot.is_nisa) {
          holdings['true'].push(lot);
        } else {
          holdings['false'].push(lot);
        }
      });
      return holdings;
    },
  },
  {
    url: '/api/holding/owner/:id',
    method: 'get',
    response: ({ url }) => {
      const ownerId = parseInt(url.split('/').pop());
      const holdings = {
        'true': [],
        'false': []
      };
      stocklots.forEach(lot => {
        if (lot.owner_id === ownerId) {
          if (lot.is_nisa) {
            holdings['true'].push(lot);
          } else {
            holdings['false'].push(lot);
          }
        }
      });
      return holdings;
    }
  },
  {
    url: '/api/holding/:id',
    method: 'get',
    response: ({ url }) => {
      const id = parseInt(url.split('/').pop());
      const lot = stocklots.find(l => l.id === id);

      if (lot) {
        return {
          id: lot.id,
          stock: {
            code: lot.stock_code,
            name: lot.stock_name,
          },
          owner: {
            id: lot.owner_id,
            name: lot.owner_name,
          },
          unit: lot.unit,
          price: lot.acquisition_price,
          is_nisa: lot.is_nisa,
        };
      } else {
        return null;
      }
    }
  }
];