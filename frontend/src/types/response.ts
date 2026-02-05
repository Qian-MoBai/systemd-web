/** 响应数据 */
export type ResponseData<T = any> = {
  /** 状态码 */
  code: number
  /** 数据 */
  data: T
  /** 错误信息 */
  message: string
}
