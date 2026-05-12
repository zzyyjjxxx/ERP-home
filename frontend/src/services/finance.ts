import request from '@/utils/request';

// Account Subject
export function getSubjectTree(): Promise<any[]> {
  return request.get('/finance/subject/tree');
}

export function addSubject(data: any): Promise<any> {
  return request.post('/finance/subject', data);
}

export function updateSubject(id: number, data: any): Promise<any> {
  return request.put(`/finance/subject/${id}`, data);
}

export function deleteSubject(id: number): Promise<any> {
  return request.delete(`/finance/subject/${id}`);
}

// Voucher
export function getVoucherList(params: any): Promise<{ records: any[]; total: number }> {
  return request.get('/finance/voucher/list', { params });
}

export function getVoucherDetail(id: number): Promise<any> {
  return request.get(`/finance/voucher/${id}`);
}

export function addVoucher(data: any): Promise<any> {
  return request.post('/finance/voucher', data);
}

export function updateVoucher(id: number, data: any): Promise<any> {
  return request.put(`/finance/voucher/${id}`, data);
}

export function deleteVoucher(id: number): Promise<any> {
  return request.delete(`/finance/voucher/${id}`);
}

export function auditVoucher(id: number): Promise<any> {
  return request.put(`/finance/voucher/${id}/audit`);
}

// Receivable
export function getReceivableList(params: any): Promise<{ records: any[]; total: number }> {
  return request.get('/finance/receivable/list', { params });
}

// Payable
export function getPayableList(params: any): Promise<{ records: any[]; total: number }> {
  return request.get('/finance/payable/list', { params });
}

// Payment
export function getPaymentList(params: any): Promise<{ records: any[]; total: number }> {
  return request.get('/finance/payment/list', { params });
}

export function addPayment(data: any): Promise<any> {
  return request.post('/finance/payment', data);
}

export function deletePayment(id: number): Promise<any> {
  return request.delete(`/finance/payment/${id}`);
}
