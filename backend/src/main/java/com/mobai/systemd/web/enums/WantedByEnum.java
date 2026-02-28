package com.mobai.systemd.web.enums;

/**
 * 系统服务目标枚举类
 *
 * @author Qian-MoBai
 */

public enum WantedByEnum {
	/**
	 * 多用户模式
	 */
	MULTI_USER_TARGET("multi-user.target"),
	/**
	 * 图形化模式
	 */
	GRAPHICAL_TARGET("graphical.target"),
	/**
	 * 默认模式
	 */
	DEFAULT_TARGET("default.target");
	private final String value;

	WantedByEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
