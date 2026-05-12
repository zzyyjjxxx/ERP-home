import request from '@/utils/request';

export function getBomList() {
  return request.get('/production/bom/list');
}

export function addBom(data: any) {
  return request.post('/production/bom', data);
}

export function updateBom(id: number, data: any) {
  return request.put(`/production/bom/${id}`, data);
}

export function deleteBom(id: number) {
  return request.delete(`/production/bom/${id}`);
}

export function getWorkOrderList(params: any) {
  return request.get('/production/workorder/list', { params });
}

export function addWorkOrder(data: any) {
  return request.post('/production/workorder', data);
}

export function completeWorkOrder(id: number) {
  return request.put(`/production/workorder/${id}/complete`);
}

export function deleteWorkOrder(id: number) {
  return request.delete(`/production/workorder/${id}`);
}

export function getPlanningList(params: any) {
  return request.get('/production/planning/list', { params });
}

export function addPlanning(data: any) {
  return request.post('/production/planning', data);
}

export function deletePlanning(id: number) {
  return request.delete(`/production/planning/${id}`);
}

export function getQcList(params: any) {
  return request.get('/production/qc/list', { params });
}

export function addQc(data: any) {
  return request.post('/production/qc', data);
}

export function deleteQc(id: number) {
  return request.delete(`/production/qc/${id}`);
}
