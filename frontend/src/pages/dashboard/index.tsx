import { Card, Row, Col, Statistic } from 'antd';
import { ArrowUpOutlined, ArrowDownOutlined, ShoppingCartOutlined, ShoppingOutlined, InboxOutlined, DollarOutlined, WarningOutlined } from '@ant-design/icons';
import { useQuery } from '@tanstack/react-query';
import ReactECharts from 'echarts-for-react';
import { getDashboardStats } from '@/services/dashboard';

export default function Dashboard() {
  const { data, isLoading } = useQuery({ queryKey: ['dashboard'], queryFn: getDashboardStats });

  const salesTrendOption = {
    xAxis: { type: 'category', data: data?.salesTrend?.map((i: any) => i.date) || [] },
    yAxis: { type: 'value' },
    series: [{ data: data?.salesTrend?.map((i: any) => i.amount) || [], type: 'line', smooth: true, areaStyle: {} }],
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 20, top: 20, bottom: 30 },
  };

  return (
    <div>
      <Row gutter={16}>
        <Col span={6}>
          <Card><Statistic title="今日销售额" value={data?.todaySales || 0} prefix="¥" precision={2} valueStyle={{ color: '#3f8600' }}
            suffix={<ArrowUpOutlined />} /></Card>
        </Col>
        <Col span={6}>
          <Card><Statistic title="今日采购额" value={data?.todayPurchase || 0} prefix="¥" precision={2} valueStyle={{ color: '#cf1322' }}
            suffix={<ArrowDownOutlined />} /></Card>
        </Col>
        <Col span={6}>
          <Card><Statistic title="本月销售额" value={data?.monthSales || 0} prefix="¥" precision={2} valueStyle={{ color: '#3f8600' }} /></Card>
        </Col>
        <Col span={6}>
          <Card><Statistic title="本月采购额" value={data?.monthPurchase || 0} prefix="¥" precision={2} valueStyle={{ color: '#cf1322' }} /></Card>
        </Col>
      </Row>

      <Row gutter={16} style={{ marginTop: 16 }}>
        <Col span={6}><Card><Statistic title="待审订单" value={data?.pendingOrders || 0} prefix={<ShoppingCartOutlined />} /></Card></Col>
        <Col span={6}><Card><Statistic title="库存预警" value={data?.lowStockCount || 0} prefix={<WarningOutlined />} valueStyle={{ color: data?.lowStockCount ? '#cf1322' : undefined }} /></Card></Col>
        <Col span={6}><Card><Statistic title="应收账款" value={data?.receivablesTotal || 0} prefix="¥" precision={2} valueStyle={{ color: '#faad14' }} /></Card></Col>
        <Col span={6}><Card><Statistic title="应付账款" value={data?.payablesTotal || 0} prefix="¥" precision={2} valueStyle={{ color: '#fa541c' }} /></Card></Col>
      </Row>

      <Card title="近30天销售趋势" style={{ marginTop: 16 }}>
        <ReactECharts option={salesTrendOption} style={{ height: 350 }} />
      </Card>
    </div>
  );
}
