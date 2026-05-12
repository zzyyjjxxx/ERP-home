import { useRef, useState, useEffect } from 'react';
import { ProTable, ModalForm, ProFormSelect, ProFormDatePicker, ProFormText } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Tag, message, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getDeliveryList, addDelivery, updateDelivery, deleteDelivery, auditDelivery, getAllCustomers } from '@/services/sales';
import { getWarehouseList } from '@/services/inventory';
import PermissionBtn from '@/components/PermissionBtn';

export default function DeliveryList() {
  const actionRef = useRef<ActionType>();
  const [modalOpen, setModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);
  const [customerMap, setCustomerMap] = useState<Record<number, string>>({});

  useEffect(() => {
    getAllCustomers().then((data: any) => {
      const map: Record<number, string> = {};
      (data || []).forEach((c: any) => { map[c.id] = c.name; });
      setCustomerMap(map);
    });
  }, []);

  const columns: ProColumns[] = [
    { title: '发货单号', dataIndex: 'deliveryNo', key: 'deliveryNo' },
    {
      title: '客户', dataIndex: 'customerId', key: 'customerId', search: false,
      render: (_, record) => customerMap[record.customerId] || record.customerName || record.customerId,
    },
    { title: '发货日期', dataIndex: 'deliveryDate', key: 'deliveryDate', search: false, valueType: 'date' },
    { title: '总数量', dataIndex: 'totalQty', key: 'totalQty', search: false },
    {
      title: '状态', dataIndex: 'status', key: 'status',
      render: (_, r) => {
        const statusMap: Record<number, { text: string; color: string }> = {
          0: { text: '待审', color: 'orange' },
          1: { text: '完成', color: 'green' },
        };
        const s = statusMap[r.status] || { text: '未知', color: 'default' };
        return <Tag color={s.color}>{s.text}</Tag>;
      },
    },
    {
      title: '操作', key: 'action', search: false,
      render: (_, record) => (
        <>
          {record.status === 0 && (
            <>
              <PermissionBtn permission="sales:delivery:edit" type="link" onClick={() => { setEditRecord(record); setModalOpen(true); }}>编辑</PermissionBtn>
              <PermissionBtn permission="sales:delivery:audit" type="link" onClick={async () => { await auditDelivery(record.id); message.success('审核成功'); actionRef.current?.reload(); }}>审核</PermissionBtn>
            </>
          )}
          <PermissionBtn permission="sales:delivery:delete" type="link" danger>
            <Popconfirm title="确定删除?" onConfirm={async () => { await deleteDelivery(record.id); message.success('删除成功'); actionRef.current?.reload(); }}>删除</Popconfirm>
          </PermissionBtn>
        </>
      ),
    },
  ];

  return (
    <>
      <ProTable columns={columns} request={async (params) => { const data = await getDeliveryList(params); return { data: data.records, total: data.total, success: true }; }}
        actionRef={actionRef} rowKey="id" search={{ labelWidth: 'auto' }}
        toolBarRender={() => [
          <PermissionBtn key="add" permission="sales:delivery:add" type="primary" icon={<PlusOutlined />} onClick={() => { setEditRecord(null); setModalOpen(true); }}>新增发货单</PermissionBtn>,
        ]} />
      <ModalForm
        title={editRecord ? '编辑发货单' : '新增发货单'}
        open={modalOpen}
        onOpenChange={setModalOpen}
        initialValues={editRecord}
        onFinish={async (values) => {
          if (editRecord) {
            await updateDelivery(editRecord.id, values);
          } else {
            await addDelivery(values);
          }
          message.success(editRecord ? '修改成功' : '新增成功');
          actionRef.current?.reload();
          return true;
        }}
      >
        <ProFormSelect
          name="customerId"
          label="客户"
          rules={[{ required: true, message: '请选择客户' }]}
          request={async () => {
            const data: any = await getAllCustomers();
            return (data || []).map((c: any) => ({ label: c.name, value: c.id }));
          }}
        />
        <ProFormSelect
          name="warehouseId"
          label="仓库"
          rules={[{ required: true, message: '请选择仓库' }]}
          request={async () => {
            const data: any = await getWarehouseList();
            return (data || []).map((w: any) => ({ label: w.name, value: w.id }));
          }}
        />
        <ProFormText name="deliveryNo" label="发货单号" />
        <ProFormDatePicker name="deliveryDate" label="发货日期" />
      </ModalForm>
    </>
  );
}
