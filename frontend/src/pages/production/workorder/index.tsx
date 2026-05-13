import { useRef, useState } from 'react';
import { ProTable, ModalForm, ProFormSelect, ProFormDigit, ProFormDatePicker, ProFormText } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Button, Tag, Modal, InputNumber, App, Popconfirm, Space } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import {
  getWorkOrderList, addWorkOrder, updateWorkOrder, deleteWorkOrder,
  startWorkOrder, completeWorkOrder, closeWorkOrder, getBomList, getBomItems,
} from '@/services/production';
import { getAllProducts } from '@/services/inventory';

export default function WorkOrderList() {
  const { message } = App.useApp();
  const actionRef = useRef<ActionType>();
  const [modalOpen, setModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);
  const [tableKey, setTableKey] = useState(0);

  const [completeModalOpen, setCompleteModalOpen] = useState(false);
  const [completeRecord, setCompleteRecord] = useState<any>(null);
  const [actualQty, setActualQty] = useState(0);
  const [bomPreview, setBomPreview] = useState<any[]>([]);
  const [bomPreviewOpen, setBomPreviewOpen] = useState(false);

  const columns: ProColumns[] = [
    { title: '工单号', dataIndex: 'orderNo', key: 'orderNo', sorter: true },
    { title: '产品', dataIndex: 'productName', key: 'productName' },
    { title: '计划数量', dataIndex: 'planQty', key: 'planQty', search: false },
    { title: '实际数量', dataIndex: 'actualQty', key: 'actualQty', search: false },
    {
      title: '状态', dataIndex: 'status', key: 'status',
      valueEnum: {
        0: { text: '待领料', status: 'Default' },
        1: { text: '生产中', status: 'Processing' },
        2: { text: '完成', status: 'Success' },
        3: { text: '关闭', status: 'Error' },
      },
      render: (_, record) => {
        const statusMap: Record<number, { text: string; color: string }> = {
          0: { text: '待领料', color: 'default' },
          1: { text: '生产中', color: 'blue' },
          2: { text: '完成', color: 'green' },
          3: { text: '关闭', color: 'red' },
        };
        const s = statusMap[record.status] || { text: '未知', color: 'default' };
        return <Tag color={s.color}>{s.text}</Tag>;
      },
    },
    { title: '开始日期', dataIndex: 'startDate', key: 'startDate', search: false, valueType: 'date' },
    { title: '结束日期', dataIndex: 'endDate', key: 'endDate', search: false, valueType: 'date' },
    { title: '负责人', dataIndex: 'assignee', key: 'assignee', search: false },
    {
      title: '操作', key: 'action', search: false, fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button type="link" onClick={() => {
            setEditRecord(record);
            setModalOpen(true);
          }}>编辑</Button>
          {record.status === 0 && (
            <Button type="link" onClick={async () => {
              await startWorkOrder(record.id);
              message.success('已开工');
              setTableKey(k => k + 1);
            }}>开工</Button>
          )}
          {record.status === 1 && (
            <Button type="link" onClick={() => {
              setCompleteRecord(record);
              setActualQty(record.planQty || 0);
              setCompleteModalOpen(true);
            }}>完工</Button>
          )}
          {(record.status === 0 || record.status === 1) && (
            <Button type="link" danger onClick={async () => {
              await closeWorkOrder(record.id);
              message.success('已关闭');
              setTableKey(k => k + 1);
            }}>关闭</Button>
          )}
          <Popconfirm title="确定删除?" onConfirm={async () => {
            await deleteWorkOrder(record.id);
            message.success('删除成功');
            setTableKey(k => k + 1);
          }}>
            <Button type="link" danger>删除</Button>
          </Popconfirm>
        </Space>
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
          const data = await getWorkOrderList(query);
          return { data: data.records, total: data.total, success: true };
        }}
        actionRef={actionRef}
        rowKey="id"
        search={{ labelWidth: 'auto' }}
        toolBarRender={() => [
          <Button key="add" type="primary" icon={<PlusOutlined />} onClick={() => {
            setEditRecord(null);
            setBomPreview([]);
            setModalOpen(true);
          }}>新增工单</Button>,
        ]}
      />
      <ModalForm
        key={editRecord?.id || 'add'}
        title={editRecord ? '编辑工单' : '新增工单'}
        open={modalOpen}
        onOpenChange={setModalOpen}
        initialValues={editRecord}
        modalProps={{ destroyOnClose: true }}
        width={600}
        onFinish={async (values) => {
          try {
            if (editRecord) {
              await updateWorkOrder(editRecord.id, values);
            } else {
              await addWorkOrder(values);
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
        <ProFormSelect
          name="productId"
          label="产品"
          rules={[{ required: true, message: '请选择产品' }]}
          placeholder="选择产品"
          request={async () => {
            const data: any = await getAllProducts();
            return (data || []).map((p: any) => ({ label: p.name, value: p.id }));
          }}
        />
        <ProFormSelect
          name="bomId"
          label="BOM（可选）"
          placeholder="选择BOM版本"
          request={async () => {
            const data: any = await getBomList({ pageNum: 1, pageSize: 999 });
            const records = data?.records || [];
            return records
              .filter((b: any) => b.status === 1)
              .map((b: any) => ({ label: `${b.productName} - ${b.version}`, value: b.id }));
          }}
          fieldProps={{
            onChange: async (val: number) => {
              if (val) {
                try {
                  const items: any = await getBomItems(val);
                  setBomPreview(items || []);
                } catch (err: any) {
                  message.error(err?.message || '获取BOM物料失败');
                  setBomPreview([]);
                }
              } else {
                setBomPreview([]);
              }
            },
          }}
        />
        {bomPreview.length > 0 && (
          <div style={{ marginBottom: 16, padding: 8, background: '#fafafa', borderRadius: 4 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 4 }}>
              <strong>BOM物料预览</strong>
              <Button type="link" size="small" onClick={() => setBomPreviewOpen(true)}>展开预览</Button>
            </div>
            <span style={{ fontSize: 12, color: '#888' }}>{bomPreview.length} 种物料</span>
          </div>
        )}
        <ProFormDigit name="planQty" label="计划数量" rules={[{ required: true, message: '请输入计划数量' }]} min={1} />
        <ProFormSelect
          name="priority"
          label="优先级"
          options={[
            { label: '低', value: 0 },
            { label: '中', value: 1 },
            { label: '高', value: 2 },
          ]}
          initialValue={1}
        />
        <ProFormDatePicker name="startDate" label="开始日期" />
        <ProFormDatePicker name="endDate" label="结束日期" />
      </ModalForm>

      {/* Complete work order modal */}
      <Modal
        title="完工确认"
        open={completeModalOpen}
        onCancel={() => setCompleteModalOpen(false)}
        onOk={async () => {
          if (actualQty <= 0) {
            message.error('请输入实际生产数量');
            return;
          }
          await completeWorkOrder(completeRecord.id, actualQty);
          message.success('已完工');
          setCompleteModalOpen(false);
          setTableKey(k => k + 1);
        }}
      >
        <div style={{ marginBottom: 16 }}>
          <p>工单号: <strong>{completeRecord?.orderNo}</strong></p>
          <p>计划数量: <strong>{completeRecord?.planQty}</strong></p>
        </div>
        <div>
          <label>实际生产数量:</label>
          <InputNumber
            style={{ width: '100%', marginTop: 4 }}
            min={1}
            value={actualQty}
            onChange={(val) => setActualQty(val || 0)}
          />
        </div>
      </Modal>

      {/* BOM preview modal */}
      <Modal
        title="BOM物料预览"
        open={bomPreviewOpen}
        onCancel={() => setBomPreviewOpen(false)}
        footer={<Button onClick={() => setBomPreviewOpen(false)}>关闭</Button>}
        width={600}
      >
        <Table
          dataSource={bomPreview}
          pagination={false}
          rowKey={(record: any, index: number) => record.materialId?.toString() || index.toString()}
          columns={[
            { title: '物料', dataIndex: 'materialName', key: 'materialName' },
            { title: '数量', dataIndex: 'quantity', key: 'quantity' },
            { title: '单位', dataIndex: 'unit', key: 'unit' },
          ]}
          size="small"
        />
      </Modal>
    </>
  );
}
