import { useRef, useState, useEffect } from 'react';
import { ProTable, ModalForm, ProFormSelect, ProFormDatePicker, ProFormText } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Tag, App, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getReceiptList, addReceipt, updateReceipt, deleteReceipt, auditReceipt, unauditReceipt, getAllSuppliers } from '@/services/purchase';
import { getWarehouseList } from '@/services/inventory';
import PermissionBtn from '@/components/PermissionBtn';

export default function ReceiptList() {
  const { message } = App.useApp();
  const actionRef = useRef<ActionType>();
  const [modalOpen, setModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);
  const [tableKey, setTableKey] = useState(0);

  const [supplierMap, setSupplierMap] = useState<Record<number, string>>({});

  useEffect(() => {
    getAllSuppliers().then((data: any) => {
      const map: Record<number, string> = {};
      (data || []).forEach((s: any) => { map[s.id] = s.name; });
      setSupplierMap(map);
    });
  }, []);

  const columns: ProColumns[] = [
    { title: '收货单号', dataIndex: 'receiptNo', key: 'receiptNo', sorter: true },
    {
      title: '供应商', dataIndex: 'supplierId', key: 'supplierId', search: false,
      render: (_, record) => supplierMap[record.supplierId] || record.supplierName || record.supplierId,
    },
    { title: '收货日期', dataIndex: 'receiptDate', key: 'receiptDate', search: false, valueType: 'date' },
    { title: '总数量', dataIndex: 'totalQty', key: 'totalQty', search: false },
    {
      title: '状态', dataIndex: 'status', key: 'status',
      valueEnum: { 0: '待审', 1: '完成', 2: '取消' },
      render: (_, record) => {
        const statusMap: Record<number, { text: string; color: string }> = {
          0: { text: '待审', color: 'orange' },
          1: { text: '完成', color: 'green' },
          2: { text: '取消', color: 'red' },
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
              <PermissionBtn permission="purchase:receipt:edit" type="link" onClick={() => { setEditRecord(record); setModalOpen(true); }}>编辑</PermissionBtn>
              <PermissionBtn permission="purchase:receipt:audit" type="link" onClick={async () => { await auditReceipt(record.id); message.success('审核成功'); setTableKey(k => k + 1); }}>审核</PermissionBtn>
            </>
          )}
          {record.status === 1 && <PermissionBtn permission="purchase:receipt:unaudit" type="link" onClick={async () => { await unauditReceipt(record.id); message.success('反审核成功'); setTableKey(k => k + 1); }}>反审核</PermissionBtn>}
          <PermissionBtn permission="purchase:receipt:delete" type="link" danger>
            <Popconfirm title="确定删除?" onConfirm={async () => { await deleteReceipt(record.id); message.success('删除成功'); setTableKey(k => k + 1); }}>删除</Popconfirm>
          </PermissionBtn>
        </>
      ),
    },
  ];

  return (
    <>
      <ProTable key={tableKey} columns={columns} request={async (params) => { const { sorter, ...rest } = params; const query: any = { ...rest }; if (sorter?.field) { query.sortField = sorter.field; query.sortOrder = sorter.order === 'ascend' ? 'asc' : 'desc'; } const data = await getReceiptList(query); return { data: data.records, total: data.total, success: true }; }}
        actionRef={actionRef} rowKey="id" search={{ labelWidth: 'auto' }}
        toolBarRender={() => [
          <PermissionBtn key="add" permission="purchase:receipt:add" type="primary" icon={<PlusOutlined />} onClick={() => { setEditRecord(null); setModalOpen(true); }}>新增收货单</PermissionBtn>,
        ]} />
      <ModalForm
        key={editRecord?.id || 'add'}
        title={editRecord ? '编辑收货单' : '新增收货单'}
        open={modalOpen}
        onOpenChange={setModalOpen}
        initialValues={editRecord}
        modalProps={{ destroyOnClose: true }}
        onFinish={async (values) => {
          try {
            if (editRecord) {
              await updateReceipt(editRecord.id, values);
            } else {
              await addReceipt(values);
            }
            message.success(editRecord ? '修改成功' : '新增成功');
            setTableKey(k => k + 1);
            return true;
          } catch (err: any) {
            message.error(err?.message || '操作失败');
            return false;
          }
        }}
      >
        <ProFormSelect
          name="supplierId"
          label="供应商"
          rules={[{ required: true, message: '请选择供应商' }]}
          request={async () => {
            const data: any = await getAllSuppliers();
            return (data || []).map((s: any) => ({ label: s.name, value: s.id }));
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
        <ProFormText name="receiptNo" label="收货单号" />
        <ProFormDatePicker name="receiptDate" label="收货日期" />
      </ModalForm>
    </>
  );
}
