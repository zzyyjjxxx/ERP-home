import { useRef, useState } from 'react';
import { ProTable, ModalForm, ProFormText, ProFormSelect } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Button, message, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getSubjectTree, addSubject, updateSubject, deleteSubject } from '@/services/finance';

export default function SubjectList() {
  const actionRef = useRef<ActionType>();
  const [modalOpen, setModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);

  const columns: ProColumns[] = [
    { title: '编码', dataIndex: 'code', key: 'code' },
    { title: '名称', dataIndex: 'name', key: 'name' },
    { title: '类型', dataIndex: 'subjectType', key: 'subjectType', search: false },
    { title: '操作', key: 'action', search: false,
      render: (_, record) => (
        <>
          <Button type="link" onClick={() => { setEditRecord(record); setModalOpen(true); }}>编辑</Button>
          <Popconfirm title="确定删除?" onConfirm={async () => { await deleteSubject(record.id); message.success('删除成功'); actionRef.current?.reload(); }}>
            <Button type="link" danger>删除</Button>
          </Popconfirm>
        </>
      ),
    },
  ];

  return (
    <>
      <ProTable columns={columns} request={async () => { const data = await getSubjectTree(); return { data, total: data?.length || 0, success: true }; }}
        actionRef={actionRef} rowKey="id" search={false} pagination={false}
        toolBarRender={() => [<Button key="add" type="primary" icon={<PlusOutlined />} onClick={() => { setEditRecord(null); setModalOpen(true); }}>新增科目</Button>]} />
      <ModalForm title={editRecord ? '编辑科目' : '新增科目'} open={modalOpen} onOpenChange={setModalOpen} initialValues={editRecord}
        onFinish={async (values) => { editRecord ? await updateSubject(editRecord.id, values) : await addSubject(values); message.success(editRecord ? '修改成功' : '新增成功'); actionRef.current?.reload(); return true; }}>
        <ProFormText name="code" label="编码" rules={[{ required: true }]} />
        <ProFormText name="name" label="名称" rules={[{ required: true }]} />
        <ProFormSelect name="subjectType" label="类型" options={[{ label: '资产', value: 'ASSET' }, { label: '负债', value: 'LIABILITY' }, { label: '权益', value: 'EQUITY' }, { label: '收入', value: 'INCOME' }, { label: '费用', value: 'EXPENSE' }]} />
        <ProFormText name="parentId" label="父级ID" />
      </ModalForm>
    </>
  );
}
