package com.mobai.systemd.web.entity;

/**
 * 服务单元信息
 *
 * @param unitName    服务名称
 * @param loadState   服务加载状态
 * @param activeState 服务运行状态
 * @param subState    服务子状态
 * @param description 服务描述
 */
public record ServiceUnitInfo(
		String unitName,
		String loadState,
		String activeState,
		String subState,
		String description
) {
}
