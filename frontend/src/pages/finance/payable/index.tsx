import { useRef, useState } from 'react';
import { ProTable, ModalForm, ProFormText, ProFormDigit, ProFormDatePicker } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Tag, Space, App, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getPayableList, addPayable, updatePayable, deletePayable, auditPayment } from '@/services/finance';
import PermissionBtn from '@/components/PermissionBtn';

const STATUS_MAP: Record<number, { text: string; color: string }> = {
  0: { text: '未付', color: 'red' },
  1: { text: '部分已付', color: 'orange' },
  2: { text: '已付清', color: 'green' },
};

export default function PayableList() {
  const { message } = App.useApp();
  const actionRef = useRef<ActionType>(null!);
  const [modalOpen, setModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);
  const [tableKey, setTableKey] = useState(0);

  const columns: ProColumns[] = [
    { title: '供应商', dataIndex: 'supplierName', key: 'supplierName' },
    { title: '供应商ID', dataIndex: 'supplierId', key: 'supplierId', hideInTable: true },
    { title: '业务单号', dataIndex: 'bizNo', key: 'bizNo', search: false },
    { title: '业务类型', dataIndex: 'bizType', key: 'bizType', search: false },
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
    {
      title: '操作', key: 'action', search: false,
      render: (_, record) => (
        <Space>
          {(record.status === 0 || record.status === 1) && <PermissionBtn permission="finance:payment:audit" type="link" onClick={async () => { await auditPayment(record.id); message.success('付款成功'); setTableKey(k => k + 1); }}>付款</PermissionBtn>}
          <PermissionBtn permission="finance:payable:edit" type="link" onClick={() => { setEditRecord(record); setModalOpen(true); }}>编辑</PermissionBtn>
          <PermissionBtn permission="finance:payable:delete" type="link" danger>
            <Popconfirm title="确定删除?" onConfirm={async () => { await deletePayable(record.id); message.success('删除成功'); setTableKey(k => k + 1); }}>删除</Popconfirm>
          </PermissionBtn>
        </Space>
      ),
    },
  ];

  return (
    <>
      <ProTable
        key={tableKey}
        columns={columns}
        request={async (params) => {
          const { sorter, ...rest } = params;
          const query: any = { ...rest };
          if (sorter?.field) {
            query.sortField = sorter.field;
            query.sortOrder = sorter.order === 'ascend' ? 'asc' : 'desc';
          }
          const data = await getPayableList(query);
          return { data: data.records, total: data.total, success: true };
        }}
        actionRef={actionRef}
        rowKey="id"
        search={{ labelWidth: 'auto' }}
        toolBarRender={() => [
          <PermissionBtn key="add" permission="finance:payable:add" type="primary" icon={<PlusOutlined />} onClick={() => { setEditRecord(null); setModalOpen(true); }}>新增应付</PermissionBtn>,
        ]}
      />
      <ModalForm
        key={editRecord?.id || 'add'}
        title={editRecord ? '编辑应付' : '新增应付'}
        open={modalOpen}
        onOpenChange={setModalOpen}
        initialValues={editRecord}
        modalProps={{ destroyOnClose: true }}
        onFinish={async (values) => {
          try {
            editRecord ? await updatePayable(editRecord.id, values) : await addPayable(values);
            message.success(editRecord ? '修改成功' : '新增成功');
            setTableKey(k => k + 1);
            return true;
          } catch (err: any) {
            message.error(err?.message || '操作失败');
            return false;
          }
        }}
      >
        <ProFormDigit name="supplierId" label="供应商ID" rules={[{ required: true }]} />
        <ProFormText name="bizType" label="业务类型" />
        <ProFormText name="bizNo" label="业务单号" />
        <ProFormDigit name="amount" label="应付金额" fieldProps={{ precision: 2 }} rules={[{ required: true }]} />
        <ProFormDigit name="paidAmount" label="已付金额" fieldProps={{ precision: 2 }} initialValue={0} />
        <ProFormDigit name="balance" label="余额" fieldProps={{ precision: 2 }} />
        <ProFormDatePicker name="dueDate" label="到期日" />
      </ModalForm>
    </>
  );
}
