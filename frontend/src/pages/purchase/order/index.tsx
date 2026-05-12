import { useRef } from 'react';
import { ProTable, ModalForm, ProFormText, ProFormSelect, ProFormDatePicker } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Button, Tag, message, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getOrderList, auditOrder, cancelOrder, deleteOrder } from '@/services/purchase';
import PermissionBtn from '@/components/PermissionBtn';

export default function OrderList() {
  const actionRef = useRef<ActionType>();

  const columns: ProColumns[] = [
    { title: '订单号', dataIndex: 'orderNo', key: 'orderNo' },
    { title: '供应商ID', dataIndex: 'supplierId', key: 'supplierId', search: false },
    { title: '订单日期', dataIndex: 'orderDate', key: 'orderDate', search: false, valueType: 'date' },
    { title: '总金额', dataIndex: 'totalAmount', key: 'totalAmount', search: false },
    {
      title: '状态', dataIndex: 'status', key: 'status',
      valueEnum: { 0: '草稿', 1: '待审', 2: '已审', 3: '收货中', 4: '完成', 5: '取消' },
      render: (_, record) => {
        const statusMap: Record<number, { text: string; color: string }> = { 0: { text: '草稿', color: 'default' }, 1: { text: '待审', color: 'orange' }, 2: { text: '已审', color: 'blue' }, 3: { text: '收货中', color: 'cyan' }, 4: { text: '完成', color: 'green' }, 5: { text: '取消', color: 'red' } };
        const s = statusMap[record.status] || { text: '未知', color: 'default' };
        return <Tag color={s.color}>{s.text}</Tag>;
      },
    },
    {
      title: '操作', key: 'action', search: false,
      render: (_, record) => (
        <>
          {record.status === 1 && <PermissionBtn permission="purchase:order:audit" type="link" onClick={async () => { await auditOrder(record.id); message.success('审核成功'); actionRef.current?.reload(); }}>审核</PermissionBtn>}
          {(record.status === 0 || record.status === 1) && <PermissionBtn permission="purchase:order:cancel" type="link" danger><Popconfirm title="确定取消?" onConfirm={async () => { await cancelOrder(record.id); message.success('已取消'); actionRef.current?.reload(); }}>取消</Popconfirm></PermissionBtn>}
          <PermissionBtn permission="purchase:order:delete" type="link" danger><Popconfirm title="确定删除?" onConfirm={async () => { await deleteOrder(record.id); message.success('删除成功'); actionRef.current?.reload(); }}>删除</Popconfirm></PermissionBtn>
        </>
      ),
    },
  ];

  return (
    <ProTable columns={columns} request={async (params) => { const data = await getOrderList(params); return { data: data.records, total: data.total, success: true }; }}
      actionRef={actionRef} rowKey="id" search={{ labelWidth: 'auto' }}
      toolBarRender={() => [
        <PermissionBtn key="add" permission="purchase:order:add" type="primary" icon={<PlusOutlined />}>新增订单</PermissionBtn>,
      ]} />
  );
}
