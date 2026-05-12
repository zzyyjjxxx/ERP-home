import { useRef, useState } from 'react';
import { ProTable, ModalForm, ProFormText } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Button, message, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getBomList, addBom, updateBom, deleteBom } from '@/services/production';

export default function BomList() {
  const actionRef = useRef<ActionType>();
  const [modalOpen, setModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);

  const columns: ProColumns[] = [
    { title: '成品ID', dataIndex: 'productId', key: 'productId' },
    { title: '版本', dataIndex: 'version', key: 'version', search: false },
    { title: '状态', dataIndex: 'status', key: 'status', search: false },
    { title: '操作', key: 'action', search: false,
      render: (_, record) => (
        <>
          <Button type="link" onClick={() => { setEditRecord(record); setModalOpen(true); }}>编辑</Button>
          <Popconfirm title="确定删除?" onConfirm={async () => { await deleteBom(record.id); message.success('删除成功'); actionRef.current?.reload(); }}>
            <Button type="link" danger>删除</Button>
          </Popconfirm>
        </>
      ),
    },
  ];

  return (
    <>
      <ProTable columns={columns} request={async () => { const data = await getBomList(); return { data, total: data?.length || 0, success: true }; }}
        actionRef={actionRef} rowKey="id" search={false}
        toolBarRender={() => [<Button key="add" type="primary" icon={<PlusOutlined />} onClick={() => { setEditRecord(null); setModalOpen(true); }}>新增BOM</Button>]} />
      <ModalForm title={editRecord ? '编辑BOM' : '新增BOM'} open={modalOpen} onOpenChange={setModalOpen} initialValues={editRecord}
        onFinish={async (values) => { editRecord ? await updateBom(editRecord.id, values) : await addBom(values); message.success(editRecord ? '修改成功' : '新增成功'); actionRef.current?.reload(); return true; }}>
        <ProFormText name="productId" label="成品ID" rules={[{ required: true }]} />
        <ProFormText name="version" label="版本" />
      </ModalForm>
    </>
  );
}
