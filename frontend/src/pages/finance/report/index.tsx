import { Card, Row, Col, Statistic, Typography, Empty } from 'antd';
import { DollarOutlined, BankOutlined, LineChartOutlined } from '@ant-design/icons';

const { Title, Paragraph } = Typography;

const reports = [
  {
    key: 'income',
    title: '利润表',
    icon: <LineChartOutlined style={{ fontSize: 48, color: '#1677ff' }} />,
    description: '反映企业在一定会计期间的经营成果，包括营业收入、营业成本、各项费用及净利润等。',
    color: '#1677ff',
  },
  {
    key: 'balance',
    title: '资产负债表',
    icon: <BankOutlined style={{ fontSize: 48, color: '#52c41a' }} />,
    description: '反映企业在某一特定日期的财务状况，包括资产、负债和所有者权益的构成情况。',
    color: '#52c41a',
  },
  {
    key: 'cashflow',
    title: '现金流量表',
    icon: <DollarOutlined style={{ fontSize: 48, color: '#fa8c16' }} />,
    description: '反映企业在一定会计期间现金及现金等价物的流入和流出情况，包括经营、投资和筹资活动。',
    color: '#fa8c16',
  },
];

const placeholderMetrics = [
  { title: '营业收入', value: '---', prefix: <DollarOutlined /> },
  { title: '营业成本', value: '---', prefix: <DollarOutlined /> },
  { title: '净利润', value: '---', prefix: <DollarOutlined /> },
  { title: '总资产', value: '---', prefix: <BankOutlined /> },
];

export default function ReportPage() {
  return (
    <div>
      <Title level={4} style={{ marginBottom: 24 }}>财务报表</Title>

      <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
        {placeholderMetrics.map((metric) => (
          <Col xs={24} sm={12} md={6} key={metric.title}>
            <Card>
              <Statistic
                title={metric.title}
                value={metric.value}
                prefix={metric.prefix}
              />
            </Card>
          </Col>
        ))}
      </Row>

      <Row gutter={[24, 24]}>
        {reports.map((report) => (
          <Col xs={24} md={8} key={report.key}>
            <Card
              hoverable
              style={{ textAlign: 'center', height: '100%', borderTop: `4px solid ${report.color}` }}
              bodyStyle={{ padding: '32px 24px' }}
            >
              <div style={{ marginBottom: 16 }}>{report.icon}</div>
              <Title level={3}>{report.title}</Title>
              <Paragraph type="secondary" style={{ marginBottom: 16 }}>
                {report.description}
              </Paragraph>
              <Empty
                description="报表功能开发中，敬请期待..."
                image={Empty.PRESENTED_IMAGE_SIMPLE}
                style={{ marginTop: 16 }}
              />
            </Card>
          </Col>
        ))}
      </Row>
    </div>
  );
}
