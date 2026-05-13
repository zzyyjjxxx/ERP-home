import { useRef, useState } from 'react';
import { ProTable, ModalForm, ProFormText, ProFormSelect, ProFormTextArea } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Tag, Button, App, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getCheckList, addCheck, updateCheck, deleteCheck, auditCheck, unauditCheck, getWarehouseList } from '@/services/inventory';
import PermissionBtn from '@/components/PermissionBtn';

export default function CheckList() {
  const { message } = App.useApp();
  const actionRef = useRef<ActionType>(null!);
  const [modalOpen, setModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);
  const [tableKey, setTableKey] = useState(0);

  const columns: ProColumns[] = [
    { title: '盘点单号', dataIndex: 'checkNo', key: 'checkNo', sorter: true },
    { title: '仓库', dataIndex: 'warehouseName', key: 'warehouseName' },
    { title: '盘点日期', dataIndex: 'checkDate', key: 'checkDate', valueType: 'date', search: false },
    { title: '盘点人', dataIndex: 'checker', key: 'checker', search: false },
    { title: '备注', dataIndex: 'remark', key: 'remark', search: false, ellipsis: true },
    {
      title: '状态', dataIndex: 'status', key: 'status',
      valueEnum: { 0: { text: '待审核', status: 'Processing' }, 1: { text: '已审核', status: 'Success' }, 2: { text: '已取消', status: 'Error' } },
      render: (_, record) => {
        const map: Record<number, { text: string; color: string }> = {
          0: { text: '待审核', color: 'blue' },
          1: { text: '已审核', color: 'green' },
          2: { text: '已取消', color: 'red' },
        };
        const item = map[record.status] || { text: '未知', color: 'default' };
        return <Tag color={item.color}>{item.text}</Tag>;
      },
    },
    {
      title: '操作', key: 'action', search: false, fixed: 'right',
      render: (_, record) => (
        <>
          {record.status === 0 && (
            <Popconfirm title="确定审核?" onConfirm={async () => {
              await auditCheck(record.id);
              message.success('审核成功');
              setTableKey(k => k + 1);
            }}>
              <Button type="link">审核</Button>
            </Popconfirm>
          )}
          {record.status === 1 && <PermissionBtn permission="inventory:check:unaudit" type="link" onClick={async () => { await unauditCheck(record.id); message.success('反审核成功'); setTableKey(k => k + 1); }}>反审核</PermissionBtn>}
          <Button type="link" onClick={() => { setEditRecord(record); setModalOpen(true); }} disabled={record.status !== 0}>编辑</Button>
          <Popconfirm title="确定删除?" onConfirm={async () => {
            await deleteCheck(record.id);
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
          const data: any = await getCheckList(query);
          return { data: data.records, total: data.total, success: true };
        }}
        actionRef={actionRef}
        rowKey="id"
        search={{ labelWidth: 'auto' }}
        toolBarRender={() => [
          <Button key="add" type="primary" icon={<PlusOutlined />} onClick={() => { setEditRecord(null); setModalOpen(true); }}>新增盘点单</Button>,
        ]}
      />
      <ModalForm
        key={editRecord?.id || 'add'}
        title={editRecord ? '编辑盘点单' : '新增盘点单'}
        open={modalOpen}
        onOpenChange={setModalOpen}
        initialValues={editRecord}
        modalProps={{ destroyOnClose: true }}
        onFinish={async (values) => {
          try {
            if (editRecord) {
              await updateCheck(editRecord.id, values);
            } else {
              await addCheck(values);
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
        <ProFormText name="checkNo" label="盘点单号" />
        <ProFormSelect
          name="warehouseId"
          label="仓库"
          rules={[{ required: true }]}
          request={async () => {
            const data: any = await getWarehouseList();
            return (data || []).map((w: any) => ({ label: w.name, value: w.id }));
          }}
        />
        <ProFormText name="checker" label="盘点人" />
        <ProFormText name="checkDate" label="盘点日期" />
        <ProFormTextArea name="remark" label="备注" />
      </ModalForm>
    </>
  );
}
