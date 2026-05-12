import { useRef } from 'react';
import { ProTable } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Tag } from 'antd';
import { getReceivableList } from '@/services/finance';

export default function ReceivableList() {
  const actionRef = useRef<ActionType>();
  const columns: ProColumns[] = [
    { title: '客户ID', dataIndex: 'customerId', key: 'customerId' },
    { title: '业务单号', dataIndex: 'bizNo', key: 'bizNo', search: false },
    { title: '金额', dataIndex: 'amount', key: 'amount', search: false },
    { title: '已收', dataIndex: 'receivedAmount', key: 'receivedAmount', search: false },
    { title: '余额', dataIndex: 'balance', key: 'balance', search: false },
    { title: '到期日', dataIndex: 'dueDate', key: 'dueDate', search: false, valueType: 'date' },
    { title: '状态', dataIndex: 'status', key: 'status',
      render: (_, r) => {
        const m: Record<number, { t: string; c: string }> = { 0: { t: '未收', c: 'red' }, 1: { t: '部分收', c: 'orange' }, 2: { t: '已收清', c: 'green' } };
        return <Tag color={m[r.status]?.c}>{m[r.status]?.t}</Tag>;
      },
    },
  ];

  return (
    <ProTable columns={columns} request={async (params) => { const data = await getReceivableList(params); return { data: data.records, total: data.total, success: true }; }}
      actionRef={actionRef} rowKey="id" search={{ labelWidth: 'auto' }} />
  );
}
