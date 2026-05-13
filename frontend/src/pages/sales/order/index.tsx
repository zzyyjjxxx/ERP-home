import { useRef, useState, useEffect } from 'react';
import { ProTable, ProFormSelect, ProFormDatePicker } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Button, Tag, Modal, Form, Table, InputNumber, Input, App, Popconfirm, Space } from 'antd';
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons';
import { getOrderList, addOrder, updateOrder, auditOrder, unauditOrder, pushDownDelivery, cancelOrder, deleteOrder, getAllCustomers } from '@/services/sales';
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

export default function SalesOrderList() {
  const { message } = App.useApp();
  const actionRef = useRef<ActionType>();
  const [modalOpen, setModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);
  const [tableKey, setTableKey] = useState(0);

  const [form] = Form.useForm();
  const [items, setItems] = useState<OrderItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [customerMap, setCustomerMap] = useState<Record<number, string>>({});

  useEffect(() => {
    getAllCustomers().then((data: any) => {
      const map: Record<number, string> = {};
      (data || []).forEach((c: any) => { map[c.id] = c.name; });
      setCustomerMap(map);
    });
  }, []);

  const columns: ProColumns[] = [
    { title: '订单号', dataIndex: 'orderNo', key: 'orderNo', sorter: true },
    {
      title: '客户', dataIndex: 'customerId', key: 'customerId', search: false,
      render: (_, record) => customerMap[record.customerId] || record.customerName || record.customerId,
    },
    { title: '订单日期', dataIndex: 'orderDate', key: 'orderDate', search: false, valueType: 'date' },
    { title: '总数量', dataIndex: 'totalQty', key: 'totalQty', search: false },
    { title: '总金额', dataIndex: 'totalAmount', key: 'totalAmount', search: false, valueType: 'money' },
    {
      title: '状态', dataIndex: 'status', key: 'status',
      valueEnum: { 0: '草稿', 1: '待审', 2: '已审', 3: '发货中', 4: '完成', 5: '取消' },
      render: (_, record) => {
        const m: Record<number, { t: string; c: string }> = { 0: { t: '草稿', c: 'default' }, 1: { t: '待审', c: 'orange' }, 2: { t: '已审', c: 'blue' }, 3: { t: '发货中', c: 'cyan' }, 4: { t: '完成', c: 'green' }, 5: { t: '取消', c: 'red' } };
        const s = m[record.status] || { t: '未知', c: 'default' };
        return <Tag color={s.c}>{s.t}</Tag>;
      },
    },
    {
      title: '操作', key: 'action', search: false,
      render: (_, record) => (
        <Space>
          {record.status === 1 && <PermissionBtn permission="sales:order:audit" type="link" onClick={async () => { await auditOrder(record.id); message.success('审核成功'); setTableKey(k => k + 1); }}>审核</PermissionBtn>}
          {record.status === 2 && <PermissionBtn permission="sales:order:unaudit" type="link" onClick={async () => { await unauditOrder(record.id); message.success('反审核成功'); setTableKey(k => k + 1); }}>反审核</PermissionBtn>}
          {record.status === 2 && <PermissionBtn permission="sales:order:push-delivery" type="link" onClick={async () => { await pushDownDelivery(record.id); message.success('下推成功'); setTableKey(k => k + 1); }}>下推发货</PermissionBtn>}
          {(record.status === 0 || record.status === 1) && (
            <>
              <PermissionBtn permission="sales:order:edit" type="link" onClick={() => {
                setEditRecord(record);
                setItems((record.items || []).map((it: any, idx: number) => ({
                  key: String(it.id || Date.now() + idx),
                  productId: it.productId,
                  productName: it.productName || '',
                  quantity: it.quantity || 1,
                  unitPrice: it.unitPrice || 0,
                  amount: (it.quantity || 0) * (it.unitPrice || 0),
                })));
                form.setFieldsValue(record);
                setModalOpen(true);
              }}>编辑</PermissionBtn>
              <PermissionBtn permission="sales:order:cancel" type="link" danger>
                <Popconfirm title="确定取消?" onConfirm={async () => { await cancelOrder(record.id); message.success('已取消'); setTableKey(k => k + 1); }}>取消</Popconfirm>
              </PermissionBtn>
            </>
          )}
          <PermissionBtn permission="sales:order:delete" type="link" danger>
            <Popconfirm title="确定删除?" onConfirm={async () => { await deleteOrder(record.id); message.success('删除成功'); setTableKey(k => k + 1); }}>删除</Popconfirm>
          </PermissionBtn>
        </Space>
      ),
    },
  ];

  const handleAddItem = () => {
    setItems([...items, { key: Date.now().toString(), productId: undefined, productName: '', quantity: 1, unitPrice: 0, amount: 0 }]);
  };

  const handleRemoveItem = (key: string) => {
    setItems(items.filter((item) => item.key !== key));
  };

  const handleItemChange = (key: string, field: keyof OrderItem, value: any) => {
    setItems(items.map((item) => {
      if (item.key === key) {
        const updated = { ...item, [field]: value };
        if (field === 'quantity' || field === 'unitPrice') {
          updated.amount = updated.quantity * updated.unitPrice;
        }
        return updated;
      }
      return item;
    }));
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (items.length === 0) { message.error('请添加订单明细'); return; }
      if (items.some((item) => !item.productId)) { message.error('请选择所有明细的商品'); return; }
      setLoading(true);
      const record: any = {
        ...values,
        items: items.map((item) => ({ productId: item.productId, quantity: item.quantity, unitPrice: item.unitPrice })),
      };
      if (editRecord) {
        await updateOrder(editRecord.id, record);
        message.success('修改成功');
      } else {
        await addOrder(record);
        message.success('新增成功');
      }
      setModalOpen(false);
      setTableKey(k => k + 1);
    } catch (e: any) {
      if (e?.errorFields) return;
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
    { title: '数量', width: 120, render: (_: any, record: OrderItem) => <InputNumber min={1} value={record.quantity} onChange={(val) => handleItemChange(record.key, 'quantity', val || 0)} /> },
    { title: '单价', width: 120, render: (_: any, record: OrderItem) => <InputNumber min={0} precision={2} value={record.unitPrice} onChange={(val) => handleItemChange(record.key, 'unitPrice', val || 0)} /> },
    { title: '金额', width: 120, render: (_: any, record: OrderItem) => record.amount.toFixed(2) },
    { title: '操作', width: 80, render: (_: any, record: OrderItem) => <Button type="link" danger icon={<DeleteOutlined />} onClick={() => handleRemoveItem(record.key)} /> },
  ];

  return (
    <>
      <ProTable key={tableKey} columns={columns} request={async (params) => { const { sorter, ...rest } = params; const query: any = { ...rest }; if (sorter?.field) { query.sortField = sorter.field; query.sortOrder = sorter.order === 'ascend' ? 'asc' : 'desc'; } const data = await getOrderList(query); return { data: data.records, total: data.total, success: true }; }}
        actionRef={actionRef} rowKey="id" search={{ labelWidth: 'auto' }}
        toolBarRender={() => [
          <PermissionBtn key="add" permission="sales:order:add" type="primary" icon={<PlusOutlined />} onClick={() => { setEditRecord(null); form.resetFields(); setItems([]); setModalOpen(true); }}>新增订单</PermissionBtn>,
        ]} />
      <Modal
        title={editRecord ? '编辑销售订单' : '新增销售订单'}
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={handleSubmit}
        confirmLoading={loading}
        width={900}
        destroyOnClose
      >
        <Form form={form} layout="vertical" style={{ marginBottom: 16 }}>
          <div style={{ display: 'flex', gap: 16, flexWrap: 'wrap' }}>
            <Form.Item name="customerId" label="客户" rules={[{ required: true, message: '请选择客户' }]} style={{ flex: 1, minWidth: 200 }}>
              <ProFormSelect
                name="customerId"
                label=""
                placeholder="选择客户"
                request={async () => {
                  const data: any = await getAllCustomers();
                  return (data || []).map((c: any) => ({ label: c.name, value: c.id }));
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
        <Table columns={itemColumns} dataSource={items} pagination={false} rowKey="key" size="small" />
      </Modal>
    </>
  );
}
