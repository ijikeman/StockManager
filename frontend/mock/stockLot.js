import { MockMethod } from 'vite-plugin-mock';
import Mock from 'mockjs';

let stockLots = Mock.mock({
  'list|3-5': [{
    'id|+1': 1,
    'owner_id|+1': 1,
    'owner_name': '@name',
    'stock_code': /^[0-9]{4}$/,
    'stock_name': '@ctitle(4, 8)',
    'unit|1-10': 1,
    'quantity': function() { return this.unit * 100; },
    'is_nisa': '@boolean',
    'minimalUnit': 100,
  }]
}).list;

export default [
  {
    url: '/api/stock-lot',
    method: 'get',
    response: () => {
      return stockLots;
    },
  },
  {
    url: '/api/stock-lot/:id',
    method: 'put',
    response: ({ url, body }) => {
      const id = parseInt(url.split('/').pop());
      const index = stockLots.findIndex(lot => lot.id === id);
      if (index !== -1) {
        // Manually map properties to handle case difference
        stockLots[index].unit = body.unit;
        stockLots[index].is_nisa = body.isNisa;
        stockLots[index].quantity = body.unit * stockLots[index].minimalUnit;
        return stockLots[index];
      }
      return null;
    },
  },
  {
    url: '/api/stock-lot/:id',
    method: 'delete',
    response: ({ url }) => {
      const id = parseInt(url.split('/').pop());
      stockLots = stockLots.filter(lot => lot.id !== id);
      return { success: true };
    },
  },
];