import { useRef, useState } from 'react';
import { ProTable, ModalForm, ProFormText } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Button, Tag, App, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getSupplierList, addSupplier, updateSupplier, deleteSupplier } from '@/services/purchase';
import PermissionBtn from '@/components/PermissionBtn';

export default function SupplierList() {
  const { message } = App.useApp();
  const actionRef = useRef<ActionType>();
  const [modalOpen, setModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);
  const [tableKey, setTableKey] = useState(0);

  const columns: ProColumns[] = [
    { title: '编码', dataIndex: 'code', key: 'code', sorter: true },
    { title: '名称', dataIndex: 'name', key: 'name', sorter: true },
    { title: '联系人', dataIndex: 'contactPerson', key: 'contactPerson', search: false },
    { title: '电话', dataIndex: 'phone', key: 'phone', search: false },
    { title: '邮箱', dataIndex: 'email', key: 'email', search: false },
    {
      title: '状态', dataIndex: 'status', key: 'status',
      valueEnum: { 0: { text: '禁用', status: 'Error' }, 1: { text: '正常', status: 'Success' } },
      render: (_, record) => (
        <Tag color={record.status === 1 ? 'green' : 'red'}>
          {record.status === 1 ? '正常' : '禁用'}
        </Tag>
      ),
    },
    {
      title: '操作', key: 'action', search: false,
      render: (_, record) => (
        <>
          <PermissionBtn permission="purchase:supplier:edit" type="link" onClick={() => { setEditRecord(record); setModalOpen(true); }}>编辑</PermissionBtn>
          <PermissionBtn permission="purchase:supplier:delete" type="link" danger>
            <Popconfirm title="确定删除?" onConfirm={async () => { await deleteSupplier(record.id); message.success('删除成功'); setTableKey(k => k + 1); }}>删除</Popconfirm>
          </PermissionBtn>
        </>
      ),
    },
  ];

  return (
    <>
      <ProTable key={tableKey} columns={columns} request={async (params) => { const { sorter, ...rest } = params; const query: any = { ...rest }; if (sorter?.field) { query.sortField = sorter.field; query.sortOrder = sorter.order === 'ascend' ? 'asc' : 'desc'; } const data = await getSupplierList(query); return { data: data.records, total: data.total, success: true }; }}
        actionRef={actionRef} rowKey="id" search={{ labelWidth: 'auto' }}
        toolBarRender={() => [
          <PermissionBtn key="add" permission="purchase:supplier:add" type="primary" icon={<PlusOutlined />} onClick={() => { setEditRecord(null); setModalOpen(true); }}>新增供应商</PermissionBtn>,
        ]} />
      <ModalForm key={editRecord?.id || 'add'} title={editRecord ? '编辑供应商' : '新增供应商'} open={modalOpen} onOpenChange={setModalOpen} initialValues={editRecord} modalProps={{ destroyOnClose: true }}
        onFinish={async (values) => { try { editRecord ? await updateSupplier(editRecord.id, values) : await addSupplier(values); message.success(editRecord ? '修改成功' : '新增成功'); setTableKey(k => k + 1); return true; } catch (err: any) { message.error(err?.message || '操作失败'); return false; } }}>
        <ProFormText name="code" label="编码" rules={[{ required: true }]} />
        <ProFormText name="name" label="名称" rules={[{ required: true }]} />
        <ProFormText name="contactPerson" label="联系人" />
        <ProFormText name="phone" label="电话" />
        <ProFormText name="email" label="邮箱" />
        <ProFormText name="address" label="地址" />
        <ProFormText name="bankName" label="开户行" />
        <ProFormText name="bankAccount" label="银行账号" />
        <ProFormText name="taxNo" label="税号" />
        <ProFormText name="remark" label="备注" />
      </ModalForm>
    </>
  );
}
