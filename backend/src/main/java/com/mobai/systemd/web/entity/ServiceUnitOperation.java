package com.mobai.systemd.web.entity;

/**
 * 服务单元操作
 *
 * @param level     系统级别
 * @param operation 操作
 * @param unitName  服务名称
 * @author Qian-MoBai
 */
public record ServiceUnitOperation(
		String level,
		String operation,
		String unitName
) {
}
