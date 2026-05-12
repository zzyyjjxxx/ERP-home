import { useRef, useState } from 'react';
import { ProTable, ModalForm, ProFormText } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Tag, message, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getCustomerList, addCustomer, updateCustomer, deleteCustomer } from '@/services/sales';
import PermissionBtn from '@/components/PermissionBtn';

export default function CustomerList() {
  const actionRef = useRef<ActionType>();
  const [modalOpen, setModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);

  const columns: ProColumns[] = [
    { title: '编码', dataIndex: 'code', key: 'code' },
    { title: '名称', dataIndex: 'name', key: 'name' },
    { title: '联系人', dataIndex: 'contactPerson', key: 'contactPerson', search: false },
    { title: '电话', dataIndex: 'phone', key: 'phone', search: false },
    { title: '邮箱', dataIndex: 'email', key: 'email', search: false },
    {
      title: '状态', dataIndex: 'status', key: 'status',
      render: (_, record) => <Tag color={record.status === 1 ? 'green' : 'red'}>{record.status === 1 ? '正常' : '禁用'}</Tag>,
    },
    {
      title: '操作', key: 'action', search: false,
      render: (_, record) => (
        <>
          <PermissionBtn permission="sales:customer:edit" type="link" onClick={() => { setEditRecord(record); setModalOpen(true); }}>编辑</PermissionBtn>
          <PermissionBtn permission="sales:customer:delete" type="link" danger>
            <Popconfirm title="确定删除?" onConfirm={async () => { await deleteCustomer(record.id); message.success('删除成功'); actionRef.current?.reload(); }}>删除</Popconfirm>
          </PermissionBtn>
        </>
      ),
    },
  ];

  return (
    <>
      <ProTable columns={columns} request={async (params) => { const data = await getCustomerList(params); return { data: data.records, total: data.total, success: true }; }}
        actionRef={actionRef} rowKey="id" search={{ labelWidth: 'auto' }}
        toolBarRender={() => [<PermissionBtn key="add" permission="sales:customer:add" type="primary" icon={<PlusOutlined />} onClick={() => { setEditRecord(null); setModalOpen(true); }}>新增客户</PermissionBtn>]} />
      <ModalForm title={editRecord ? '编辑客户' : '新增客户'} open={modalOpen} onOpenChange={setModalOpen} initialValues={editRecord}
        onFinish={async (values) => { editRecord ? await updateCustomer(editRecord.id, values) : await addCustomer(values); message.success(editRecord ? '修改成功' : '新增成功'); actionRef.current?.reload(); return true; }}>
        <ProFormText name="code" label="编码" rules={[{ required: true }]} />
        <ProFormText name="name" label="名称" rules={[{ required: true }]} />
        <ProFormText name="contactPerson" label="联系人" />
        <ProFormText name="phone" label="电话" />
        <ProFormText name="email" label="邮箱" />
        <ProFormText name="address" label="地址" />
        <ProFormText name="remark" label="备注" />
      </ModalForm>
    </>
  );
}
