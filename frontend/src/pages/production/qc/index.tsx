import { useRef } from 'react';
import { ProTable } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Button, Tag, message, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getQcList, deleteQc } from '@/services/production';

export default function QcList() {
  const actionRef = useRef<ActionType>();

  const columns: ProColumns[] = [
    { title: '质检单号', dataIndex: 'qcNo', key: 'qcNo' },
    { title: '工单ID', dataIndex: 'workOrderId', key: 'workOrderId' },
    { title: '产品ID', dataIndex: 'productId', key: 'productId' },
    { title: '检验数', dataIndex: 'checkQty', key: 'checkQty', search: false },
    { title: '合格数', dataIndex: 'passQty', key: 'passQty', search: false },
    { title: '不合格数', dataIndex: 'failQty', key: 'failQty', search: false },
    { title: '结果', dataIndex: 'result', key: 'result',
      render: (_, r) => {
        const m: Record<string, { t: string; c: string }> = { PASS: { t: '合格', c: 'green' }, FAIL: { t: '不合格', c: 'red' }, PARTIAL: { t: '部分合格', c: 'orange' } };
        return <Tag color={m[r.result]?.c}>{m[r.result]?.t}</Tag>;
      },
    },
    { title: '操作', key: 'action', search: false,
      render: (_, record) => (
        <Popconfirm title="确定删除?" onConfirm={async () => { await deleteQc(record.id); message.success('删除成功'); actionRef.current?.reload(); }}>
          <Button type="link" danger>删除</Button>
        </Popconfirm>
      ),
    },
  ];

  return (
    <ProTable columns={columns} request={async (params) => { const data = await getQcList(params); return { data: data.records, total: data.total, success: true }; }}
      actionRef={actionRef} rowKey="id" search={{ labelWidth: 'auto' }}
      toolBarRender={() => [<Button key="add" type="primary" icon={<PlusOutlined />}>新增质检单</Button>]} />
  );
}
