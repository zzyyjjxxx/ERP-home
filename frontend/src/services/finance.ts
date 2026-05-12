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

export function unauditVoucher(id: number): Promise<any> {
  return request.put(`/finance/voucher/${id}/unaudit`);
}

// Receivable
export function getReceivableList(params: any): Promise<{ records: any[]; total: number }> {
  return request.get('/finance/receivable/list', { params });
}
export function addReceivable(data: any): Promise<any> {
  return request.post('/finance/receivable', data);
}
export function updateReceivable(id: number, data: any): Promise<any> {
  return request.put(`/finance/receivable/${id}`, data);
}
export function deleteReceivable(id: number): Promise<any> {
  return request.delete(`/finance/receivable/${id}`);
}

// Payable
export function getPayableList(params: any): Promise<{ records: any[]; total: number }> {
  return request.get('/finance/payable/list', { params });
}
export function addPayable(data: any): Promise<any> {
  return request.post('/finance/payable', data);
}
export function updatePayable(id: number, data: any): Promise<any> {
  return request.put(`/finance/payable/${id}`, data);
}
export function deletePayable(id: number): Promise<any> {
  return request.delete(`/finance/payable/${id}`);
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

export function auditPayment(id: number): Promise<any> {
  return request.put(`/finance/payment/${id}/audit`);
}

export function unauditPayment(id: number): Promise<any> {
  return request.put(`/finance/payment/${id}/unaudit`);
}
