/** 服务单元信息 */
export type ServiceUnitInfo = {
  /** 服务文件名称 */
  unitFile: string
  /** 服务加载状态 */
  state: string
  /** 服务运行状态 */
  preset: string
}

/** 服务单元操作 */
export type ServiceUnitOperation = {
  /** 系统级别 */
  level: string
  /** 操作 */
  operation: string
  /** 服务单元名称 */
  unitName: string
}

/** 服务单元文件 */
export type ServiceFile = {
  /** 系统级别 */
  level: string
  /** 文件名称 */
  unitName: string
  /** 文件内容 */
  content: string
}
