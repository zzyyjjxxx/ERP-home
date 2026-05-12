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

export function getWarehouseList() {
  return request.get('/inventory/warehouse/list');
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
