import { useUserStore } from '@/stores/userStore';
import { Button, ButtonProps } from 'antd';

interface Props extends ButtonProps {
  permission: string;
}

export default function PermissionBtn({ permission, children, ...props }: Props) {
  const hasPermission = useUserStore((s) => s.hasPermission);
  if (!hasPermission(permission)) return null;
  return <Button {...props}>{children}</Button>;
}
