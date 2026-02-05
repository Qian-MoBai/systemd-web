/** 服务单元信息 */
export type ServiceUnitInfo = {
  /** 服务单元名称 */
  unitName: string
  /** 服务加载状态 */
  loadState: string
  /** 服务运行状态 */
  activeState: string
  /** 服务子状态 */
  subState: string
  /** 服务描述 */
  description: string
}

/**  服务单元操作 */
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
