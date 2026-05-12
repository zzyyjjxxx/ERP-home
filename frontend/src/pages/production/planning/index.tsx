import { useRef } from 'react';
import { ProTable } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Button, Tag, message, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getPlanningList, deletePlanning } from '@/services/production';

export default function PlanningList() {
  const actionRef = useRef<ActionType>();

  const columns: ProColumns[] = [
    { title: '计划号', dataIndex: 'planNo', key: 'planNo' },
    { title: '产品ID', dataIndex: 'productId', key: 'productId' },
    { title: '数量', dataIndex: 'quantity', key: 'quantity', search: false },
    { title: '来源', dataIndex: 'sourceType', key: 'sourceType', search: false },
    { title: '状态', dataIndex: 'status', key: 'status',
      render: (_, r) => <Tag>{['草稿', '已审', '执行中', '完成'][r.status] || '未知'}</Tag>,
    },
    { title: '操作', key: 'action', search: false,
      render: (_, record) => (
        <Popconfirm title="确定删除?" onConfirm={async () => { await deletePlanning(record.id); message.success('删除成功'); actionRef.current?.reload(); }}>
          <Button type="link" danger>删除</Button>
        </Popconfirm>
      ),
    },
  ];

  return (
    <ProTable columns={columns} request={async (params) => { const data = await getPlanningList(params); return { data: data.records, total: data.total, success: true }; }}
      actionRef={actionRef} rowKey="id" search={{ labelWidth: 'auto' }}
      toolBarRender={() => [<Button key="add" type="primary" icon={<PlusOutlined />}>新增计划</Button>]} />
  );
}
