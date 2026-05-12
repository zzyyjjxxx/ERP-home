import { useRef, useState } from 'react';
import { ProTable, ModalForm, ProFormText } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { message, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getProductList, addProduct, updateProduct, deleteProduct } from '@/services/inventory';
import PermissionBtn from '@/components/PermissionBtn';

export default function ProductList() {
  const actionRef = useRef<ActionType>();
  const [modalOpen, setModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);

  const columns: ProColumns[] = [
    { title: '编码', dataIndex: 'code', key: 'code' },
    { title: '名称', dataIndex: 'name', key: 'name' },
    { title: '规格', dataIndex: 'spec', key: 'spec', search: false },
    { title: '型号', dataIndex: 'model', key: 'model', search: false },
    { title: '采购价', dataIndex: 'purchasePrice', key: 'purchasePrice', search: false },
    { title: '销售价', dataIndex: 'salePrice', key: 'salePrice', search: false },
    {
      title: '操作', key: 'action', search: false,
      render: (_, record) => (
        <>
          <PermissionBtn permission="inventory:product:edit" type="link" onClick={() => { setEditRecord(record); setModalOpen(true); }}>编辑</PermissionBtn>
          <PermissionBtn permission="inventory:product:delete" type="link" danger>
            <Popconfirm title="确定删除?" onConfirm={async () => { await deleteProduct(record.id); message.success('删除成功'); actionRef.current?.reload(); }}>删除</Popconfirm>
          </PermissionBtn>
        </>
      ),
    },
  ];

  return (
    <>
      <ProTable columns={columns} request={async (params) => { const data = await getProductList(params); return { data: data.records, total: data.total, success: true }; }}
        actionRef={actionRef} rowKey="id" search={{ labelWidth: 'auto' }}
        toolBarRender={() => [
          <PermissionBtn key="add" permission="inventory:product:add" type="primary" icon={<PlusOutlined />} onClick={() => { setEditRecord(null); setModalOpen(true); }}>新增商品</PermissionBtn>,
        ]} />
      <ModalForm title={editRecord ? '编辑商品' : '新增商品'} open={modalOpen} onOpenChange={setModalOpen} initialValues={editRecord}
        onFinish={async (values) => { editRecord ? await updateProduct(editRecord.id, values) : await addProduct(values); message.success(editRecord ? '修改成功' : '新增成功'); actionRef.current?.reload(); return true; }}>
        <ProFormText name="code" label="编码" rules={[{ required: true }]} />
        <ProFormText name="name" label="名称" rules={[{ required: true }]} />
        <ProFormText name="spec" label="规格" />
        <ProFormText name="model" label="型号" />
        <ProFormText name="purchasePrice" label="采购价" />
        <ProFormText name="salePrice" label="销售价" />
        <ProFormText name="minStock" label="最低库存" />
        <ProFormText name="maxStock" label="最高库存" />
        <ProFormText name="remark" label="备注" />
      </ModalForm>
    </>
  );
}
