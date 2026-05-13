import { useRef, useState } from 'react';
import { ProTable, ModalForm, ProFormText, ProFormSelect, ProFormDigit } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Button, App, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getMenuTree, addMenu, updateMenu, deleteMenu } from '@/services/system';

export default function MenuList() {
  const { message } = App.useApp();
  const actionRef = useRef<ActionType>(null!);
  const [modalOpen, setModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);
  const [tableKey, setTableKey] = useState(0);

  const columns: ProColumns[] = [
    { title: '名称', dataIndex: 'menuName', key: 'menuName', sorter: true },
    {
      title: '类型', dataIndex: 'menuType', key: 'menuType',
      valueEnum: { M: '目录', C: '菜单', B: '按钮' },
    },
    { title: '路径', dataIndex: 'path', key: 'path', search: false },
    { title: '权限标识', dataIndex: 'perms', key: 'perms', search: false },
    { title: '排序', dataIndex: 'sort', key: 'sort', search: false },
    {
      title: '操作', key: 'action', search: false,
      render: (_, record) => (
        <>
          <Button type="link" onClick={() => {
            setEditRecord(record);
            setModalOpen(true);
          }}>编辑</Button>
          <Popconfirm title="确定删除?" onConfirm={async () => {
            await deleteMenu(record.id);
            message.success('删除成功');
            setTableKey(k => k + 1);
          }}>
            <Button type="link" danger>删除</Button>
          </Popconfirm>
        </>
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
          const data = await getMenuTree(query);
          return { data, total: data?.length || 0, success: true };
        }}
        actionRef={actionRef}
        rowKey="id"
        search={false}
        pagination={false}
        toolBarRender={() => [
          <Button key="add" type="primary" icon={<PlusOutlined />} onClick={() => {
            setEditRecord(null);
            setModalOpen(true);
          }}>新增</Button>,
        ]}
      />
      <ModalForm
        key={editRecord?.id || 'add'}
        title={editRecord ? '编辑菜单' : '新增菜单'}
        open={modalOpen}
        onOpenChange={setModalOpen}
        initialValues={editRecord}
        modalProps={{ destroyOnClose: true }}
        onFinish={async (values) => {
          try {
            if (editRecord) {
              await updateMenu(editRecord.id, values);
            } else {
              await addMenu(values);
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
        <ProFormSelect name="menuType" label="类型" rules={[{ required: true }]} options={[
          { label: '目录', value: 'M' },
          { label: '菜单', value: 'C' },
          { label: '按钮', value: 'B' },
        ]} />
        <ProFormText name="menuName" label="名称" rules={[{ required: true }]} />
        <ProFormDigit name="parentId" label="父级ID" />
        <ProFormText name="path" label="路径" />
        <ProFormText name="component" label="组件" />
        <ProFormText name="icon" label="图标" />
        <ProFormText name="perms" label="权限标识" />
        <ProFormDigit name="sort" label="排序" />
      </ModalForm>
    </>
  );
}
