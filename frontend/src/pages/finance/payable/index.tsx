import { useRef } from 'react';
import { ProTable } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Tag } from 'antd';
import { getPayableList } from '@/services/finance';

const STATUS_MAP: Record<number, { text: string; color: string }> = {
  0: { text: '未付', color: 'red' },
  1: { text: '部分已付', color: 'orange' },
  2: { text: '已付清', color: 'green' },
};

export default function PayableList() {
  const actionRef = useRef<ActionType>();

  const columns: ProColumns[] = [
    { title: '供应商', dataIndex: 'supplierName', key: 'supplierName' },
    { title: '业务单号', dataIndex: 'bizNo', key: 'bizNo', search: false },
    {
      title: '应付金额', dataIndex: 'amount', key: 'amount',
      search: false, valueType: 'money', width: 130,
    },
    {
      title: '已付金额', dataIndex: 'paidAmount', key: 'paidAmount',
      search: false, valueType: 'money', width: 130,
    },
    {
      title: '余额', dataIndex: 'balance', key: 'balance',
      search: false, valueType: 'money', width: 130,
    },
    {
      title: '到期日', dataIndex: 'dueDate', key: 'dueDate',
      search: false, valueType: 'date', width: 120,
    },
    {
      title: '状态', dataIndex: 'status', key: 'status', width: 100,
      render: (_, r) => {
        const info = STATUS_MAP[r.status];
        return info ? <Tag color={info.color}>{info.text}</Tag> : <Tag>未知</Tag>;
      },
    },
    { title: '备注', dataIndex: 'remark', key: 'remark', search: false, ellipsis: true },
  ];

  return (
    <ProTable
      columns={columns}
      request={async (params) => {
        const data = await getPayableList(params);
        return { data: data.records, total: data.total, success: true };
      }}
      actionRef={actionRef}
      rowKey="id"
      search={{ labelWidth: 'auto' }}
    />
  );
}
