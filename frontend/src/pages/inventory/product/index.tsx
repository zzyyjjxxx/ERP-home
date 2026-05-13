import { useRef, useState, useEffect } from 'react';
import { ProTable, ModalForm, ProFormText, ProFormSelect, ProFormDigit } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Tag, App, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getProductList, addProduct, updateProduct, deleteProduct, getCategoryTree, getUnitList } from '@/services/inventory';
import PermissionBtn from '@/components/PermissionBtn';

export default function ProductList() {
  const { message } = App.useApp();
  const actionRef = useRef<ActionType>();
  const [modalOpen, setModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);
  const [tableKey, setTableKey] = useState(0);

  const [categoryMap, setCategoryMap] = useState<Record<number, string>>({});
  const [unitMap, setUnitMap] = useState<Record<number, string>>({});

  useEffect(() => {
    getCategoryTree().then((data: any) => {
      const map: Record<number, string> = {};
      const list = Array.isArray(data) ? data : (data?.records || []);
      list.forEach((c: any) => { map[c.id] = c.name; });
      setCategoryMap(map);
    });
    getUnitList().then((data: any) => {
      const map: Record<number, string> = {};
      const list = Array.isArray(data) ? data : (data?.records || []);
      list.forEach((u: any) => { map[u.id] = u.name; });
      setUnitMap(map);
    });
  }, []);

  const columns: ProColumns[] = [
    { title: '编码', dataIndex: 'code', key: 'code', sorter: true },
    { title: '名称', dataIndex: 'name', key: 'name', sorter: true },
    { title: '分类', dataIndex: 'categoryId', key: 'categoryId', search: false, render: (_, record) => categoryMap[record.categoryId] || record.categoryName || record.categoryId },
    { title: '单位', dataIndex: 'unitId', key: 'unitId', search: false, render: (_, record) => unitMap[record.unitId] || record.unitName || record.unitId },
    { title: '规格', dataIndex: 'spec', key: 'spec', search: false },
    { title: '型号', dataIndex: 'model', key: 'model', search: false },
    { title: '采购价', dataIndex: 'purchasePrice', key: 'purchasePrice', search: false, valueType: 'money' },
    { title: '销售价', dataIndex: 'salePrice', key: 'salePrice', search: false, valueType: 'money' },
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
          <PermissionBtn permission="inventory:product:edit" type="link" onClick={() => { setEditRecord(record); setModalOpen(true); }}>编辑</PermissionBtn>
          <PermissionBtn permission="inventory:product:delete" type="link" danger>
            <Popconfirm title="确定删除?" onConfirm={async () => { await deleteProduct(record.id); message.success('删除成功'); setTableKey(k => k + 1); }}>删除</Popconfirm>
          </PermissionBtn>
        </>
      ),
    },
  ];

  return (
    <>
      <ProTable key={tableKey} columns={columns} request={async (params) => { const { sorter, ...rest } = params; const query: any = { ...rest }; if (sorter?.field) { query.sortField = sorter.field; query.sortOrder = sorter.order === 'ascend' ? 'asc' : 'desc'; } const data = await getProductList(query); return { data: data.records, total: data.total, success: true }; }}
        actionRef={actionRef} rowKey="id" search={{ labelWidth: 'auto' }}
        toolBarRender={() => [
          <PermissionBtn key="add" permission="inventory:product:add" type="primary" icon={<PlusOutlined />} onClick={() => { setEditRecord(null); setModalOpen(true); }}>新增商品</PermissionBtn>,
        ]} />
      <ModalForm key={editRecord?.id || 'add'} title={editRecord ? '编辑商品' : '新增商品'} open={modalOpen} onOpenChange={setModalOpen} initialValues={editRecord} modalProps={{ destroyOnClose: true }}
        onFinish={async (values) => { try { editRecord ? await updateProduct(editRecord.id, values) : await addProduct(values); message.success(editRecord ? '修改成功' : '新增成功'); setTableKey(k => k + 1); return true; } catch (err: any) { message.error(err?.message || '操作失败'); return false; } }}>
        <ProFormText name="code" label="编码" rules={[{ required: true }]} />
        <ProFormText name="name" label="名称" rules={[{ required: true }]} />
        <ProFormSelect
          name="categoryId"
          label="分类"
          rules={[{ required: true, message: '请选择分类' }]}
          request={async () => {
            const data: any = await getCategoryTree();
            const list = Array.isArray(data) ? data : (data?.records || []);
            return list.map((c: any) => ({ label: c.name, value: c.id })).sort((a: any, b: any) => a.label.localeCompare(b.label));
          }}
        />
        <ProFormSelect
          name="unitId"
          label="单位"
          rules={[{ required: true, message: '请选择单位' }]}
          request={async () => {
            const data: any = await getUnitList();
            const list = Array.isArray(data) ? data : (data?.records || []);
            return list.map((u: any) => ({ label: u.name, value: u.id })).sort((a: any, b: any) => a.label.localeCompare(b.label));
          }}
        />
        <ProFormText name="spec" label="规格" />
        <ProFormText name="model" label="型号" />
        <ProFormDigit name="purchasePrice" label="采购价" min={0} fieldProps={{ precision: 2 }} />
        <ProFormDigit name="salePrice" label="销售价" min={0} fieldProps={{ precision: 2 }} />
        <ProFormDigit name="minStock" label="最低库存" min={0} />
        <ProFormDigit name="maxStock" label="最高库存" min={0} />
        <ProFormText name="remark" label="备注" />
      </ModalForm>
    </>
  );
}
