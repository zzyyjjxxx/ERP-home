import { useEffect, useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Layout, Menu, Button, Dropdown, Avatar } from 'antd';
import {
  MenuFoldOutlined, MenuUnfoldOutlined, UserOutlined, LogoutOutlined,
  SettingOutlined,
} from '@ant-design/icons';
import { useUserStore } from '@/stores/userStore';
import { getUserInfo } from '@/services/auth';

const { Header, Sider, Content } = Layout;

const menuItems = [
  { key: '/system', icon: <SettingOutlined />, label: '系统管理', children: [
    { key: '/system/user', label: '用户管理' },
    { key: '/system/role', label: '角色管理' },
    { key: '/system/menu', label: '菜单管理' },
    { key: '/system/dict', label: '数据字典' },
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

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider trigger={null} collapsible collapsed={collapsed}>
        <div style={{ height: 48, margin: 16, color: '#fff', textAlign: 'center', lineHeight: '48px', fontWeight: 'bold' }}>
          {collapsed ? 'ERP' : 'ERP 管理系统'}
        </div>
        <Menu
          theme="dark" mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <Layout>
        <Header style={{ padding: '0 24px', background: '#fff', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Button type="text" icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />} onClick={() => setCollapsed(!collapsed)} />
          <Dropdown menu={{ items: [{ key: 'logout', icon: <LogoutOutlined />, label: '退出登录', onClick: handleLogout }] }}>
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
