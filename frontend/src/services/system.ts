import request from '@/utils/request';

export function getUserList(params: Record<string, unknown>): Promise<{ records: unknown[]; total: number }> {
  return request.get('/system/user/list', { params });
}

export function addUser(data: Record<string, unknown>): Promise<unknown> {
  return request.post('/system/user', data);
}

export function updateUser(id: number, data: Record<string, unknown>): Promise<unknown> {
  return request.put(`/system/user/${id}`, data);
}

export function deleteUser(id: number): Promise<unknown> {
  return request.delete(`/system/user/${id}`);
}

export function assignUserRoles(id: number, roleIds: number[]): Promise<unknown> {
  return request.put(`/system/user/${id}/roles`, roleIds);
}

export function getRoleList(): Promise<unknown[]> {
  return request.get('/system/role/list');
}

export function addRole(data: Record<string, unknown>): Promise<unknown> {
  return request.post('/system/role', data);
}

export function updateRole(id: number, data: Record<string, unknown>): Promise<unknown> {
  return request.put(`/system/role/${id}`, data);
}

export function deleteRole(id: number): Promise<unknown> {
  return request.delete(`/system/role/${id}`);
}

export function getRoleMenus(id: number): Promise<number[]> {
  return request.get(`/system/role/${id}/menus`);
}

export function assignRoleMenus(id: number, menuIds: number[]): Promise<unknown> {
  return request.put(`/system/role/${id}/menus`, menuIds);
}

export function getMenuTree(): Promise<unknown[]> {
  return request.get('/system/menu/tree');
}

export function addMenu(data: Record<string, unknown>): Promise<unknown> {
  return request.post('/system/menu', data);
}

export function updateMenu(id: number, data: Record<string, unknown>): Promise<unknown> {
  return request.put(`/system/menu/${id}`, data);
}

export function deleteMenu(id: number): Promise<unknown> {
  return request.delete(`/system/menu/${id}`);
}
