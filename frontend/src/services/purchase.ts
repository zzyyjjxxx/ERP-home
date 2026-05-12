import request from '@/utils/request';

export function getSupplierList(params: any) {
  return request.get('/purchase/supplier/list', { params });
}

export function getAllSuppliers() {
  return request.get('/purchase/supplier/all');
}

export function addSupplier(data: any) {
  return request.post('/purchase/supplier', data);
}

export function updateSupplier(id: number, data: any) {
  return request.put(`/purchase/supplier/${id}`, data);
}

export function deleteSupplier(id: number) {
  return request.delete(`/purchase/supplier/${id}`);
}

export function getOrderList(params: any) {
  return request.get('/purchase/order/list', { params });
}

export function getOrderDetail(id: number) {
  return request.get(`/purchase/order/${id}`);
}

export function getOrderItems(id: number) {
  return request.get(`/purchase/order/${id}/items`);
}

export function addOrder(data: any) {
  return request.post('/purchase/order', data);
}

export function auditOrder(id: number) {
  return request.put(`/purchase/order/${id}/audit`);
}

export function cancelOrder(id: number) {
  return request.put(`/purchase/order/${id}/cancel`);
}

export function deleteOrder(id: number) {
  return request.delete(`/purchase/order/${id}`);
}

export function getReceiptList(params: any) {
  return request.get('/purchase/receipt/list', { params });
}

export function getReceiptItems(id: number) {
  return request.get(`/purchase/receipt/${id}/items`);
}

export function addReceipt(data: any) {
  return request.post('/purchase/receipt', data);
}

export function updateReceipt(id: number, data: any) {
  return request.put(`/purchase/receipt/${id}`, data);
}

export function deleteReceipt(id: number) {
  return request.delete(`/purchase/receipt/${id}`);
}

export function auditReceipt(id: number) {
  return request.put(`/purchase/receipt/${id}/audit`);
}

export function getReturnList(params: any) {
  return request.get('/purchase/return/list', { params });
}

export function addReturn(data: any) {
  return request.post('/purchase/return', data);
}

export function updateReturn(id: number, data: any) {
  return request.put(`/purchase/return/${id}`, data);
}

export function deleteReturn(id: number) {
  return request.delete(`/purchase/return/${id}`);
}

export function completeReturn(id: number) {
  return request.put(`/purchase/return/${id}/complete`);
}
