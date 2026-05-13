import { useRef, useState, useEffect } from 'react';
import { ProTable } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Button, Tag, App, Popconfirm, Modal, Form, DatePicker, Select, Input, InputNumber, TreeSelect, Space, Table, Typography } from 'antd';
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import {
  getVoucherList, addVoucher, updateVoucher, deleteVoucher, auditVoucher, unauditVoucher,
  getVoucherDetail,
} from '@/services/finance';
import { getSubjectTree } from '@/services/finance';

interface VoucherItem {
  key: string;
  subjectId: number | null;
  summary: string;
  debit: number;
  credit: number;
}

const STATUS_MAP: Record<number, { text: string; color: string }> = {
  0: { text: '草稿', color: 'default' },
  1: { text: '已审', color: 'blue' },
  2: { text: '过账', color: 'green' },
  3: { text: '作废', color: 'red' },
};

export default function VoucherList() {
  const { message } = App.useApp();
  const actionRef = useRef<ActionType>();
  const [modalOpen, setModalOpen] = useState(false);
  const [editId, setEditId] = useState<number | null>(null);
  const [tableKey, setTableKey] = useState(0);

  const [confirmLoading, setConfirmLoading] = useState(false);
  const [form] = Form.useForm();
  const [items, setItems] = useState<VoucherItem[]>([]);
  const [subjectTree, setSubjectTree] = useState<any[]>([]);

  useEffect(() => {
    getSubjectTree().then(setSubjectTree).catch(() => {});
  }, []);

  const createEmptyItem = (): VoucherItem => ({
    key: `${Date.now()}_${Math.random()}`,
    subjectId: null,
    summary: '',
    debit: 0,
    credit: 0,
  });

  const resetForm = () => {
    form.resetFields();
    setItems([createEmptyItem()]);
    setEditId(null);
  };

  const handleOpenAdd = () => {
    resetForm();
    setModalOpen(true);
  };

  const handleOpenEdit = async (id: number) => {
    setEditId(id);
    try {
      const detail = await getVoucherDetail(id);
      form.setFieldsValue({
        voucherDate: detail.voucherDate ? dayjs(detail.voucherDate) : undefined,
        voucherType: detail.voucherType,
        remark: detail.remark,
      });
      if (detail.items && detail.items.length > 0) {
        setItems(detail.items.map((item: any, idx: number) => ({
          key: `item_${idx}_${item.id || Date.now()}`,
          subjectId: item.subjectId,
          summary: item.summary || '',
          debit: item.debit || 0,
          credit: item.credit || 0,
        })));
      } else {
        setItems([createEmptyItem()]);
      }
    } catch (err: any) {
      message.error(err?.message || '获取凭证详情失败');
      setItems([createEmptyItem()]);
    }
    setModalOpen(true);
  };

  const totalDebit = items.reduce((sum, item) => sum + (Number(item.debit) || 0), 0);
  const totalCredit = items.reduce((sum, item) => sum + (Number(item.credit) || 0), 0);

  const updateItem = (key: string, field: keyof VoucherItem, value: any) => {
    setItems(prev => prev.map(item =>
      item.key === key ? { ...item, [field]: value } : item
    ));
  };

  const addItem = () => {
    setItems(prev => [...prev, createEmptyItem()]);
  };

  const removeItem = (key: string) => {
    if (items.length <= 1) {
      message.warning('至少保留一条分录');
      return;
    }
    setItems(prev => prev.filter(item => item.key !== key));
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();

      // Validate items
      for (const item of items) {
        if (!item.subjectId) {
          message.error('请为每条分录选择科目');
          return;
        }
        if (item.debit === 0 && item.credit === 0) {
          message.error('每条分录借方金额和贷方金额不能同时为0');
          return;
        }
        if (item.debit > 0 && item.credit > 0) {
          message.error('每条分录不能同时有借方和贷方金额');
          return;
        }
      }

      if (Math.abs(totalDebit - totalCredit) > 0.001) {
        message.error('借方合计与贷方合计必须相等');
        return;
      }

      const payload = {
        voucherDate: values.voucherDate.format('YYYY-MM-DD'),
        voucherType: values.voucherType,
        remark: values.remark || '',
        items: items.map(item => ({
          subjectId: item.subjectId,
          summary: item.summary,
          debit: item.debit,
          credit: item.credit,
        })),
      };

      setConfirmLoading(true);
      try {
        if (editId) {
          await updateVoucher(editId, payload);
          message.success('修改成功');
        } else {
          await addVoucher(payload);
          message.success('新增成功');
        }
        setModalOpen(false);
        setTableKey(k => k + 1);
      } finally {
        setConfirmLoading(false);
      }
    } catch (err) {
      // Form validation error, do nothing
    }
  };

  const handleAudit = async (id: number) => {
    await auditVoucher(id);
    message.success('审核成功');
    setTableKey(k => k + 1);
  };

  const columns: ProColumns[] = [
    { title: '凭证号', dataIndex: 'voucherNo', key: 'voucherNo', width: 120, sorter: true },
    {
      title: '日期', dataIndex: 'voucherDate', key: 'voucherDate',
      search: false, valueType: 'date', width: 120,
    },
    {
      title: '类型', dataIndex: 'voucherType', key: 'voucherType',
      search: false, width: 90,
    },
    {
      title: '摘要', dataIndex: 'remark', key: 'remark',
      search: false, ellipsis: true,
    },
    {
      title: '借方合计', dataIndex: 'totalDebit', key: 'totalDebit',
      search: false, width: 120, valueType: 'money',
    },
    {
      title: '贷方合计', dataIndex: 'totalCredit', key: 'totalCredit',
      search: false, width: 120, valueType: 'money',
    },
    {
      title: '制单人', dataIndex: 'createBy', key: 'createBy',
      search: false, width: 100,
    },
    {
      title: '状态', dataIndex: 'status', key: 'status', width: 90,
      render: (_, r) => {
        const info = STATUS_MAP[r.status];
        return info ? <Tag color={info.color}>{info.text}</Tag> : <Tag>未知</Tag>;
      },
    },
    {
      title: '操作', key: 'action', search: false, fixed: 'right', width: 200,
      render: (_, record) => (
        <Space>
          {record.status === 0 && (
            <>
              <Button type="link" size="small" onClick={() => handleOpenEdit(record.id)}>编辑</Button>
              <Button type="link" size="small" onClick={() => handleAudit(record.id)}>审核</Button>
            </>
          )}
          {record.status === 1 && <Button type="link" size="small" onClick={async () => { await unauditVoucher(record.id); message.success('反审核成功'); setTableKey(k => k + 1); }}>反审核</Button>}
          <Popconfirm title="确定删除?" onConfirm={async () => {
            await deleteVoucher(record.id);
            message.success('删除成功');
            setTableKey(k => k + 1);
          }}>
            <Button type="link" size="small" danger>删除</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const itemColumns = [
    {
      title: '科目',
      dataIndex: 'subjectId',
      key: 'subjectId',
      width: 220,
      render: (_: any, record: VoucherItem) => (
        <TreeSelect
          style={{ width: '100%' }}
          treeData={subjectTree}
          fieldNames={{ label: 'name', value: 'id', children: 'children' }}
          value={record.subjectId}
          placeholder="选择科目"
          treeDefaultExpandAll
          onChange={(value) => updateItem(record.key, 'subjectId', value)}
          allowClear
          showSearch
          filterTreeNode={(input, node: any) =>
            (node.name || node.label || '').toLowerCase().includes(input.toLowerCase())
          }
        />
      ),
    },
    {
      title: '摘要',
      dataIndex: 'summary',
      key: 'summary',
      render: (_: any, record: VoucherItem) => (
        <Input
          value={record.summary}
          placeholder="请输入摘要"
          onChange={(e) => updateItem(record.key, 'summary', e.target.value)}
        />
      ),
    },
    {
      title: '借方金额',
      dataIndex: 'debit',
      key: 'debit',
      width: 140,
      render: (_: any, record: VoucherItem) => (
        <InputNumber
          style={{ width: '100%' }}
          value={record.debit}
          min={0}
          precision={2}
          placeholder="0.00"
          onChange={(value) => updateItem(record.key, 'debit', value || 0)}
        />
      ),
    },
    {
      title: '贷方金额',
      dataIndex: 'credit',
      key: 'credit',
      width: 140,
      render: (_: any, record: VoucherItem) => (
        <InputNumber
          style={{ width: '100%' }}
          value={record.credit}
          min={0}
          precision={2}
          placeholder="0.00"
          onChange={(value) => updateItem(record.key, 'credit', value || 0)}
        />
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 60,
      render: (_: any, record: VoucherItem) => (
        <Button
          type="link"
          danger
          icon={<DeleteOutlined />}
          onClick={() => removeItem(record.key)}
          disabled={items.length <= 1}
        />
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
          const data = await getVoucherList(query);
          return { data: data.records, total: data.total, success: true };
        }}
        actionRef={actionRef}
        rowKey="id"
        search={{ labelWidth: 'auto' }}
        toolBarRender={() => [
          <Button key="add" type="primary" icon={<PlusOutlined />} onClick={handleOpenAdd}>
            新增凭证
          </Button>,
        ]}
      />
      <Modal
        title={editId ? '编辑凭证' : '新增凭证'}
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={handleSubmit}
        confirmLoading={confirmLoading}
        width={960}
        destroyOnClose
      >
        <Form form={form} layout="inline" style={{ marginBottom: 16 }}>
          <Form.Item name="voucherDate" label="日期" rules={[{ required: true, message: '请选择日期' }]}>
            <DatePicker style={{ width: 160 }} />
          </Form.Item>
          <Form.Item name="voucherType" label="类型" rules={[{ required: true, message: '请选择类型' }]}>
            <Select
              style={{ width: 120 }}
              options={[
                { label: '收款', value: '收款' },
                { label: '付款', value: '付款' },
                { label: '转账', value: '转账' },
              ]}
            />
          </Form.Item>
          <Form.Item name="remark" label="摘要">
            <Input style={{ width: 280 }} placeholder="凭证摘要" />
          </Form.Item>
        </Form>

        <div style={{ marginBottom: 8 }}>
          <Space>
            <Button type="dashed" icon={<PlusOutlined />} onClick={addItem}>
              添加分录
            </Button>
          </Space>
        </div>

        <Table
          columns={itemColumns}
          dataSource={items}
          rowKey="key"
          pagination={false}
          size="small"
          bordered
          summary={() => {
            const diff = Math.abs(totalDebit - totalCredit);
            const balanced = diff < 0.001;
            return (
              <Table.Summary fixed>
                <Table.Summary.Row>
                  <Table.Summary.Cell index={0} colSpan={2}>
                    <Typography.Text strong>合计</Typography.Text>
                  </Table.Summary.Cell>
                  <Table.Summary.Cell index={2}>
                    <Typography.Text strong style={{ color: balanced ? '#52c41a' : '#ff4d4f' }}>
                      {totalDebit.toFixed(2)}
                    </Typography.Text>
                  </Table.Summary.Cell>
                  <Table.Summary.Cell index={3}>
                    <Typography.Text strong style={{ color: balanced ? '#52c41a' : '#ff4d4f' }}>
                      {totalCredit.toFixed(2)}
                    </Typography.Text>
                  </Table.Summary.Cell>
                  <Table.Summary.Cell index={4}>
                    {balanced ? (
                      <Typography.Text type="success">平衡</Typography.Text>
                    ) : (
                      <Typography.Text type="danger">差额: {diff.toFixed(2)}</Typography.Text>
                    )}
                  </Table.Summary.Cell>
                </Table.Summary.Row>
              </Table.Summary>
            );
          }}
        />
      </Modal>
    </>
  );
}
