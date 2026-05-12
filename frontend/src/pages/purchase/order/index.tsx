import { useRef, useState } from 'react';
import { ProTable, ProFormSelect, ProFormDatePicker, ProFormDigit } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Button, Tag, Modal, Form, Table, InputNumber, Input, message, Popconfirm, Space } from 'antd';
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons';
import { getOrderList, addOrder, auditOrder, cancelOrder, deleteOrder, getAllSuppliers } from '@/services/purchase';
import { getAllProducts } from '@/services/inventory';
import PermissionBtn from '@/components/PermissionBtn';

interface OrderItem {
  key: string;
  productId: number | undefined;
  productName: string;
  quantity: number;
  unitPrice: number;
  amount: number;
}

export default function OrderList() {
  const actionRef = useRef<ActionType>();
  const [modalOpen, setModalOpen] = useState(false);
  const [form] = Form.useForm();
  const [items, setItems] = useState<OrderItem[]>([]);
  const [loading, setLoading] = useState(false);

  const columns: ProColumns[] = [
    { title: '订单编号', dataIndex: 'orderNo', key: 'orderNo' },
    { title: '供应商', dataIndex: 'supplierName', key: 'supplierName' },
    { title: '订单日期', dataIndex: 'orderDate', key: 'orderDate', search: false, valueType: 'date' },
    { title: '交货日期', dataIndex: 'deliveryDate', key: 'deliveryDate', search: false, valueType: 'date' },
    { title: '总数量', dataIndex: 'totalQty', key: 'totalQty', search: false },
    { title: '总金额', dataIndex: 'totalAmount', key: 'totalAmount', search: false, valueType: 'money' },
    {
      title: '状态', dataIndex: 'status', key: 'status',
      valueEnum: { 0: '草稿', 1: '待审', 2: '已审', 3: '收货中', 4: '完成', 5: '取消' },
      render: (_, record) => {
        const statusMap: Record<number, { text: string; color: string }> = { 0: { text: '草稿', color: 'default' }, 1: { text: '待审', color: 'orange' }, 2: { text: '已审', color: 'blue' }, 3: { text: '收货中', color: 'cyan' }, 4: { text: '完成', color: 'green' }, 5: { text: '取消', color: 'red' } };
        const s = statusMap[record.status] || { text: '未知', color: 'default' };
        return <Tag color={s.color}>{s.text}</Tag>;
      },
    },
    {
      title: '操作', key: 'action', search: false,
      render: (_, record) => (
        <Space>
          {record.status === 1 && <PermissionBtn permission="purchase:order:audit" type="link" onClick={async () => { await auditOrder(record.id); message.success('审核成功'); actionRef.current?.reload(); }}>审核</PermissionBtn>}
          {(record.status === 0 || record.status === 1) && <PermissionBtn permission="purchase:order:cancel" type="link" danger><Popconfirm title="确定取消?" onConfirm={async () => { await cancelOrder(record.id); message.success('已取消'); actionRef.current?.reload(); }}>取消</Popconfirm></PermissionBtn>}
          <PermissionBtn permission="purchase:order:delete" type="link" danger><Popconfirm title="确定删除?" onConfirm={async () => { await deleteOrder(record.id); message.success('删除成功'); actionRef.current?.reload(); }}>删除</Popconfirm></PermissionBtn>
        </Space>
      ),
    },
  ];

  const handleAddItem = () => {
    const newItem: OrderItem = {
      key: Date.now().toString(),
      productId: undefined,
      productName: '',
      quantity: 1,
      unitPrice: 0,
      amount: 0,
    };
    setItems([...items, newItem]);
  };

  const handleRemoveItem = (key: string) => {
    setItems(items.filter((item) => item.key !== key));
  };

  const handleItemChange = (key: string, field: keyof OrderItem, value: any) => {
    const newItems = items.map((item) => {
      if (item.key === key) {
        const updated = { ...item, [field]: value };
        if (field === 'quantity' || field === 'unitPrice') {
          updated.amount = updated.quantity * updated.unitPrice;
        }
        return updated;
      }
      return item;
    });
    setItems(newItems);
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (items.length === 0) {
        message.error('请添加订单明细');
        return;
      }
      const hasInvalidItem = items.some((item) => !item.productId);
      if (hasInvalidItem) {
        message.error('请选择所有明细的商品');
        return;
      }
      setLoading(true);
      const record: any = {
        ...values,
        items: items.map((item) => ({
          productId: item.productId,
          quantity: item.quantity,
          unitPrice: item.unitPrice,
        })),
      };
      await addOrder(record);
      message.success('新增成功');
      setModalOpen(false);
      actionRef.current?.reload();
    } catch (e: any) {
      if (e?.errorFields) return; // form validation error
      message.error('操作失败');
    } finally {
      setLoading(false);
    }
  };

  const itemColumns = [
    {
      title: '商品',
      render: (_: any, record: OrderItem) => (
        <ProFormSelect
          name={'product_' + record.key}
          placeholder="选择商品"
          width={220}
          request={async () => {
            const data: any = await getAllProducts();
            return (data || []).map((p: any) => ({ label: p.name, value: p.id }));
          }}
          fieldProps={{
            value: record.productId,
            onChange: (val: number, option: any) => {
              handleItemChange(record.key, 'productId', val);
              handleItemChange(record.key, 'productName', option?.label || '');
            },
          }}
        />
      ),
    },
    {
      title: '数量',
      width: 120,
      render: (_: any, record: OrderItem) => (
        <InputNumber
          min={1}
          value={record.quantity}
          onChange={(val) => handleItemChange(record.key, 'quantity', val || 0)}
        />
      ),
    },
    {
      title: '单价',
      width: 120,
      render: (_: any, record: OrderItem) => (
        <InputNumber
          min={0}
          precision={2}
          value={record.unitPrice}
          onChange={(val) => handleItemChange(record.key, 'unitPrice', val || 0)}
        />
      ),
    },
    {
      title: '金额',
      width: 120,
      render: (_: any, record: OrderItem) => record.amount.toFixed(2),
    },
    {
      title: '操作',
      width: 80,
      render: (_: any, record: OrderItem) => (
        <Button type="link" danger icon={<DeleteOutlined />} onClick={() => handleRemoveItem(record.key)} />
      ),
    },
  ];

  return (
    <>
      <ProTable columns={columns} request={async (params) => { const data = await getOrderList(params); return { data: data.records, total: data.total, success: true }; }}
        actionRef={actionRef} rowKey="id" search={{ labelWidth: 'auto' }}
        toolBarRender={() => [
          <PermissionBtn key="add" permission="purchase:order:add" type="primary" icon={<PlusOutlined />} onClick={() => { form.resetFields(); setItems([]); setModalOpen(true); }}>新增订单</PermissionBtn>,
        ]} />
      <Modal
        title="新增采购订单"
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={handleSubmit}
        confirmLoading={loading}
        width={900}
        destroyOnClose
      >
        <Form form={form} layout="vertical" style={{ marginBottom: 16 }}>
          <div style={{ display: 'flex', gap: 16, flexWrap: 'wrap' }}>
            <Form.Item name="supplierId" label="供应商" rules={[{ required: true, message: '请选择供应商' }]} style={{ flex: 1, minWidth: 200 }}>
              <ProFormSelect
                name="supplierId"
                label=""
                placeholder="选择供应商"
                request={async () => {
                  const data: any = await getAllSuppliers();
                  return (data || []).map((s: any) => ({ label: s.name, value: s.id }));
                }}
              />
            </Form.Item>
            <Form.Item name="orderNo" label="订单编号" style={{ flex: 1, minWidth: 200 }}>
              <Input placeholder="自动生成" disabled />
            </Form.Item>
            <Form.Item name="orderDate" label="订单日期" style={{ flex: 1, minWidth: 200 }}>
              <ProFormDatePicker name="orderDate" label="" />
            </Form.Item>
            <Form.Item name="deliveryDate" label="交货日期" style={{ flex: 1, minWidth: 200 }}>
              <ProFormDatePicker name="deliveryDate" label="" />
            </Form.Item>
          </div>
        </Form>
        <div style={{ marginBottom: 8, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <strong>订单明细</strong>
          <Button type="dashed" icon={<PlusOutlined />} onClick={handleAddItem}>添加明细</Button>
        </div>
        <Table
          columns={itemColumns}
          dataSource={items}
          pagination={false}
          rowKey="key"
          size="small"
        />
      </Modal>
    </>
  );
}
