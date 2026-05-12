import { useRef, useState, useEffect } from 'react';
import { ProTable, ModalForm, ProFormSelect, ProFormDatePicker, ProFormText, ProFormTextArea } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Tag, message, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getReturnList, addReturn, updateReturn, deleteReturn, completeReturn, unauditReturn, getAllCustomers } from '@/services/sales';
import { getWarehouseList } from '@/services/inventory';
import PermissionBtn from '@/components/PermissionBtn';

export default function SalesReturnList() {
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
    { title: '退货单号', dataIndex: 'returnNo', key: 'returnNo' },
    {
      title: '客户', dataIndex: 'customerId', key: 'customerId', search: false,
      render: (_, record) => customerMap[record.customerId] || record.customerName || record.customerId,
    },
    { title: '退货日期', dataIndex: 'returnDate', key: 'returnDate', search: false, valueType: 'date' },
    { title: '总金额', dataIndex: 'totalAmount', key: 'totalAmount', search: false, valueType: 'money' },
    {
      title: '状态', dataIndex: 'status', key: 'status',
      render: (_, record) => {
        const statusMap: Record<number, { text: string; color: string }> = {
          0: { text: '草稿', color: 'orange' },
          1: { text: '完成', color: 'green' },
        };
        const s = statusMap[record.status] || { text: '未知', color: 'default' };
        return <Tag color={s.color}>{s.text}</Tag>;
      },
    },
    {
      title: '操作', key: 'action', search: false,
      render: (_, record) => (
        <>
          {record.status === 0 && (
            <>
              <PermissionBtn permission="sales:return:edit" type="link" onClick={() => { setEditRecord(record); setModalOpen(true); }}>编辑</PermissionBtn>
              <PermissionBtn permission="sales:return:audit" type="link" onClick={async () => { await completeReturn(record.id); message.success('完成'); actionRef.current?.reload(); }}>完成</PermissionBtn>
            </>
          )}
          {record.status === 1 && <PermissionBtn permission="sales:return:unaudit" type="link" onClick={async () => { await unauditReturn(record.id); message.success('反审核成功'); actionRef.current?.reload(); }}>反审核</PermissionBtn>}
          <PermissionBtn permission="sales:return:delete" type="link" danger>
            <Popconfirm title="确定删除?" onConfirm={async () => { await deleteReturn(record.id); message.success('删除成功'); actionRef.current?.reload(); }}>删除</Popconfirm>
          </PermissionBtn>
        </>
      ),
    },
  ];

  return (
    <>
      <ProTable columns={columns} request={async (params) => { const data = await getReturnList(params); return { data: data.records, total: data.total, success: true }; }}
        actionRef={actionRef} rowKey="id" search={{ labelWidth: 'auto' }}
        toolBarRender={() => [
          <PermissionBtn key="add" permission="sales:return:add" type="primary" icon={<PlusOutlined />} onClick={() => { setEditRecord(null); setModalOpen(true); }}>新增退货单</PermissionBtn>,
        ]} />
      <ModalForm
        title={editRecord ? '编辑退货单' : '新增退货单'}
        open={modalOpen}
        onOpenChange={setModalOpen}
        initialValues={editRecord}
        onFinish={async (values) => {
          if (editRecord) {
            await updateReturn(editRecord.id, values);
          } else {
            await addReturn(values);
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
          request={async () => {
            const data: any = await getWarehouseList();
            return (data || []).map((w: any) => ({ label: w.name, value: w.id }));
          }}
        />
        <ProFormText name="returnNo" label="退货单号" />
        <ProFormDatePicker name="returnDate" label="退货日期" />
        <ProFormTextArea name="reason" label="退货原因" />
      </ModalForm>
    </>
  );
}
