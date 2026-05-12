import { useRef, useState } from 'react';
import { ProTable, ModalForm, ProFormText, ProFormSelect, ProFormDatePicker } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Button, Tag, message, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getVoucherList, addVoucher, deleteVoucher } from '@/services/finance';

export default function VoucherList() {
  const actionRef = useRef<ActionType>();
  const [modalOpen, setModalOpen] = useState(false);

  const columns: ProColumns[] = [
    { title: '凭证号', dataIndex: 'voucherNo', key: 'voucherNo' },
    { title: '日期', dataIndex: 'voucherDate', key: 'voucherDate', search: false, valueType: 'date' },
    { title: '类型', dataIndex: 'voucherType', key: 'voucherType', search: false },
    { title: '借方', dataIndex: 'totalDebit', key: 'totalDebit', search: false },
    { title: '贷方', dataIndex: 'totalCredit', key: 'totalCredit', search: false },
    { title: '状态', dataIndex: 'status', key: 'status', render: (_, r) => {
      const m: Record<number, string> = { 0: '草稿', 1: '已审', 2: '过账', 3: '作废' };
      return <Tag>{m[r.status] || '未知'}</Tag>;
    }},
    { title: '操作', key: 'action', search: false,
      render: (_, record) => (
        <Popconfirm title="确定删除?" onConfirm={async () => { await deleteVoucher(record.id); message.success('删除成功'); actionRef.current?.reload(); }}>
          <Button type="link" danger>删除</Button>
        </Popconfirm>
      ),
    },
  ];

  return (
    <>
      <ProTable columns={columns} request={async (params) => { const data = await getVoucherList(params); return { data: data.records, total: data.total, success: true }; }}
        actionRef={actionRef} rowKey="id" search={{ labelWidth: 'auto' }}
        toolBarRender={() => [<Button key="add" type="primary" icon={<PlusOutlined />} onClick={() => setModalOpen(true)}>新增凭证</Button>]} />
      <ModalForm title="新增凭证" open={modalOpen} onOpenChange={setModalOpen}
        onFinish={async (values) => { await addVoucher(values); message.success('新增成功'); actionRef.current?.reload(); return true; }}>
        <ProFormDatePicker name="voucherDate" label="日期" rules={[{ required: true }]} />
        <ProFormSelect name="voucherType" label="类型" options={[{ label: '收款', value: '收款' }, { label: '付款', value: '付款' }, { label: '转账', value: '转账' }]} />
        <ProFormText name="remark" label="摘要" />
      </ModalForm>
    </>
  );
}
