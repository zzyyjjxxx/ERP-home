import { useRef } from 'react';
import { ProTable } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Button, Tag, message, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getWorkOrderList, completeWorkOrder, deleteWorkOrder } from '@/services/production';

export default function WorkOrderList() {
  const actionRef = useRef<ActionType>();

  const columns: ProColumns[] = [
    { title: '工单号', dataIndex: 'orderNo', key: 'orderNo' },
    { title: '产品ID', dataIndex: 'productId', key: 'productId' },
    { title: '计划数量', dataIndex: 'planQty', key: 'planQty', search: false },
    { title: '实际数量', dataIndex: 'actualQty', key: 'actualQty', search: false },
    { title: '状态', dataIndex: 'status', key: 'status',
      render: (_, r) => {
        const m: Record<number, { t: string; c: string }> = { 0: { t: '待领料', c: 'default' }, 1: { t: '生产中', c: 'blue' }, 2: { t: '完成', c: 'green' }, 3: { t: '关闭', c: 'red' } };
        return <Tag color={m[r.status]?.c}>{m[r.status]?.t}</Tag>;
      },
    },
    { title: '操作', key: 'action', search: false,
      render: (_, record) => (
        <>
          {record.status === 1 && <Button type="link" onClick={async () => { await completeWorkOrder(record.id); message.success('完成'); actionRef.current?.reload(); }}>完成</Button>}
          <Popconfirm title="确定删除?" onConfirm={async () => { await deleteWorkOrder(record.id); message.success('删除成功'); actionRef.current?.reload(); }}>
            <Button type="link" danger>删除</Button>
          </Popconfirm>
        </>
      ),
    },
  ];

  return (
    <ProTable columns={columns} request={async (params) => { const data = await getWorkOrderList(params); return { data: data.records, total: data.total, success: true }; }}
      actionRef={actionRef} rowKey="id" search={{ labelWidth: 'auto' }}
      toolBarRender={() => [<Button key="add" type="primary" icon={<PlusOutlined />}>新增工单</Button>]} />
  );
}
