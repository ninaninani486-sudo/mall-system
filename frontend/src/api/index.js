import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

export function proxyImage(url) {
  if (!url) return ''
  if (url.startsWith('/api/')) return url
  if (url.includes('picsum.photos') || url.includes('placehold.co')) return url
  return `/api/image/proxy?url=${encodeURIComponent(url)}`
}

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || 'Error')
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return res
  },
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    ElMessage.error(error.message || 'Network Error')
    return Promise.reject(error)
  }
)

export default api
