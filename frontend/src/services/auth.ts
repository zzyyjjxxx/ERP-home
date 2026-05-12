import request from '@/utils/request';
import type { UserInfo } from '@/stores/userStore';

export function login(username: string, password: string): Promise<{ token: string; user: UserInfo }> {
  return request.post('/auth/login', { username, password });
}

export function getUserInfo(): Promise<UserInfo> {
  return request.get('/auth/info');
}
