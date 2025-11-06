import { MockMethod } from 'vite-plugin-mock';
import Mock from 'mockjs';

// Static data for AAPL to ensure it's always available for tests
const aaplStockLot = {
  id: 1,
  owner: {
    id: 1,
    name: 'Test Owner',
  },
  stock: {
    id: 1,
    code: 'AAPL',
    name: 'AAPL',
    currentPrice: 170.00,
    incoming: 0.25,
  },
  currentUnit: 100,
  minimalUnit: 1,
  averagePrice: 150.00,
  purchaseDate: '2023-01-15',
  incoming: 0.25,
  totalIncoming: 200.00,
};


// Generate random mock data
const randomStocklots = Mock.mock({
  'list|20-29': [{
    'id|+1': 2, // Start ID from 2 to avoid conflict
    owner: {
      'id|+1': 2,
      name: '@name',
    },
    stock: {
      'id|+1': 2,
      code: '@string("upper", 4)',
      name: '@word(3, 6)',
      currentPrice: '@float(10, 2000, 2, 2)',
      incoming: '@float(0, 10, 2, 2)',
    },
    'currentUnit': '@integer(10, 500)',
    'minimalUnit': 1,
    'averagePrice': '@float(10, 2000, 2, 2)',
    'purchaseDate': '@date("yyyy-MM-dd")',
    'incoming': '@float(0, 10, 2, 2)',
    'totalIncoming': '@float(0, 500, 2, 2)',
  }]
}).list;

const stocklots = [aaplStockLot, ...randomStocklots];

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
        owner: { id: body.ownerId, name: 'Mock Owner' },
        stock: { id: body.stockId, name: 'Mock Stock', code: 'MSFT', currentPrice: 450.0, incoming: 0.5 },
        currentUnit: body.unit,
        averagePrice: body.price,
        incoming: 0.5,
        totalIncoming: 0.00,
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