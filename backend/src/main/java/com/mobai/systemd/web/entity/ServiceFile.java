package com.mobai.systemd.web.entity;

/**
 * service 文件
 *
 * @param level    系统级别
 * @param unitName 服务名称
 * @param content  服务内容
 * @author Qian-MoBai
 */
public record ServiceFile(
		String level,
		String unitName,
		String content
) {
}
