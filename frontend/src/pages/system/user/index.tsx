import { useRef, useState } from 'react';
import { ProTable, ModalForm, ProFormText, ProFormSelect } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Button, Tag, message, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getUserList, addUser, updateUser, deleteUser } from '@/services/system';
import PermissionBtn from '@/components/PermissionBtn';

export default function UserList() {
  const actionRef = useRef<ActionType>();
  const [modalOpen, setModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);

  const columns: ProColumns[] = [
    { title: '用户名', dataIndex: 'username', key: 'username' },
    { title: '姓名', dataIndex: 'realName', key: 'realName' },
    { title: '手机号', dataIndex: 'phone', key: 'phone', search: false },
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
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', search: false, valueType: 'dateTime' },
    {
      title: '操作', key: 'action', search: false, fixed: 'right',
      render: (_, record) => (
        <>
          <PermissionBtn permission="system:user:edit" type="link" onClick={() => {
            setEditRecord(record);
            setModalOpen(true);
          }}>编辑</PermissionBtn>
          <PermissionBtn permission="system:user:delete" type="link" danger>
            <Popconfirm title="确定删除?" onConfirm={async () => {
              await deleteUser(record.id);
              message.success('删除成功');
              actionRef.current?.reload();
            }}>
              删除
            </Popconfirm>
          </PermissionBtn>
        </>
      ),
    },
  ];

  return (
    <>
      <ProTable
        columns={columns}
        request={async (params) => {
          const data = await getUserList(params);
          return { data: data.records, total: data.total, success: true };
        }}
        actionRef={actionRef}
        rowKey="id"
        search={{ labelWidth: 'auto' }}
        toolBarRender={() => [
          <PermissionBtn key="add" permission="system:user:add" type="primary" icon={<PlusOutlined />} onClick={() => {
            setEditRecord(null);
            setModalOpen(true);
          }}>
            新增用户
          </PermissionBtn>,
        ]}
      />
      <ModalForm
        title={editRecord ? '编辑用户' : '新增用户'}
        open={modalOpen}
        onOpenChange={setModalOpen}
        initialValues={editRecord}
        onFinish={async (values) => {
          if (editRecord) {
            await updateUser(editRecord.id, values);
          } else {
            await addUser(values);
          }
          message.success(editRecord ? '修改成功' : '新增成功');
          actionRef.current?.reload();
          return true;
        }}
      >
        <ProFormText name="username" label="用户名" rules={[{ required: true }]} />
        <ProFormText name="password" label="密码" rules={editRecord ? [] : [{ required: true }]} />
        <ProFormText name="realName" label="姓名" />
        <ProFormText name="phone" label="手机号" />
        <ProFormText name="email" label="邮箱" />
        <ProFormSelect name="status" label="状态" options={[
          { label: '正常', value: 1 },
          { label: '禁用', value: 0 },
        ]} />
      </ModalForm>
    </>
  );
}
