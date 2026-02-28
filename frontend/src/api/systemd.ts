import type { ResponseData } from '@/types/response'
import type { ServiceUnitInfo, ServiceUnitOperation, ServiceFile } from '@/types/systemd'
import request from '@/utils/request'

/** 获取服务单元列表
 * @param level 运行级别
 */
export const getServiceUnits = (level: string): Promise<ResponseData<ServiceUnitInfo[]>> => {
  return request({
    method: 'get',
    url: '/systemd/service',
    params: { level },
  })
}

/** 操作服务单元
 * @param operation 操作参数
 */
export const operateServiceUnit = (
  operation: ServiceUnitOperation,
): Promise<ResponseData<boolean>> => {
  return request({
    method: 'post',
    url: '/systemd/service/operation',
    data: operation,
  })
}

/** 获取服务模板 */
export const getServiceTemplate = (): Promise<ResponseData<string>> => {
  return request({
    method: 'get',
    url: '/systemd/service/template',
  })
}

/** 上传服务文件
 * @param serviceFile 服务文件信息
 */
export const uploadService = (serviceFile: ServiceFile): Promise<ResponseData<boolean>> => {
  return request({
    method: 'post',
    url: '/systemd/service/upload',
    data: serviceFile,
  })
}
