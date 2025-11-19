import request from '../utils/request'

// 认证相关
export const authApi = {
  login: (data) => request.post('/auth/login', data)
}

// 小说相关
export const novelApi = {
  getList: (params) => request.get('/admin/novel/list', { params }),
  create: (data) => request.post('/admin/novel', data),
  update: (id, data) => request.put(`/admin/novel/${id}`, data),
  delete: (id) => request.delete(`/admin/novel/${id}`),
  submitAudit: (id) => request.post(`/admin/novel/${id}/submit`),
  audit: (id, data) => request.post(`/admin/novel/${id}/audit`, data)
}

// 章节相关
export const chapterApi = {
  create: (novelId, data) => request.post(`/admin/chapter/novel/${novelId}`, data),
  update: (id, data) => request.put(`/admin/chapter/${id}`, data),
  delete: (id) => request.delete(`/admin/chapter/${id}`)
}

// 用户相关
export const userApi = {
  getUserInfo: (id) => request.get(`/admin/user/${id}`),
  setTrialSettings: (id, data) => request.put(`/admin/user/${id}/trial-settings`, data)
}
