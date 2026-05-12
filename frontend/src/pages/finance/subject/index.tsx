import { useState, useEffect, useRef } from 'react';
import { Table, Button, Tag, Space, message, Popconfirm, Modal, Form, Input, Select, InputNumber } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { getSubjectTree, addSubject, updateSubject, deleteSubject } from '@/services/finance';

interface SubjectRecord {
  id: number;
  code: string;
  name: string;
  subjectType: string;
  parentId: number | null;
  children?: SubjectRecord[];
}

const SUBJECT_TYPE_MAP: Record<string, { text: string; color: string }> = {
  ASSET: { text: '资产', color: 'blue' },
  LIABILITY: { text: '负债', color: 'red' },
  EQUITY: { text: '权益', color: 'green' },
  INCOME: { text: '收入', color: 'orange' },
  EXPENSE: { text: '费用', color: 'purple' },
};

export default function SubjectList() {
  const [data, setData] = useState<SubjectRecord[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<SubjectRecord | null>(null);
  const [parentRecord, setParentRecord] = useState<SubjectRecord | null>(null);
  const [form] = Form.useForm();

  const loadData = async () => {
    setLoading(true);
    try {
      const tree = await getSubjectTree();
      setData(tree || []);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleAdd = () => {
    setEditRecord(null);
    setParentRecord(null);
    form.resetFields();
    form.setFieldsValue({ subjectType: 'ASSET' });
    setModalOpen(true);
  };

  const handleAddChild = (parent: SubjectRecord) => {
    setEditRecord(null);
    setParentRecord(parent);
    form.resetFields();
    form.setFieldsValue({ parentId: parent.id, subjectType: 'ASSET' });
    setModalOpen(true);
  };

  const handleEdit = (record: SubjectRecord) => {
    setEditRecord(record);
    setParentRecord(null);
    form.setFieldsValue(record);
    setModalOpen(true);
  };

  const handleDelete = async (id: number) => {
    await deleteSubject(id);
    message.success('删除成功');
    loadData();
  };

  const handleSubmit = async () => {
    const values = await form.validateFields();
    if (editRecord) {
      await updateSubject(editRecord.id, values);
      message.success('修改成功');
    } else {
      await addSubject(values);
      message.success('新增成功');
    }
    setModalOpen(false);
    loadData();
  };

  const columns: ColumnsType<SubjectRecord> = [
    { title: '编码', dataIndex: 'code', key: 'code', width: 150 },
    { title: '名称', dataIndex: 'name', key: 'name', width: 200 },
    {
      title: '类型', dataIndex: 'subjectType', key: 'subjectType', width: 100,
      render: (type: string) => {
        const info = SUBJECT_TYPE_MAP[type];
        return info ? <Tag color={info.color}>{info.text}</Tag> : <Tag>{type}</Tag>;
      },
    },
    { title: '排序', dataIndex: 'sort', key: 'sort', width: 80 },
    {
      title: '操作', key: 'action', width: 240,
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" onClick={() => handleAddChild(record)}>新增子级</Button>
          <Button type="link" size="small" onClick={() => handleEdit(record)}>编辑</Button>
          <Popconfirm title="确定删除该科目及其子级?" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" size="small" danger>删除</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div style={{ marginBottom: 16 }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增科目
        </Button>
      </div>
      <Table
        columns={columns}
        dataSource={data}
        rowKey="id"
        loading={loading}
        pagination={false}
        defaultExpandAllRows
        expandable={{ childrenColumnName: 'children' }}
      />
      <Modal
        title={editRecord ? '编辑科目' : (parentRecord ? `新增子级科目 - 父级: ${parentRecord.name}` : '新增科目')}
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={handleSubmit}
        destroyOnClose
        width={500}
      >
        <Form form={form} layout="vertical" style={{ marginTop: 16 }}>
          <Form.Item name="code" label="编码" rules={[{ required: true, message: '请输入编码' }]}>
            <Input placeholder="请输入科目编码" />
          </Form.Item>
          <Form.Item name="name" label="名称" rules={[{ required: true, message: '请输入名称' }]}>
            <Input placeholder="请输入科目名称" />
          </Form.Item>
          <Form.Item name="subjectType" label="类型" rules={[{ required: true, message: '请选择类型' }]}>
            <Select
              options={[
                { label: '资产', value: 'ASSET' },
                { label: '负债', value: 'LIABILITY' },
                { label: '权益', value: 'EQUITY' },
                { label: '收入', value: 'INCOME' },
                { label: '费用', value: 'EXPENSE' },
              ]}
            />
          </Form.Item>
          {parentRecord && (
            <Form.Item name="parentId" label="父级ID" hidden>
              <InputNumber />
            </Form.Item>
          )}
          {!parentRecord && !editRecord && (
            <Form.Item name="parentId" label="父级ID">
              <InputNumber placeholder="留空则为根节点" style={{ width: '100%' }} />
            </Form.Item>
          )}
          <Form.Item name="sort" label="排序">
            <InputNumber placeholder="排序号" style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
