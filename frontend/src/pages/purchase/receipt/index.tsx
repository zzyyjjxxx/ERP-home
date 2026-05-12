import { useRef } from 'react';
import { ProTable } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Tag, message } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getReceiptList, auditReceipt } from '@/services/purchase';
import PermissionBtn from '@/components/PermissionBtn';

export default function ReceiptList() {
  const actionRef = useRef<ActionType>();

  const columns: ProColumns[] = [
    { title: '收货单号', dataIndex: 'receiptNo', key: 'receiptNo' },
    { title: '供应商ID', dataIndex: 'supplierId', key: 'supplierId', search: false },
    { title: '收货日期', dataIndex: 'receiptDate', key: 'receiptDate', search: false, valueType: 'date' },
    { title: '总数量', dataIndex: 'totalQty', key: 'totalQty', search: false },
    {
      title: '状态', dataIndex: 'status', key: 'status',
      valueEnum: { 0: '待审', 1: '完成', 2: '取消' },
      render: (_, record) => <Tag color={record.status === 1 ? 'green' : 'orange'}>{record.status === 1 ? '完成' : record.status === 2 ? '取消' : '待审'}</Tag>,
    },
    {
      title: '操作', key: 'action', search: false,
      render: (_, record) => (
        <>
          {record.status === 0 && <PermissionBtn permission="purchase:receipt:audit" type="link" onClick={async () => { await auditReceipt(record.id); message.success('审核成功'); actionRef.current?.reload(); }}>审核</PermissionBtn>}
        </>
      ),
    },
  ];

  return (
    <ProTable columns={columns} request={async (params) => { const data = await getReceiptList(params); return { data: data.records, total: data.total, success: true }; }}
      actionRef={actionRef} rowKey="id" search={{ labelWidth: 'auto' }}
      toolBarRender={() => [
        <PermissionBtn key="add" permission="purchase:receipt:add" type="primary" icon={<PlusOutlined />}>新增收货单</PermissionBtn>,
      ]} />
  );
}
