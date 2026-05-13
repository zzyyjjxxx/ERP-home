import React, { useEffect } from 'react';
import ReactDOM from 'react-dom/client';
import { RouterProvider } from 'react-router-dom';
import { App } from 'antd';
import { ProConfigProvider } from '@ant-design/pro-components';
import zhCN from 'antd/locale/zh_CN';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { setGlobalMessageApi } from './utils/message';
import router from './router';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
    },
  },
});

function AppSetup() {
  const { message } = App.useApp();
  useEffect(() => {
    setGlobalMessageApi(message);
  }, [message]);
  return <RouterProvider router={router} />;
}

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <ProConfigProvider locale={zhCN}>
        <App>
          <AppSetup />
        </App>
      </ProConfigProvider>
    </QueryClientProvider>
  </React.StrictMode>
);
