import request from '@/utils/request';

export function getUserList(params: any) {
  return request.get('/system/user/list', { params });
}

export function addUser(data: any) {
  return request.post('/system/user', data);
}

export function updateUser(id: number, data: any) {
  return request.put(`/system/user/${id}`, data);
}

export function deleteUser(id: number) {
  return request.delete(`/system/user/${id}`);
}

export function assignUserRoles(id: number, roleIds: number[]) {
  return request.put(`/system/user/${id}/roles`, roleIds);
}

export function getRoleList() {
  return request.get('/system/role/list');
}

export function addRole(data: any) {
  return request.post('/system/role', data);
}

export function updateRole(id: number, data: any) {
  return request.put(`/system/role/${id}`, data);
}

export function deleteRole(id: number) {
  return request.delete(`/system/role/${id}`);
}

export function getRoleMenus(id: number) {
  return request.get(`/system/role/${id}/menus`);
}

export function assignRoleMenus(id: number, menuIds: number[]) {
  return request.put(`/system/role/${id}/menus`, menuIds);
}

export function getMenuTree() {
  return request.get('/system/menu/tree');
}

export function addMenu(data: any) {
  return request.post('/system/menu', data);
}

export function updateMenu(id: number, data: any) {
  return request.put(`/system/menu/${id}`, data);
}

export function deleteMenu(id: number) {
  return request.delete(`/system/menu/${id}`);
}
