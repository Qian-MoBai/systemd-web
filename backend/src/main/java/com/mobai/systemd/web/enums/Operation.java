package com.mobai.systemd.web.enums;

/**
 * 服务单元操作枚举类
 *
 * @author Qian-MoBai
 */

public enum Operation {
	/**
	 * 启动
	 */
	START("start"),
	/**
	 * 停止
	 */
	STOP("stop"),
	/**
	 * 重启
	 */
	RESTART("restart"),
	/**
	 * 启用
	 */
	ENABLE("enable"),
	/**
	 * 禁用
	 */
	DISABLE("disable"),
	/**
	 * 重载
	 */
	RELOAD("reload");
	private final String operation;

	Operation(String operation) {
		this.operation = operation;
	}

	public String getOperation() {
		return operation;
	}
}
