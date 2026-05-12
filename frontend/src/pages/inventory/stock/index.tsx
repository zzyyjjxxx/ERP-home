import { useRef } from 'react';
import { ProTable } from '@ant-design/pro-components';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { Tabs } from 'antd';
import { getStockList, getStockFlow } from '@/services/inventory';

function StockTable() {
  const actionRef = useRef<ActionType>();
  const columns: ProColumns[] = [
    { title: '商品ID', dataIndex: 'productId', key: 'productId' },
    { title: '仓库ID', dataIndex: 'warehouseId', key: 'warehouseId' },
    { title: '库存数量', dataIndex: 'quantity', key: 'quantity', search: false },
    { title: '锁定数量', dataIndex: 'lockedQty', key: 'lockedQty', search: false },
  ];
  return (
    <ProTable columns={columns} request={async (params) => { const data = await getStockList(params); return { data: data.records, total: data.total, success: true }; }}
      actionRef={actionRef} rowKey="id" search={{ labelWidth: 'auto' }} />
  );
}

function StockFlowTable() {
  const actionRef = useRef<ActionType>();
  const columns: ProColumns[] = [
    { title: '商品ID', dataIndex: 'productId', key: 'productId' },
    { title: '仓库ID', dataIndex: 'warehouseId', key: 'warehouseId', search: false },
    { title: '业务类型', dataIndex: 'bizType', key: 'bizType' },
    { title: '业务单号', dataIndex: 'bizNo', key: 'bizNo', search: false },
    { title: '变更前', dataIndex: 'beforeQty', key: 'beforeQty', search: false },
    { title: '变更量', dataIndex: 'changeQty', key: 'changeQty', search: false },
    { title: '变更后', dataIndex: 'afterQty', key: 'afterQty', search: false },
    { title: '时间', dataIndex: 'createTime', key: 'createTime', search: false, valueType: 'dateTime' },
  ];
  return (
    <ProTable columns={columns} request={async (params) => { const data = await getStockFlow(params); return { data: data.records, total: data.total, success: true }; }}
      actionRef={actionRef} rowKey="id" search={{ labelWidth: 'auto' }} />
  );
}

export default function StockPage() {
  return (
    <Tabs items={[
      { key: 'stock', label: '库存查询', children: <StockTable /> },
      { key: 'flow', label: '库存流水', children: <StockFlowTable /> },
    ]} />
  );
}
