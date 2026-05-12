import { useRef } from 'react';
import { ProTable } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Tag, message } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getDeliveryList, auditDelivery } from '@/services/sales';
import PermissionBtn from '@/components/PermissionBtn';

export default function DeliveryList() {
  const actionRef = useRef<ActionType>();

  const columns: ProColumns[] = [
    { title: '发货单号', dataIndex: 'deliveryNo', key: 'deliveryNo' },
    { title: '客户ID', dataIndex: 'customerId', key: 'customerId', search: false },
    { title: '发货日期', dataIndex: 'deliveryDate', key: 'deliveryDate', search: false, valueType: 'date' },
    { title: '总数量', dataIndex: 'totalQty', key: 'totalQty', search: false },
    { title: '状态', dataIndex: 'status', key: 'status', render: (_, r) => <Tag color={r.status === 1 ? 'green' : 'orange'}>{r.status === 1 ? '完成' : '待审'}</Tag> },
    {
      title: '操作', key: 'action', search: false,
      render: (_, record) => (
        record.status === 0 && <PermissionBtn permission="sales:delivery:audit" type="link" onClick={async () => { await auditDelivery(record.id); message.success('审核成功'); actionRef.current?.reload(); }}>审核</PermissionBtn>
      ),
    },
  ];

  return (
    <ProTable columns={columns} request={async (params) => { const data = await getDeliveryList(params); return { data: data.records, total: data.total, success: true }; }}
      actionRef={actionRef} rowKey="id" search={{ labelWidth: 'auto' }}
      toolBarRender={() => [<PermissionBtn key="add" permission="sales:delivery:add" type="primary" icon={<PlusOutlined />}>新增发货单</PermissionBtn>]} />
  );
}
