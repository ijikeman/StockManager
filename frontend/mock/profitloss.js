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
      'minimalUnit|100-1000': 100,
      'purchasePrice': '@float(100, 2000, 2, 2)',
      'currentPrice': '@float(100, 2500, 2, 2)',
      'currentUnit|1-20': 1,
      'totalIncoming': '@float(0, 50000, 2, 2)',
      'totalBenefit': '@float(0, 10000, 2, 2)',
      'buyTransactionDate': '@date("yyyy-MM-dd")',
      'isNisa|1': [true, false]
    }]
  }).items;
};

// Generate mock data for ProfitlossDto (realized profit/loss)
const generateRealizedProfitLossData = () => {
  return Mock.mock({
    'items|3-8': [{
      'stockCode': '@string("upper", 4)',
      'stockName': '@word(3, 6)',
      'minimalUnit|100-1000': 100,
      'purchasePrice': '@float(100, 2000, 2, 2)',
      'sellPrice': '@float(100, 2500, 2, 2)',
      'sellUnit|1-10': 1,
      'totalIncoming': '@float(0, 30000, 2, 2)',
      'totalBenefit': '@float(0, 8000, 2, 2)',
      'profitLoss': '@float(-50000, 100000, 2, 2)',
      'buyTransactionDate': '@date("yyyy-MM-dd")',
      'sellTransactionDate': '@date("yyyy-MM-dd")',
      'ownerName': '@word(2, 4)',
      'isNisa|1': [true, false]
    }]
  }).items;
};

let profitLossData = generateProfitLossStockLotData();
let realizedProfitLossData = generateRealizedProfitLossData();

export default [
  {
    url: '/api/profitloss',
    method: 'get',
    response: () => {
      console.log('HITTING PROFIT/LOSS STOCK LOT MOCK');
      return profitLossData;
    },
  },
  {
    url: '/api/profitloss/realized',
    method: 'get',
    response: () => {
      console.log('HITTING REALIZED PROFIT/LOSS MOCK');
      return realizedProfitLossData;
    },
  }
];
