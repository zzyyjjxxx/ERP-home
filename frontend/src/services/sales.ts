import request from '@/utils/request';

export function getCustomerList(params: any) {
  return request.get('/sales/customer/list', { params });
}

export function getAllCustomers() {
  return request.get('/sales/customer/all');
}

export function addCustomer(data: any) {
  return request.post('/sales/customer', data);
}

export function updateCustomer(id: number, data: any) {
  return request.put(`/sales/customer/${id}`, data);
}

export function deleteCustomer(id: number) {
  return request.delete(`/sales/customer/${id}`);
}

export function getOrderList(params: any) {
  return request.get('/sales/order/list', { params });
}

export function getOrderItems(id: number) {
  return request.get(`/sales/order/${id}/items`);
}

export function addOrder(data: any) {
  return request.post('/sales/order', data);
}

export function auditOrder(id: number) {
  return request.put(`/sales/order/${id}/audit`);
}

export function cancelOrder(id: number) {
  return request.put(`/sales/order/${id}/cancel`);
}

export function deleteOrder(id: number) {
  return request.delete(`/sales/order/${id}`);
}

export function getDeliveryList(params: any) {
  return request.get('/sales/delivery/list', { params });
}

export function addDelivery(data: any) {
  return request.post('/sales/delivery', data);
}

export function auditDelivery(id: number) {
  return request.put(`/sales/delivery/${id}/audit`);
}
