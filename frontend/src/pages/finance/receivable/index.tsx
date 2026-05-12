import { useRef, useState } from 'react';
import { ProTable, ModalForm, ProFormText, ProFormDigit, ProFormSelect, ProFormDatePicker } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Tag, Space, message, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getReceivableList, addReceivable, updateReceivable, deleteReceivable, auditPayment } from '@/services/finance';
import PermissionBtn from '@/components/PermissionBtn';

const STATUS_MAP: Record<number, { text: string; color: string }> = {
  0: { text: '未收', color: 'red' },
  1: { text: '部分已收', color: 'orange' },
  2: { text: '已收清', color: 'green' },
};

export default function ReceivableList() {
  const actionRef = useRef<ActionType>(null!);
  const [modalOpen, setModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);

  const columns: ProColumns[] = [
    { title: '客户', dataIndex: 'customerName', key: 'customerName' },
    { title: '客户ID', dataIndex: 'customerId', key: 'customerId', hideInTable: true },
    { title: '业务单号', dataIndex: 'bizNo', key: 'bizNo', search: false },
    { title: '业务类型', dataIndex: 'bizType', key: 'bizType', search: false },
    {
      title: '应收金额', dataIndex: 'amount', key: 'amount',
      search: false, valueType: 'money', width: 130,
    },
    {
      title: '已收金额', dataIndex: 'receivedAmount', key: 'receivedAmount',
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
          {(record.status === 0 || record.status === 1) && <PermissionBtn permission="finance:payment:audit" type="link" onClick={async () => { await auditPayment(record.id); message.success('收款成功'); actionRef.current?.reload(); }}>收款</PermissionBtn>}
          <PermissionBtn permission="finance:receivable:edit" type="link" onClick={() => { setEditRecord(record); setModalOpen(true); }}>编辑</PermissionBtn>
          <PermissionBtn permission="finance:receivable:delete" type="link" danger>
            <Popconfirm title="确定删除?" onConfirm={async () => { await deleteReceivable(record.id); message.success('删除成功'); actionRef.current?.reload(); }}>删除</Popconfirm>
          </PermissionBtn>
        </Space>
      ),
    },
  ];

  return (
    <>
      <ProTable
        columns={columns}
        request={async (params) => {
          const data = await getReceivableList(params);
          return { data: data.records, total: data.total, success: true };
        }}
        actionRef={actionRef}
        rowKey="id"
        search={{ labelWidth: 'auto' }}
        toolBarRender={() => [
          <PermissionBtn key="add" permission="finance:receivable:add" type="primary" icon={<PlusOutlined />} onClick={() => { setEditRecord(null); setModalOpen(true); }}>新增应收</PermissionBtn>,
        ]}
      />
      <ModalForm
        title={editRecord ? '编辑应收' : '新增应收'}
        open={modalOpen}
        onOpenChange={setModalOpen}
        initialValues={editRecord}
        onFinish={async (values) => {
          editRecord ? await updateReceivable(editRecord.id, values) : await addReceivable(values);
          message.success(editRecord ? '修改成功' : '新增成功');
          actionRef.current?.reload();
          return true;
        }}
      >
        <ProFormDigit name="customerId" label="客户ID" rules={[{ required: true }]} />
        <ProFormText name="bizType" label="业务类型" />
        <ProFormText name="bizNo" label="业务单号" />
        <ProFormDigit name="amount" label="应收金额" fieldProps={{ precision: 2 }} rules={[{ required: true }]} />
        <ProFormDigit name="receivedAmount" label="已收金额" fieldProps={{ precision: 2 }} initialValue={0} />
        <ProFormDigit name="balance" label="余额" fieldProps={{ precision: 2 }} />
        <ProFormDatePicker name="dueDate" label="到期日" />
      </ModalForm>
    </>
  );
}
