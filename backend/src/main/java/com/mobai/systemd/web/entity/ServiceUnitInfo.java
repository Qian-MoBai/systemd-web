package com.mobai.systemd.web.entity;

/**
 * 服务单元信息
 *
 * @param unitFile 服务名称
 * @param state    服务加载状态
 * @param preset   服务运行状态
 */
public record ServiceUnitInfo(
		String unitFile,
		String state,
		String preset
) {
}
