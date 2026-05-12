import { useUserStore } from '@/stores/userStore';
import { Button, type ButtonProps } from 'antd';

interface Props extends ButtonProps {
  permission: string;
  children: React.ReactNode;
}

export default function PermissionBtn({ permission, children, ...props }: Props) {
  const hasPermission = useUserStore((s) => s.hasPermission);
  if (!hasPermission(permission)) return null;
  return <Button {...props}>{children}</Button>;
}
