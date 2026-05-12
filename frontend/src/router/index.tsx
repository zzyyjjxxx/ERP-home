import { createBrowserRouter, Navigate } from 'react-router-dom';
import Login from '@/pages/login';
import Layout from '@/components/layout';
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

const router = createBrowserRouter([
  { path: '/login', element: <Login /> },
  {
    path: '/',
    element: <Layout />,
    children: [
      { index: true, element: <Navigate to="/system/user" replace /> },
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
    ],
  },
]);

export default router;
