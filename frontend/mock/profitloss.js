import { MockMethod } from 'vite-plugin-mock';
import Mock from 'mockjs';

// Generate mock data for profit/loss summary
// This combines income, benefit, stocklot evaluation, and sell transactions
const generateProfitLossSummary = () => {
  const summary = Mock.mock({
    'incomeItems|3-5': [{
      'id|+1': 1,
      type: 'income',
      date: '@date("yyyy-MM-dd")',
      stockCode: '@string("upper", 4)',
      stockName: '@word(3, 6)',
      ownerName: '@name',
      description: '配当金',
      amount: '@float(1000, 10000, 2, 2)',
      stockLotId: '@integer(1, 100)'
    }],
    'benefitItems|2-4': [{
      'id|+1': 1,
      type: 'benefit',
      date: '@date("yyyy-MM-dd")',
      stockCode: '@string("upper", 4)',
      stockName: '@word(3, 6)',
      ownerName: '@name',
      description: '株主優待',
      amount: '@float(500, 5000, 2, 2)',
      stockLotId: '@integer(1, 100)'
    }],
    'sellItems|3-6': [{
      'id|+1': 1,
      type: 'sell',
      date: '@date("yyyy-MM-dd")',
      stockCode: '@string("upper", 4)',
      stockName: '@word(3, 6)',
      ownerName: '@name',
      description: '売却益',
      unit: '@integer(1, 10)',
      buyPrice: '@float(100, 2000, 2, 2)',
      sellPrice: '@float(100, 2000, 2, 2)',
      fee: '@float(0, 500, 2, 2)',
      'amount': function() {
        const unit = this.unit;
        const minimalUnit = 100;
        const totalUnits = unit * minimalUnit;
        const profit = (this.sellPrice - this.buyPrice) * totalUnits - this.fee;
        return profit;
      }
    }],
    'stockLotEvaluations|5-8': [{
      'id|+1': 1,
      type: 'evaluation',
      stockCode: '@string("upper", 4)',
      stockName: '@word(3, 6)',
      ownerName: '@name',
      description: '含み損益',
      unit: '@integer(1, 10)',
      averagePrice: '@float(100, 2000, 2, 2)',
      currentPrice: '@float(100, 2000, 2, 2)',
      'amount': function() {
        const unit = this.unit;
        const minimalUnit = 100;
        const totalUnits = unit * minimalUnit;
        return (this.currentPrice - this.averagePrice) * totalUnits;
      }
    }]
  });

  // Combine all items into a single array
  const allItems = [
    ...summary.incomeItems,
    ...summary.benefitItems,
    ...summary.sellItems,
    ...summary.stockLotEvaluations
  ];

  // Calculate totals
  const totalIncome = summary.incomeItems.reduce((sum, item) => sum + item.amount, 0);
  const totalBenefit = summary.benefitItems.reduce((sum, item) => sum + item.amount, 0);
  const totalSellProfit = summary.sellItems.reduce((sum, item) => sum + item.amount, 0);
  const totalEvaluation = summary.stockLotEvaluations.reduce((sum, item) => sum + item.amount, 0);
  const totalProfitLoss = totalIncome + totalBenefit + totalSellProfit + totalEvaluation;

  return {
    items: allItems,
    summary: {
      totalIncome,
      totalBenefit,
      totalSellProfit,
      totalEvaluation,
      totalProfitLoss
    }
  };
};

let profitLossData = generateProfitLossSummary();

export default [
  {
    url: '/api/profitloss',
    method: 'get',
    response: () => {
      console.log('HITTING PROFIT/LOSS SUMMARY MOCK');
      return profitLossData;
    },
  }
];
