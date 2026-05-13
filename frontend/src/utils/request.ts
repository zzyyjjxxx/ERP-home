import axios from 'axios';
import { showError } from './message';

const instance = axios.create({
  baseURL: '/api',
  timeout: 30000,
});

instance.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

instance.interceptors.response.use(
  (response) => {
    const { code, message: msg, data } = response.data;
    if (code === 200) return data;
    if (code === 401) {
      localStorage.clear();
      window.location.href = '/login';
      return Promise.reject(new Error(msg));
    }
    return Promise.reject(new Error(msg || '请求失败'));
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.clear();
      window.location.href = '/login';
    }
    const msg = error.response?.data?.message || '网络异常';
    showError(msg);
    return Promise.reject(new Error(msg));
  }
);

interface RequestConfig {
  params?: Record<string, unknown>;
  [key: string]: unknown;
}

const request = {
  get: <T = unknown>(url: string, config?: RequestConfig): Promise<T> =>
    instance.get(url, config) as Promise<T>,
  post: <T = unknown>(url: string, data?: unknown, config?: RequestConfig): Promise<T> =>
    instance.post(url, data, config) as Promise<T>,
  put: <T = unknown>(url: string, data?: unknown, config?: RequestConfig): Promise<T> =>
    instance.put(url, data, config) as Promise<T>,
  delete: <T = unknown>(url: string, config?: RequestConfig): Promise<T> =>
    instance.delete(url, config) as Promise<T>,
};

export default request;
