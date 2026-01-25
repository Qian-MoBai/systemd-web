package com.mobai.systemd.web.entity;

/**
 * 服务单元操作
 *
 * @author Qian-MoBai
 */
public record ServiceUnitOperation(
		// 运行级别
		String level,
		// 操作
		String operation,
		// 服务名称
		String unitName
) {
}
