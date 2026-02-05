import type { ResponseData } from '@/types/response'
import type { ServiceUnitInfo } from '@/types/systemd'
import request from '@/utils/request'

/** 获取服务单元列表
 * @param level 运行级别
 */
export const getServiceUnits = (level: string): Promise<ResponseData<ServiceUnitInfo[]>> => {
  return request({ method: 'get', url: '/systemd/service', params: level })
}
