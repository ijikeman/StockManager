import { MockMethod } from 'vite-plugin-mock';
import Mock from 'mockjs';

// Default minimal unit for Japanese stocks (standard is 100 shares per unit)
const DEFAULT_MINIMAL_UNIT = 100;

// Generate mock data for ProfitlossStockLotDto
const generateProfitLossStockLotData = () => {
  return Mock.mock({
    'items|5-10': [{
      'stockCode': '@string("upper", 4)',
      'stockName': '@word(3, 6)',
      'purchasePrice': '@float(100, 2000, 2, 2)',
      'currentPrice': '@float(100, 2500, 2, 2)',
      'currentUnit|1-20': 1,
      'totalIncoming': '@float(0, 50000, 2, 2)',
      'totalBenefit': '@float(0, 10000, 2, 2)',
      'buyTransactionDate': '@date("yyyy-MM-dd")'
    }]
  }).items;
};

let profitLossData = generateProfitLossStockLotData();

export default [
  {
    url: '/api/profitloss',
    method: 'get',
    response: () => {
      console.log('HITTING PROFIT/LOSS STOCK LOT MOCK');
      return profitLossData;
    },
  }
];
