import request from '@/utils/request';

// BOM
export function getBomList(params: any) { return request.get('/production/bom/list', { params }); }
export function getBomItems(bomId: number) { return request.get(`/production/bom/${bomId}/items`); }
export function addBom(data: any) { return request.post('/production/bom', data); }
export function updateBom(id: number, data: any) { return request.put(`/production/bom/${id}`, data); }
export function deleteBom(id: number) { return request.delete(`/production/bom/${id}`); }
export function enableBom(id: number) { return request.put(`/production/bom/${id}/enable`); }
export function disableBom(id: number) { return request.put(`/production/bom/${id}/disable`); }

// Work Order
export function getWorkOrderList(params: any) { return request.get('/production/workorder/list', { params }); }
export function addWorkOrder(data: any) { return request.post('/production/workorder', data); }
export function updateWorkOrder(id: number, data: any) { return request.put(`/production/workorder/${id}`, data); }
export function deleteWorkOrder(id: number) { return request.delete(`/production/workorder/${id}`); }
export function startWorkOrder(id: number) { return request.put(`/production/workorder/${id}/start`); }
export function completeWorkOrder(id: number, actualQty: number) { return request.put(`/production/workorder/${id}/complete?actualQty=${actualQty}`); }
export function closeWorkOrder(id: number) { return request.put(`/production/workorder/${id}/close`); }

// Planning
export function getPlanningList(params: any) { return request.get('/production/planning/list', { params }); }
export function addPlanning(data: any) { return request.post('/production/planning', data); }
export function updatePlanning(id: number, data: any) { return request.put(`/production/planning/${id}`, data); }
export function deletePlanning(id: number) { return request.delete(`/production/planning/${id}`); }
export function auditPlanning(id: number) { return request.put(`/production/planning/${id}/audit`); }

// QC
export function getQcList(params: any) { return request.get('/production/qc/list', { params }); }
export function addQc(data: any) { return request.post('/production/qc', data); }
export function updateQc(id: number, data: any) { return request.put(`/production/qc/${id}`, data); }
export function deleteQc(id: number) { return request.delete(`/production/qc/${id}`); }
