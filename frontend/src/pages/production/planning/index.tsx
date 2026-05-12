import { useRef, useState } from 'react';
import { ProTable, ModalForm, ProFormSelect, ProFormDigit, ProFormDatePicker, ProFormText } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Button, Tag, message, Popconfirm, Space } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getPlanningList, addPlanning, updatePlanning, deletePlanning, auditPlanning } from '@/services/production';
import { getAllProducts } from '@/services/inventory';

export default function PlanningList() {
  const actionRef = useRef<ActionType>();
  const [modalOpen, setModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);

  const columns: ProColumns[] = [
    { title: '计划号', dataIndex: 'planNo', key: 'planNo' },
    { title: '产品', dataIndex: 'productName', key: 'productName' },
    { title: '计划数量', dataIndex: 'planQty', key: 'planQty', search: false },
    { title: '开始日期', dataIndex: 'startDate', key: 'startDate', search: false, valueType: 'date' },
    { title: '结束日期', dataIndex: 'endDate', key: 'endDate', search: false, valueType: 'date' },
    {
      title: '状态', dataIndex: 'status', key: 'status',
      valueEnum: {
        0: { text: '草稿', status: 'Default' },
        1: { text: '已审核', status: 'Success' },
        2: { text: '执行中', status: 'Processing' },
        3: { text: '完成', status: 'Success' },
      },
      render: (_, record) => {
        const statusMap: Record<number, { text: string; color: string }> = {
          0: { text: '草稿', color: 'default' },
          1: { text: '已审核', color: 'green' },
          2: { text: '执行中', color: 'blue' },
          3: { text: '完成', color: 'green' },
        };
        const s = statusMap[record.status] || { text: '未知', color: 'default' };
        return <Tag color={s.color}>{s.text}</Tag>;
      },
    },
    { title: '备注', dataIndex: 'remark', key: 'remark', search: false, ellipsis: true },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', search: false, valueType: 'dateTime' },
    {
      title: '操作', key: 'action', search: false, fixed: 'right',
      render: (_, record) => (
        <Space>
          {(record.status === 0) && (
            <>
              <Button type="link" onClick={() => {
                setEditRecord(record);
                setModalOpen(true);
              }}>编辑</Button>
              <Button type="link" onClick={async () => {
                await auditPlanning(record.id);
                message.success('审核成功');
                actionRef.current?.reload();
              }}>审核</Button>
            </>
          )}
          <Popconfirm title="确定删除?" onConfirm={async () => {
            await deletePlanning(record.id);
            message.success('删除成功');
            actionRef.current?.reload();
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
        columns={columns}
        request={async (params) => {
          const data = await getPlanningList(params);
          return { data: data.records, total: data.total, success: true };
        }}
        actionRef={actionRef}
        rowKey="id"
        search={{ labelWidth: 'auto' }}
        toolBarRender={() => [
          <Button key="add" type="primary" icon={<PlusOutlined />} onClick={() => {
            setEditRecord(null);
            setModalOpen(true);
          }}>新增计划</Button>,
        ]}
      />
      <ModalForm
        title={editRecord ? '编辑计划' : '新增计划'}
        open={modalOpen}
        onOpenChange={setModalOpen}
        initialValues={editRecord}
        width={500}
        onFinish={async (values) => {
          if (editRecord) {
            await updatePlanning(editRecord.id, values);
          } else {
            await addPlanning(values);
          }
          message.success(editRecord ? '修改成功' : '新增成功');
          actionRef.current?.reload();
          return true;
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
        <ProFormDigit name="planQty" label="计划数量" rules={[{ required: true, message: '请输入计划数量' }]} min={1} />
        <ProFormDatePicker name="startDate" label="开始日期" />
        <ProFormDatePicker name="endDate" label="结束日期" />
        <ProFormText name="remark" label="备注" />
      </ModalForm>
    </>
  );
}
