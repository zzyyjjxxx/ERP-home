import { useRef, useState } from 'react';
import { ProTable, ModalForm, ProFormText, ProFormDigit } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { App, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getCategoryList, addCategory, updateCategory, deleteCategory } from '@/services/inventory';
import PermissionBtn from '@/components/PermissionBtn';

export default function CategoryList() {
  const { message } = App.useApp();
  const actionRef = useRef<ActionType>(null!);
  const [modalOpen, setModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);
  const [tableKey, setTableKey] = useState(0);

  const columns: ProColumns[] = [
    { title: '编码', dataIndex: 'code', key: 'code', sorter: true },
    { title: '名称', dataIndex: 'name', key: 'name', sorter: true },
    { title: '排序', dataIndex: 'sort', key: 'sort', search: false },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', search: false, valueType: 'dateTime', sorter: true, defaultSortOrder: 'descend' as const },
    {
      title: '操作', key: 'action', search: false, fixed: 'right',
      render: (_, record) => (
        <>
          <PermissionBtn permission="inventory:category:edit" type="link" onClick={() => {
            setEditRecord(record);
            setModalOpen(true);
          }}>编辑</PermissionBtn>
          <PermissionBtn permission="inventory:category:delete" type="link" danger>
            <Popconfirm title="确定删除?" onConfirm={async () => {
              await deleteCategory(record.id);
              message.success('删除成功');
              setTableKey(k => k + 1);
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
        key={tableKey}
        columns={columns}
        request={async (params) => {
          const { current, pageSize, sorter, ...search } = params;
          const keyword = search.name || search.code || undefined;
          const query: any = { pageNum: current, pageSize, keyword };
          if (sorter?.field) {
            query.sortField = sorter.field;
            query.sortOrder = sorter.order === 'ascend' ? 'asc' : 'desc';
          }
          const data = await getCategoryList(query);
          return { data: data.records || data, total: data.total || 0, success: true };
        }}
        actionRef={actionRef}
        rowKey="id"
        search={{ labelWidth: 'auto' }}
        toolBarRender={() => [
          <PermissionBtn key="add" permission="inventory:category:add" type="primary" icon={<PlusOutlined />} onClick={() => {
            setEditRecord(null);
            setModalOpen(true);
          }}>
            新增分类
          </PermissionBtn>,
        ]}
      />
      <ModalForm
        key={editRecord?.id || 'add'}
        title={editRecord ? '编辑分类' : '新增分类'}
        open={modalOpen}
        onOpenChange={setModalOpen}
        initialValues={editRecord}
        modalProps={{ destroyOnClose: true }}
        onFinish={async (values) => {
          try {
            if (editRecord) {
              await updateCategory(editRecord.id, values);
            } else {
              await addCategory(values);
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
        <ProFormText name="code" label="编码" rules={[{ required: true }]} />
        <ProFormText name="name" label="名称" rules={[{ required: true }]} />
        <ProFormDigit name="sort" label="排序" initialValue={0} />
      </ModalForm>
    </>
  );
}
