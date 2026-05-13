import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Form, Input, Button, Checkbox, App } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { login } from '@/services/auth';
import { useUserStore } from '@/stores/userStore';
import './style.css';

export default function Login() {
  const { message } = App.useApp();
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { setToken, setUser } = useUserStore();

  const onFinish = async (values: { username: string; password: string }) => {
    setLoading(true);
    try {
      const data = await login(values.username, values.password);
      setToken(data.token);
      setUser(data.user);
      message.success('登录成功');
      navigate('/', { replace: true });
    } catch (err: any) {
      message.error(err?.message || '登录失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-brand">
        <div className="brand-dot-1" />
        <div className="brand-dot-2" />
        <div className="brand-content">
          <div className="brand-logo">
            <svg viewBox="0 0 72 72" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="4" y="4" width="28" height="28" rx="8" fill="url(#grad1)" />
              <rect x="38" y="4" width="28" height="28" rx="8" fill="url(#grad2)" />
              <rect x="4" y="38" width="28" height="28" rx="8" fill="url(#grad3)" />
              <rect x="38" y="38" width="28" height="28" rx="8" fill="url(#grad4)" />
              <defs>
                <linearGradient id="grad1" x1="0" y1="0" x2="1" y2="1">
                  <stop stop-color="#3b82f6"/><stop offset="1" stop-color="#60a5fa"/>
                </linearGradient>
                <linearGradient id="grad2" x1="0" y1="0" x2="1" y2="1">
                  <stop stop-color="#2563eb"/><stop offset="1" stop-color="#3b82f6"/>
                </linearGradient>
                <linearGradient id="grad3" x1="0" y1="0" x2="1" y2="1">
                  <stop stop-color="#4f46e5"/><stop offset="1" stop-color="#6366f1"/>
                </linearGradient>
                <linearGradient id="grad4" x1="0" y1="0" x2="1" y2="1">
                  <stop stop-color="#60a5fa"/><stop offset="1" stop-color="#93c5fd"/>
                </linearGradient>
              </defs>
            </svg>
          </div>
          <h1 className="brand-title">ERP 管理系统</h1>
          <p className="brand-subtitle">企业资源计划管理平台</p>
          <div className="brand-hint">
            <p>欢迎使用<br />请输入您的账号和密码登录系统</p>
          </div>
        </div>
      </div>

      <div className="login-form-wrapper">
        <div className="login-form-panel">
          <h3>用户登录</h3>
          <p className="form-subtitle">请输入您的账号信息</p>

          <Form onFinish={onFinish} size="large" initialValues={{ remember: true }}>
            <Form.Item name="username" rules={[{ required: true, message: '请输入用户名' }]}>
              <Input prefix={<UserOutlined />} placeholder="请输入用户名" />
            </Form.Item>

            <Form.Item name="password" rules={[{ required: true, message: '请输入密码' }]}>
              <Input.Password prefix={<LockOutlined />} placeholder="请输入密码" />
            </Form.Item>

            <div className="form-extra">
              <Form.Item name="remember" valuePropName="checked" noStyle>
                <Checkbox>记住账号</Checkbox>
              </Form.Item>
              <span className="forgot-link">忘记密码？</span>
            </div>

            <Form.Item>
              <Button type="primary" htmlType="submit" loading={loading} className="login-btn">
                登 录
              </Button>
            </Form.Item>
          </Form>

          <p className="form-footer">
            没有账号？<a>联系管理员</a>
          </p>
        </div>
      </div>
    </div>
  );
}
