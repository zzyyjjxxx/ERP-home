import { useEffect, useState } from 'react';
import { Row, Col, Card, Statistic } from 'antd';
import { ShoppingCartOutlined, ShoppingOutlined, InboxOutlined, DollarOutlined } from '@ant-design/icons';
import request from '@/utils/request';

export default function Dashboard() {
  const [stats, setStats] = useState<any>({});

  useEffect(() => {
    request.get('/dashboard/stats').then(setStats).catch(() => {});
  }, []);

  return (
    <div>
      <Row gutter={[16, 16]}>
        <Col span={6}>
          <Card><Statistic title="商品总数" value={stats.productCount || 0} prefix={<InboxOutlined />} /></Card>
        </Col>
        <Col span={6}>
          <Card><Statistic title="供应商数" value={stats.supplierCount || 0} prefix={<ShoppingOutlined />} /></Card>
        </Col>
        <Col span={6}>
          <Card><Statistic title="客户数" value={stats.customerCount || 0} prefix={<ShoppingCartOutlined />} /></Card>
        </Col>
        <Col span={6}>
          <Card><Statistic title="用户数" value={stats.userCount || 0} prefix={<DollarOutlined />} /></Card>
        </Col>
      </Row>
      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col span={12}>
          <Card title="待处理采购订单">
            <Statistic title="待审订单" value={stats.pendingPurchase || 0} />
          </Card>
        </Col>
        <Col span={12}>
          <Card title="待处理销售订单">
            <Statistic title="待审订单" value={stats.pendingSales || 0} />
          </Card>
        </Col>
      </Row>
    </div>
  );
}
