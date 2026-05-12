import { useRef, useState } from 'react';
import { ProTable, ModalForm, ProFormSelect, ProFormDigit, ProFormDatePicker, ProFormTextArea } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Button, Tag, message, Popconfirm, Space } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getQcList, addQc, updateQc, deleteQc } from '@/services/production';
import { getAllProducts } from '@/services/inventory';

export default function QcList() {
  const actionRef = useRef<ActionType>();
  const [modalOpen, setModalOpen] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);

  const columns: ProColumns[] = [
    { title: '质检单号', dataIndex: 'qcNo', key: 'qcNo' },
    { title: '工单号', dataIndex: 'workOrderNo', key: 'workOrderNo' },
    { title: '产品', dataIndex: 'productName', key: 'productName' },
    { title: '检验数', dataIndex: 'checkQty', key: 'checkQty', search: false },
    { title: '合格数', dataIndex: 'passQty', key: 'passQty', search: false },
    { title: '不合格数', dataIndex: 'failQty', key: 'failQty', search: false },
    {
      title: '结果', dataIndex: 'result', key: 'result',
      valueEnum: {
        PASS: { text: '合格', status: 'Success' },
        FAIL: { text: '不合格', status: 'Error' },
        PARTIAL: { text: '部分合格', status: 'Warning' },
      },
      render: (_, record) => {
        const resultMap: Record<string, { text: string; color: string }> = {
          PASS: { text: '合格', color: 'green' },
          FAIL: { text: '不合格', color: 'red' },
          PARTIAL: { text: '部分合格', color: 'orange' },
        };
        const r = resultMap[record.result] || { text: '未知', color: 'default' };
        return <Tag color={r.color}>{r.text}</Tag>;
      },
    },
    { title: '检验人', dataIndex: 'checker', key: 'checker', search: false },
    { title: '检验日期', dataIndex: 'checkDate', key: 'checkDate', search: false, valueType: 'date' },
    {
      title: '操作', key: 'action', search: false, fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button type="link" onClick={() => {
            setEditRecord(record);
            setModalOpen(true);
          }}>编辑</Button>
          <Popconfirm title="确定删除?" onConfirm={async () => {
            await deleteQc(record.id);
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
          const data = await getQcList(params);
          return { data: data.records, total: data.total, success: true };
        }}
        actionRef={actionRef}
        rowKey="id"
        search={{ labelWidth: 'auto' }}
        toolBarRender={() => [
          <Button key="add" type="primary" icon={<PlusOutlined />} onClick={() => {
            setEditRecord(null);
            setModalOpen(true);
          }}>新增质检单</Button>,
        ]}
      />
      <ModalForm
        title={editRecord ? '编辑质检单' : '新增质检单'}
        open={modalOpen}
        onOpenChange={setModalOpen}
        initialValues={editRecord}
        width={500}
        onFinish={async (values) => {
          if (editRecord) {
            await updateQc(editRecord.id, values);
          } else {
            await addQc(values);
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
        <ProFormDigit name="checkQty" label="检验数量" min={1} />
        <ProFormDigit name="passQty" label="合格数量" min={0} />
        <ProFormDigit name="failQty" label="不合格数量" min={0} />
        <ProFormSelect
          name="result"
          label="检验结果"
          options={[
            { label: '合格', value: 'PASS' },
            { label: '不合格', value: 'FAIL' },
            { label: '部分合格', value: 'PARTIAL' },
          ]}
        />
        <ProFormDatePicker name="checkDate" label="检验日期" />
        <ProFormTextArea name="remark" label="备注" />
      </ModalForm>
    </>
  );
}
