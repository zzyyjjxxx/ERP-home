import { useRef, useState } from 'react';
import { ProTable, ModalForm, ProFormText, ProFormSelect, ProFormDigit, ProFormTextArea } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Tag, Button, App, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getTransferList, addTransfer, updateTransfer, deleteTransfer, auditTransfer, unauditTransfer, getWarehouseList } from '@/services/inventory';
import { getAllProducts } from '@/services/inventory';
import PermissionBtn from '@/components/PermissionBtn';

export default function TransferList() {
  const { message } = App.useApp();
  const actionRef = useRef<ActionType>(null!);
  const [modalOpen, setModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);
  const [tableKey, setTableKey] = useState(0);

  const columns: ProColumns[] = [
    { title: '调拨单号', dataIndex: 'transferNo', key: 'transferNo', sorter: true },
    { title: '调出仓库', dataIndex: 'fromWarehouseName', key: 'fromWarehouseName' },
    { title: '调入仓库', dataIndex: 'toWarehouseName', key: 'toWarehouseName' },
    { title: '调拨日期', dataIndex: 'transferDate', key: 'transferDate', valueType: 'date', search: false },
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
              await auditTransfer(record.id);
              message.success('审核成功');
              setTableKey(k => k + 1);
            }}>
              <Button type="link">审核</Button>
            </Popconfirm>
          )}
          {record.status === 1 && <PermissionBtn permission="inventory:transfer:unaudit" type="link" onClick={async () => { await unauditTransfer(record.id); message.success('反审核成功'); setTableKey(k => k + 1); }}>反审核</PermissionBtn>}
          <Button type="link" onClick={() => { setEditRecord(record); setModalOpen(true); }} disabled={record.status !== 0}>编辑</Button>
          <Popconfirm title="确定删除?" onConfirm={async () => {
            await deleteTransfer(record.id);
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
          const data: any = await getTransferList(query);
          return { data: data.records, total: data.total, success: true };
        }}
        actionRef={actionRef}
        rowKey="id"
        search={{ labelWidth: 'auto' }}
        toolBarRender={() => [
          <Button key="add" type="primary" icon={<PlusOutlined />} onClick={() => { setEditRecord(null); setModalOpen(true); }}>新增调拨单</Button>,
        ]}
      />
      <ModalForm
        key={editRecord?.id || 'add'}
        title={editRecord ? '编辑调拨单' : '新增调拨单'}
        open={modalOpen}
        onOpenChange={setModalOpen}
        initialValues={editRecord}
        modalProps={{ destroyOnClose: true }}
        onFinish={async (values) => {
          try {
            if (editRecord) {
              await updateTransfer(editRecord.id, values);
            } else {
              await addTransfer(values);
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
        <ProFormText name="transferNo" label="调拨单号" />
        <ProFormSelect
          name="fromWarehouseId"
          label="调出仓库"
          rules={[{ required: true }]}
          request={async () => {
            const data: any = await getWarehouseList();
            return (data || []).map((w: any) => ({ label: w.name, value: w.id }));
          }}
        />
        <ProFormSelect
          name="toWarehouseId"
          label="调入仓库"
          rules={[{ required: true }]}
          request={async () => {
            const data: any = await getWarehouseList();
            return (data || []).map((w: any) => ({ label: w.name, value: w.id }));
          }}
        />
        <ProFormSelect
          name="productId"
          label="商品"
          rules={[{ required: true }]}
          request={async () => {
            const data: any = await getAllProducts();
            return (data || []).map((p: any) => ({ label: p.name, value: p.id }));
          }}
        />
        <ProFormDigit name="quantity" label="数量" rules={[{ required: true }]} />
        <ProFormText name="transferDate" label="调拨日期" />
        <ProFormTextArea name="remark" label="备注" />
      </ModalForm>
    </>
  );
}
