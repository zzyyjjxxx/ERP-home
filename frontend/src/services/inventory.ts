import request from '@/utils/request';

export function getProductList(params: any) {
  return request.get('/inventory/product/list', { params });
}

export function getAllProducts() {
  return request.get('/inventory/product/all');
}

export function addProduct(data: any) {
  return request.post('/inventory/product', data);
}

export function updateProduct(id: number, data: any) {
  return request.put(`/inventory/product/${id}`, data);
}

export function deleteProduct(id: number) {
  return request.delete(`/inventory/product/${id}`);
}

export function getWarehouseList(params?: any) {
  return request.get('/inventory/warehouse/list', { params });
}

export function addWarehouse(data: any) {
  return request.post('/inventory/warehouse', data);
}

export function updateWarehouse(id: number, data: any) {
  return request.put(`/inventory/warehouse/${id}`, data);
}

export function deleteWarehouse(id: number) {
  return request.delete(`/inventory/warehouse/${id}`);
}

export function getStockList(params: any) {
  return request.get('/inventory/stock/list', { params });
}

export function getStockFlow(params: any) {
  return request.get('/inventory/stock/flow', { params });
}

// Category
export function getCategoryList(params?: any) {
  return request.get('/inventory/category/list', { params });
}
export function getCategoryTree() {
  return request.get('/inventory/category/tree');
}
export function addCategory(data: any) {
  return request.post('/inventory/category', data);
}
export function updateCategory(id: number, data: any) {
  return request.put('/inventory/category/' + id, data);
}
export function deleteCategory(id: number) {
  return request.delete('/inventory/category/' + id);
}

// Unit
export function getUnitList(params?: any) {
  return request.get('/inventory/unit/list', { params });
}
export function addUnit(data: any) {
  return request.post('/inventory/unit', data);
}
export function updateUnit(id: number, data: any) {
  return request.put('/inventory/unit/' + id, data);
}
export function deleteUnit(id: number) {
  return request.delete('/inventory/unit/' + id);
}

// Transfer
export function getTransferList(params: any) {
  return request.get('/inventory/transfer/list', { params });
}
export function addTransfer(data: any) {
  return request.post('/inventory/transfer', data);
}
export function updateTransfer(id: number, data: any) {
  return request.put('/inventory/transfer/' + id, data);
}
export function deleteTransfer(id: number) {
  return request.delete('/inventory/transfer/' + id);
}
export function auditTransfer(id: number) {
  return request.put('/inventory/transfer/' + id + '/audit');
}
export function unauditTransfer(id: number) {
  return request.put('/inventory/transfer/' + id + '/unaudit');
}

// Check
export function getCheckList(params: any) {
  return request.get('/inventory/check/list', { params });
}
export function addCheck(data: any) {
  return request.post('/inventory/check', data);
}
export function updateCheck(id: number, data: any) {
  return request.put('/inventory/check/' + id, data);
}
export function deleteCheck(id: number) {
  return request.delete('/inventory/check/' + id);
}
export function auditCheck(id: number) {
  return request.put('/inventory/check/' + id + '/audit');
}
export function unauditCheck(id: number) {
  return request.put('/inventory/check/' + id + '/unaudit');
}
