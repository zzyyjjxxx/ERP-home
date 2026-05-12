import { useRef } from 'react';
import { ProTable } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Tag, message, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getOrderList, auditOrder, cancelOrder, deleteOrder } from '@/services/sales';
import PermissionBtn from '@/components/PermissionBtn';

export default function SalesOrderList() {
  const actionRef = useRef<ActionType>();

  const columns: ProColumns[] = [
    { title: '订单号', dataIndex: 'orderNo', key: 'orderNo' },
    { title: '客户ID', dataIndex: 'customerId', key: 'customerId', search: false },
    { title: '订单日期', dataIndex: 'orderDate', key: 'orderDate', search: false, valueType: 'date' },
    { title: '总金额', dataIndex: 'totalAmount', key: 'totalAmount', search: false },
    {
      title: '状态', dataIndex: 'status', key: 'status',
      render: (_, record) => {
        const m: Record<number, { t: string; c: string }> = { 0: { t: '草稿', c: 'default' }, 1: { t: '待审', c: 'orange' }, 2: { t: '已审', c: 'blue' }, 3: { t: '发货中', c: 'cyan' }, 4: { t: '完成', c: 'green' }, 5: { t: '取消', c: 'red' } };
        const s = m[record.status] || { t: '未知', c: 'default' };
        return <Tag color={s.c}>{s.t}</Tag>;
      },
    },
    {
      title: '操作', key: 'action', search: false,
      render: (_, record) => (
        <>
          {record.status === 1 && <PermissionBtn permission="sales:order:audit" type="link" onClick={async () => { await auditOrder(record.id); message.success('审核成功'); actionRef.current?.reload(); }}>审核</PermissionBtn>}
          {(record.status === 0 || record.status === 1) && <PermissionBtn permission="sales:order:cancel" type="link" danger><Popconfirm title="确定取消?" onConfirm={async () => { await cancelOrder(record.id); message.success('已取消'); actionRef.current?.reload(); }}>取消</Popconfirm></PermissionBtn>}
          <PermissionBtn permission="sales:order:delete" type="link" danger><Popconfirm title="确定删除?" onConfirm={async () => { await deleteOrder(record.id); message.success('删除成功'); actionRef.current?.reload(); }}>删除</Popconfirm></PermissionBtn>
        </>
      ),
    },
  ];

  return (
    <ProTable columns={columns} request={async (params) => { const data = await getOrderList(params); return { data: data.records, total: data.total, success: true }; }}
      actionRef={actionRef} rowKey="id" search={{ labelWidth: 'auto' }}
      toolBarRender={() => [<PermissionBtn key="add" permission="sales:order:add" type="primary" icon={<PlusOutlined />}>新增订单</PermissionBtn>]} />
  );
}
