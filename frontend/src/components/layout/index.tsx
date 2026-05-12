import { useEffect, useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Layout, Menu, Button, Dropdown, Avatar } from 'antd';
import {
  MenuFoldOutlined, MenuUnfoldOutlined, UserOutlined, LogoutOutlined,
  SettingOutlined, ShoppingOutlined, InboxOutlined, ShopOutlined, DollarOutlined, ToolOutlined, DashboardOutlined,
} from '@ant-design/icons';
import type { MenuProps } from 'antd';
import { useUserStore } from '@/stores/userStore';
import { getUserInfo } from '@/services/auth';

const { Header, Sider, Content } = Layout;

const menuItems: MenuProps['items'] = [
  { key: '/dashboard', icon: <DashboardOutlined />, label: '首页' },
  { key: '/system', icon: <SettingOutlined />, label: '系统管理', children: [
    { key: '/system/user', label: '用户管理' },
    { key: '/system/role', label: '角色管理' },
    { key: '/system/menu', label: '菜单管理' },
    { key: '/system/dict', label: '数据字典' },
  ]},
  { key: '/purchase', icon: <ShoppingOutlined />, label: '采购管理', children: [
    { key: '/purchase/supplier', label: '供应商管理' },
    { key: '/purchase/order', label: '采购订单' },
    { key: '/purchase/receipt', label: '采购收货' },
    { key: '/purchase/return', label: '采购退货' },
  ]},
  { key: '/inventory', icon: <InboxOutlined />, label: '库存管理', children: [
    { key: '/inventory/product', label: '商品管理' },
    { key: '/inventory/warehouse', label: '仓库管理' },
    { key: '/inventory/stock', label: '库存查询' },
    { key: '/inventory/transfer', label: '调拨管理' },
    { key: '/inventory/check', label: '盘点管理' },
  ]},
  { key: '/sales', icon: <ShopOutlined />, label: '销售管理', children: [
    { key: '/sales/customer', label: '客户管理' },
    { key: '/sales/order', label: '销售订单' },
    { key: '/sales/delivery', label: '销售发货' },
  ]},
  { key: '/finance', icon: <DollarOutlined />, label: '财务管理', children: [
    { key: '/finance/subject', label: '会计科目' },
    { key: '/finance/voucher', label: '凭证管理' },
    { key: '/finance/receivable', label: '应收账款' },
    { key: '/finance/payable', label: '应付账款' },
  ]},
  { key: '/production', icon: <ToolOutlined />, label: '生产管理', children: [
    { key: '/production/bom', label: 'BOM管理' },
    { key: '/production/workorder', label: '工单管理' },
    { key: '/production/planning', label: '生产计划' },
    { key: '/production/qc', label: '质检管理' },
  ]},
];

export default function MainLayout() {
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const { user, setUser, logout } = useUserStore();

  useEffect(() => {
    if (!user) {
      getUserInfo().then(setUser).catch(() => navigate('/login'));
    }
  }, []);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const currentOpenKeys = menuItems
    .filter(item => item && 'children' in item && (item as any).children?.some((c: any) => location.pathname.startsWith(c.key)))
    .map(item => item!.key as string);

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider trigger={null} collapsible collapsed={collapsed}>
        <div style={{ height: 48, margin: 16, color: '#fff', textAlign: 'center', lineHeight: '48px', fontWeight: 'bold', fontSize: collapsed ? 14 : 18 }}>
          {collapsed ? 'ERP' : 'ERP 管理系统'}
        </div>
        <Menu
          theme="dark" mode="inline"
          selectedKeys={[location.pathname]}
          defaultOpenKeys={currentOpenKeys}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <Layout>
        <Header style={{ padding: '0 24px', background: '#fff', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Button type="text" icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />} onClick={() => setCollapsed(!collapsed)} />
          <Dropdown menu={{ items: [{ key: 'logout', icon: <LogoutOutlined />, label: '退出登录' }], onClick: handleLogout }}>
            <span style={{ cursor: 'pointer' }}>
              <Avatar size="small" icon={<UserOutlined />} style={{ marginRight: 8 }} />
              {user?.realName || user?.username}
            </span>
          </Dropdown>
        </Header>
        <Content style={{ margin: 24, padding: 24, background: '#fff', borderRadius: 8, minHeight: 280 }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
