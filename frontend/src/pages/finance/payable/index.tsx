import { useRef } from 'react';
import { ProTable } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Tag } from 'antd';
import { getPayableList } from '@/services/finance';

export default function PayableList() {
  const actionRef = useRef<ActionType>();
  const columns: ProColumns[] = [
    { title: '供应商ID', dataIndex: 'supplierId', key: 'supplierId' },
    { title: '业务单号', dataIndex: 'bizNo', key: 'bizNo', search: false },
    { title: '金额', dataIndex: 'amount', key: 'amount', search: false },
    { title: '已付', dataIndex: 'paidAmount', key: 'paidAmount', search: false },
    { title: '余额', dataIndex: 'balance', key: 'balance', search: false },
    { title: '到期日', dataIndex: 'dueDate', key: 'dueDate', search: false, valueType: 'date' },
    { title: '状态', dataIndex: 'status', key: 'status',
      render: (_, r) => {
        const m: Record<number, { t: string; c: string }> = { 0: { t: '未付', c: 'red' }, 1: { t: '部分付', c: 'orange' }, 2: { t: '已付清', c: 'green' } };
        return <Tag color={m[r.status]?.c}>{m[r.status]?.t}</Tag>;
      },
    },
  ];

  return (
    <ProTable columns={columns} request={async (params) => { const data = await getPayableList(params); return { data: data.records, total: data.total, success: true }; }}
      actionRef={actionRef} rowKey="id" search={{ labelWidth: 'auto' }} />
  );
}
