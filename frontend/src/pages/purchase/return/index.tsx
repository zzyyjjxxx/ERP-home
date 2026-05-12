import { useRef } from 'react';
import { ProTable } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Tag, message } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getReturnList, completeReturn } from '@/services/purchase';
import PermissionBtn from '@/components/PermissionBtn';

export default function ReturnList() {
  const actionRef = useRef<ActionType>();

  const columns: ProColumns[] = [
    { title: '退货单号', dataIndex: 'returnNo', key: 'returnNo' },
    { title: '供应商ID', dataIndex: 'supplierId', key: 'supplierId', search: false },
    { title: '退货日期', dataIndex: 'returnDate', key: 'returnDate', search: false, valueType: 'date' },
    { title: '总金额', dataIndex: 'totalAmount', key: 'totalAmount', search: false },
    {
      title: '状态', dataIndex: 'status', key: 'status',
      render: (_, record) => <Tag color={record.status === 1 ? 'green' : 'orange'}>{record.status === 1 ? '完成' : '草稿'}</Tag>,
    },
    {
      title: '操作', key: 'action', search: false,
      render: (_, record) => (
        <>
          {record.status === 0 && <PermissionBtn permission="purchase:return:audit" type="link" onClick={async () => { await completeReturn(record.id); message.success('完成'); actionRef.current?.reload(); }}>完成</PermissionBtn>}
        </>
      ),
    },
  ];

  return (
    <ProTable columns={columns} request={async (params) => { const data = await getReturnList(params); return { data: data.records, total: data.total, success: true }; }}
      actionRef={actionRef} rowKey="id" search={{ labelWidth: 'auto' }}
      toolBarRender={() => [
        <PermissionBtn key="add" permission="purchase:return:add" type="primary" icon={<PlusOutlined />}>新增退货单</PermissionBtn>,
      ]} />
  );
}
