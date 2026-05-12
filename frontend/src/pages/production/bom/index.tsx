import { useRef, useState } from 'react';
import { ProTable, ModalForm, ProFormText, ProFormSelect, ProFormDigit } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Button, Tag, Table, InputNumber, message, Popconfirm, Space } from 'antd';
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons';
import { getBomList, addBom, updateBom, deleteBom, enableBom, disableBom } from '@/services/production';
import { getAllProducts } from '@/services/inventory';

interface BomItem {
  key: string;
  materialId: number | undefined;
  materialName: string;
  quantity: number;
  unit: string;
}

export default function BomList() {
  const actionRef = useRef<ActionType>();
  const [modalOpen, setModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);
  const [items, setItems] = useState<BomItem[]>([]);

  const columns: ProColumns[] = [
    {
      title: '产品', dataIndex: 'productName', key: 'productName',
    },
    { title: '版本', dataIndex: 'version', key: 'version', search: false },
    {
      title: '状态', dataIndex: 'status', key: 'status',
      valueEnum: { 0: { text: '禁用', status: 'Error' }, 1: { text: '启用', status: 'Success' } },
      render: (_, record) => (
        <Tag color={record.status === 1 ? 'green' : 'red'}>
          {record.status === 1 ? '启用' : '禁用'}
        </Tag>
      ),
    },
    { title: '创建人', dataIndex: 'creator', key: 'creator', search: false },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', search: false, valueType: 'dateTime' },
    {
      title: '操作', key: 'action', search: false, fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button type="link" onClick={() => {
            setEditRecord(record);
            setItems([]);
            setModalOpen(true);
          }}>编辑</Button>
          {record.status === 0 ? (
            <Button type="link" onClick={async () => { await enableBom(record.id); message.success('已启用'); actionRef.current?.reload(); }}>启用</Button>
          ) : (
            <Button type="link" onClick={async () => { await disableBom(record.id); message.success('已禁用'); actionRef.current?.reload(); }}>禁用</Button>
          )}
          <Popconfirm title="确定删除?" onConfirm={async () => { await deleteBom(record.id); message.success('删除成功'); actionRef.current?.reload(); }}>
            <Button type="link" danger>删除</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const handleAddItem = () => {
    setItems([...items, {
      key: Date.now().toString(),
      materialId: undefined,
      materialName: '',
      quantity: 1,
      unit: '',
    }]);
  };

  const handleRemoveItem = (key: string) => {
    setItems(items.filter((item) => item.key !== key));
  };

  const handleItemChange = (key: string, field: keyof BomItem, value: any) => {
    setItems(items.map((item) => item.key === key ? { ...item, [field]: value } : item));
  };

  const itemColumns = [
    {
      title: '物料',
      render: (_: any, record: BomItem) => (
        <ProFormSelect
          name={'material_' + record.key}
          placeholder="选择物料"
          width={220}
          request={async () => {
            const data: any = await getAllProducts();
            return (data || []).map((p: any) => ({ label: p.name, value: p.id }));
          }}
          fieldProps={{
            value: record.materialId,
            onChange: (val: number, option: any) => {
              handleItemChange(record.key, 'materialId', val);
              handleItemChange(record.key, 'materialName', option?.label || '');
            },
          }}
        />
      ),
    },
    {
      title: '数量', width: 120,
      render: (_: any, record: BomItem) => (
        <InputNumber min={1} value={record.quantity} onChange={(val) => handleItemChange(record.key, 'quantity', val || 1)} />
      ),
    },
    {
      title: '单位', width: 100,
      render: (_: any, record: BomItem) => (
        <ProFormText
          name={'unit_' + record.key}
          placeholder="单位"
          width={100}
          fieldProps={{
            value: record.unit,
            onChange: (e: any) => handleItemChange(record.key, 'unit', e.target.value),
          }}
        />
      ),
    },
    {
      title: '操作', width: 80,
      render: (_: any, record: BomItem) => (
        <Button type="link" danger icon={<DeleteOutlined />} onClick={() => handleRemoveItem(record.key)} />
      ),
    },
  ];

  return (
    <>
      <ProTable
        columns={columns}
        request={async (params) => {
          const data = await getBomList(params);
          return { data: data.records, total: data.total, success: true };
        }}
        actionRef={actionRef}
        rowKey="id"
        search={{ labelWidth: 'auto' }}
        toolBarRender={() => [
          <Button key="add" type="primary" icon={<PlusOutlined />} onClick={() => {
            setEditRecord(null);
            setItems([]);
            setModalOpen(true);
          }}>新增BOM</Button>,
        ]}
      />
      <ModalForm
        title={editRecord ? '编辑BOM' : '新增BOM'}
        open={modalOpen}
        onOpenChange={setModalOpen}
        initialValues={editRecord}
        width={800}
        onFinish={async (values) => {
          if (items.length === 0 && !editRecord) {
            message.error('请添加BOM物料明细');
            return false;
          }
          const payload = {
            ...values,
            items: items.map((item) => ({
              materialId: item.materialId,
              quantity: item.quantity,
              unit: item.unit,
            })),
          };
          if (editRecord) {
            await updateBom(editRecord.id, payload);
          } else {
            await addBom(payload);
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
        <ProFormText name="version" label="版本" placeholder="如: v1.0" />
        <div style={{ marginBottom: 8, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <strong>BOM物料明细</strong>
          <Button type="dashed" icon={<PlusOutlined />} onClick={handleAddItem}>添加物料</Button>
        </div>
        <Table
          columns={itemColumns}
          dataSource={items}
          pagination={false}
          rowKey="key"
          size="small"
        />
      </ModalForm>
    </>
  );
}
