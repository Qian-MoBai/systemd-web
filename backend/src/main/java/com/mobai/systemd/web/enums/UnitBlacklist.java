package com.mobai.systemd.web.enums;

/**
 * systemd 服务黑名单
 *
 * @author Qian-MoBai
 */

public enum UnitBlacklist {
	SYSTEMD("systemd"),
	DBUS("dbus"),
	UDEVADM("udevadm"),
	NETWORKING("networking"),
	NETWORK_MANAGER("network-manager"),
	LOGIND("logind");

	private final String unitName;

	UnitBlacklist(String unitName) {
		this.unitName = unitName;
	}

	public String getUnitName() {
		return unitName;
	}
}
