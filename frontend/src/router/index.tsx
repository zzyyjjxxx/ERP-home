import { createBrowserRouter, Navigate } from 'react-router-dom';
import Login from '@/pages/login';
import Layout from '@/components/layout';

const router = createBrowserRouter([
  { path: '/login', element: <Login /> },
  {
    path: '/',
    element: <Layout />,
    children: [
      { index: true, element: <Navigate to="/system/user" replace /> },
    ],
  },
]);

export default router;
