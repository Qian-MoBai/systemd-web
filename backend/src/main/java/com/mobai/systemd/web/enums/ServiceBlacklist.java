package com.mobai.systemd.web.enums;

/**
 * systemd 服务黑名单
 *
 * @author Qian-MoBai
 */

public enum ServiceBlacklist {
	SYSTEMD("systemd"),
	DBUS("dbus"),
	UDEVADM("udevadm"),
	NETWORKING("networking"),
	NETWORK_MANAGER("network-manager"),
	LOGIND("logind");

	private final String serviceName;

	ServiceBlacklist(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceName() {
		return serviceName;
	}
}
