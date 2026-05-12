import request from '@/utils/request';

export function getDashboardStats(): Promise<{
  todaySales: number;
  todayPurchase: number;
  monthSales: number;
  monthPurchase: number;
  pendingOrders: number;
  lowStockCount: number;
  receivablesTotal: number;
  payablesTotal: number;
  salesTrend: Array<{ date: string; amount: number }>;
}> {
  return request.get('/dashboard/stats');
}
