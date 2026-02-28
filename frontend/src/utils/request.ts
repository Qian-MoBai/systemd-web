import axios, { type AxiosInstance, type AxiosRequestConfig } from 'axios'
import type { ResponseData } from '@/types/response'
import { ElMessage } from 'element-plus'

const instance: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

instance.interceptors.response.use(
  (response) => {
    const resp = response.data as ResponseData
    if (resp.code === 500) {
      ElMessage.error(resp.message)
      return Promise.reject(resp.message)
    }
    return resp
  },
  (error) => {
    ElMessage.error(error.message)
    return Promise.reject(error)
  },
)
export default function request<T>(config: AxiosRequestConfig): Promise<ResponseData<T>> {
  return instance.request<any, ResponseData<T>>(config)
}
