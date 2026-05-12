import request from '@/utils/request';

export function getSubjectTree() {
  return request.get('/finance/subject/tree');
}

export function addSubject(data: any) {
  return request.post('/finance/subject', data);
}

export function updateSubject(id: number, data: any) {
  return request.put(`/finance/subject/${id}`, data);
}

export function deleteSubject(id: number) {
  return request.delete(`/finance/subject/${id}`);
}

export function getVoucherList(params: any) {
  return request.get('/finance/voucher/list', { params });
}

export function addVoucher(data: any) {
  return request.post('/finance/voucher', data);
}

export function deleteVoucher(id: number) {
  return request.delete(`/finance/voucher/${id}`);
}

export function getReceivableList(params: any) {
  return request.get('/finance/receivable/list', { params });
}

export function getPayableList(params: any) {
  return request.get('/finance/payable/list', { params });
}
