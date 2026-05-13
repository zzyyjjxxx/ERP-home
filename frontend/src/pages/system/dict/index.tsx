import { useRef, useState } from 'react';
import { ProTable, ModalForm, ProFormText } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Button, App, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import request from '@/utils/request';

function DictDataList({ dictTypeId }: { dictTypeId: number }) {
  const { message } = App.useApp();
  const actionRef = useRef<ActionType>(null!);
  const [modalOpen, setModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);
  const [tableKey, setTableKey] = useState(0);

  const columns: ProColumns[] = [
    { title: '标签', dataIndex: 'dictLabel', key: 'dictLabel' },
    { title: '值', dataIndex: 'dictValue', key: 'dictValue' },
    { title: '排序', dataIndex: 'sort', key: 'sort', search: false },
    {
      title: '操作', key: 'action', search: false,
      render: (_, record) => (
        <>
          <Button type="link" onClick={() => { setEditRecord(record); setModalOpen(true); }}>编辑</Button>
          <Popconfirm title="确定删除?" onConfirm={async () => {
            await request.delete(`/system/dict-data/${record.id}`);
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
          const query: any = { ...rest, dictTypeId };
          if (sorter?.field) {
            query.sortField = sorter.field;
            query.sortOrder = sorter.order === 'ascend' ? 'asc' : 'desc';
          }
          const data: any = await request.get('/system/dict-data/list', { params: query });
          return { data: data.records, total: data.total, success: true };
        }}
        actionRef={actionRef}
        rowKey="id"
        search={false}
        toolBarRender={() => [
          <Button key="add" type="primary" icon={<PlusOutlined />} onClick={() => { setEditRecord(null); setModalOpen(true); }}>新增</Button>,
        ]}
      />
      <ModalForm
        key={editRecord?.id || 'add'}
        title={editRecord ? '编辑字典项' : '新增字典项'}
        open={modalOpen}
        onOpenChange={setModalOpen}
        initialValues={{ ...editRecord, dictTypeId }}
        modalProps={{ destroyOnClose: true }}
        onFinish={async (values) => {
          try {
            if (editRecord) {
              await request.put(`/system/dict-data/${editRecord.id}`, values);
            } else {
              await request.post('/system/dict-data', values);
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
        <ProFormText name="dictTypeId" label="字典类型ID" hidden />
        <ProFormText name="dictLabel" label="标签" rules={[{ required: true }]} />
        <ProFormText name="dictValue" label="值" rules={[{ required: true }]} />
        <ProFormText name="cssClass" label="CSS类名" />
        <ProFormText name="sort" label="排序" />
      </ModalForm>
    </>
  );
}

export default function DictManage() {
  const { message } = App.useApp();
  const actionRef = useRef<ActionType>(null!);
  const [modalOpen, setModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);
  const [tableKey, setTableKey] = useState(0);

  const [, setActiveTab] = useState<number>();

  const columns: ProColumns[] = [
    { title: '编码', dataIndex: 'dictCode', key: 'dictCode', sorter: true },
    { title: '名称', dataIndex: 'dictName', key: 'dictName', sorter: true },
    {
      title: '操作', key: 'action', search: false,
      render: (_, record) => (
        <>
          <Button type="link" onClick={() => { setEditRecord(record); setModalOpen(true); }}>编辑</Button>
          <Popconfirm title="确定删除?" onConfirm={async () => {
            await request.delete(`/system/dict-type/${record.id}`);
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
          const data: any = await request.get('/system/dict-type/list', { params: query });
          return { data, total: data?.length || 0, success: true };
        }}
        actionRef={actionRef}
        rowKey="id"
        search={false}
        expandable={{
          expandedRowRender: (record) => <DictDataList dictTypeId={record.id} />,
          onExpand: (expanded, record) => {
            if (expanded) setActiveTab(record.id);
          },
        }}
        toolBarRender={() => [
          <Button key="add" type="primary" icon={<PlusOutlined />} onClick={() => { setEditRecord(null); setModalOpen(true); }}>新增字典类型</Button>,
        ]}
      />
      <ModalForm
        key={editRecord?.id || 'add'}
        title={editRecord ? '编辑字典类型' : '新增字典类型'}
        open={modalOpen}
        onOpenChange={setModalOpen}
        initialValues={editRecord}
        modalProps={{ destroyOnClose: true }}
        onFinish={async (values) => {
          try {
            if (editRecord) {
              await request.put(`/system/dict-type/${editRecord.id}`, values);
            } else {
              await request.post('/system/dict-type', values);
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
        <ProFormText name="dictCode" label="编码" rules={[{ required: true }]} />
        <ProFormText name="dictName" label="名称" rules={[{ required: true }]} />
      </ModalForm>
    </>
  );
}
