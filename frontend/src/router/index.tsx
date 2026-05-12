import { createBrowserRouter, Navigate } from 'react-router-dom';
import Login from '@/pages/login';
import Layout from '@/components/layout';
import Dashboard from '@/pages/dashboard';
import UserList from '@/pages/system/user';
import RoleList from '@/pages/system/role';
import MenuList from '@/pages/system/menu';
import DictManage from '@/pages/system/dict';
import SupplierList from '@/pages/purchase/supplier';
import OrderList from '@/pages/purchase/order';
import ReceiptList from '@/pages/purchase/receipt';
import PurchaseReturnList from '@/pages/purchase/return';
import ProductList from '@/pages/inventory/product';
import WarehouseList from '@/pages/inventory/warehouse';
import StockPage from '@/pages/inventory/stock';
import CustomerList from '@/pages/sales/customer';
import SalesOrderList from '@/pages/sales/order';
import DeliveryList from '@/pages/sales/delivery';
import SubjectList from '@/pages/finance/subject';
import VoucherList from '@/pages/finance/voucher';
import ReceivableList from '@/pages/finance/receivable';
import PayableList from '@/pages/finance/payable';
import BomList from '@/pages/production/bom';
import WorkOrderList from '@/pages/production/workorder';
import PlanningList from '@/pages/production/planning';
import QcList from '@/pages/production/qc';

const router = createBrowserRouter([
  { path: '/login', element: <Login /> },
  {
    path: '/',
    element: <Layout />,
    children: [
      { index: true, element: <Navigate to="/dashboard" replace /> },
      { path: 'dashboard', element: <Dashboard /> },
      { path: 'system/user', element: <UserList /> },
      { path: 'system/role', element: <RoleList /> },
      { path: 'system/menu', element: <MenuList /> },
      { path: 'system/dict', element: <DictManage /> },
      { path: 'purchase/supplier', element: <SupplierList /> },
      { path: 'purchase/order', element: <OrderList /> },
      { path: 'purchase/receipt', element: <ReceiptList /> },
      { path: 'purchase/return', element: <PurchaseReturnList /> },
      { path: 'inventory/product', element: <ProductList /> },
      { path: 'inventory/warehouse', element: <WarehouseList /> },
      { path: 'inventory/stock', element: <StockPage /> },
      { path: 'sales/customer', element: <CustomerList /> },
      { path: 'sales/order', element: <SalesOrderList /> },
      { path: 'sales/delivery', element: <DeliveryList /> },
      { path: 'finance/subject', element: <SubjectList /> },
      { path: 'finance/voucher', element: <VoucherList /> },
      { path: 'finance/receivable', element: <ReceivableList /> },
      { path: 'finance/payable', element: <PayableList /> },
      { path: 'production/bom', element: <BomList /> },
      { path: 'production/workorder', element: <WorkOrderList /> },
      { path: 'production/planning', element: <PlanningList /> },
      { path: 'production/qc', element: <QcList /> },
    ],
  },
]);

export default router;
