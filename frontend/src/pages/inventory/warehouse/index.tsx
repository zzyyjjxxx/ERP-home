import { useRef, useState } from 'react';
import { ProTable, ModalForm, ProFormText } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { App, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getWarehouseList, addWarehouse, updateWarehouse, deleteWarehouse } from '@/services/inventory';
import PermissionBtn from '@/components/PermissionBtn';

export default function WarehouseList() {
  const { message } = App.useApp();
  const actionRef = useRef<ActionType>();
  const [modalOpen, setModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);
  const [tableKey, setTableKey] = useState(0);

  const columns: ProColumns[] = [
    { title: '编码', dataIndex: 'code', key: 'code', sorter: true },
    { title: '名称', dataIndex: 'name', key: 'name', sorter: true },
    { title: '地址', dataIndex: 'address', key: 'address', search: false },
    { title: '保管员', dataIndex: 'keeper', key: 'keeper', search: false },
    { title: '电话', dataIndex: 'phone', key: 'phone', search: false },
    {
      title: '操作', key: 'action', search: false,
      render: (_, record) => (
        <>
          <PermissionBtn permission="inventory:warehouse:edit" type="link" onClick={() => { setEditRecord(record); setModalOpen(true); }}>编辑</PermissionBtn>
          <PermissionBtn permission="inventory:warehouse:delete" type="link" danger>
            <Popconfirm title="确定删除?" onConfirm={async () => { await deleteWarehouse(record.id); message.success('删除成功'); setTableKey(k => k + 1); }}>删除</Popconfirm>
          </PermissionBtn>
        </>
      ),
    },
  ];

  return (
    <>
      <ProTable key={tableKey} columns={columns} request={async (params) => { const { sorter, ...rest } = params; const query: any = { ...rest }; if (sorter?.field) { query.sortField = sorter.field; query.sortOrder = sorter.order === 'ascend' ? 'asc' : 'desc'; } const data = await getWarehouseList(query); return { data: data.records || data, total: data.total || data?.length || 0, success: true }; }}
        actionRef={actionRef} rowKey="id" search={false}
        toolBarRender={() => [
          <PermissionBtn key="add" permission="inventory:warehouse:add" type="primary" icon={<PlusOutlined />} onClick={() => { setEditRecord(null); setModalOpen(true); }}>新增仓库</PermissionBtn>,
        ]} />
      <ModalForm key={editRecord?.id || 'add'} title={editRecord ? '编辑仓库' : '新增仓库'} open={modalOpen} onOpenChange={setModalOpen} initialValues={editRecord} modalProps={{ destroyOnClose: true }}
        onFinish={async (values) => { try { editRecord ? await updateWarehouse(editRecord.id, values) : await addWarehouse(values); message.success(editRecord ? '修改成功' : '新增成功'); setTableKey(k => k + 1); return true; } catch (err: any) { message.error(err?.message || '操作失败'); return false; } }}>
        <ProFormText name="code" label="编码" rules={[{ required: true }]} />
        <ProFormText name="name" label="名称" rules={[{ required: true }]} />
        <ProFormText name="address" label="地址" />
        <ProFormText name="keeper" label="保管员" />
        <ProFormText name="phone" label="电话" />
      </ModalForm>
    </>
  );
}
