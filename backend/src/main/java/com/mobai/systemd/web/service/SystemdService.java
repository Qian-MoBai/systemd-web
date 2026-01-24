package com.mobai.systemd.web.service;

import com.mobai.systemd.web.entity.ServiceUnitInfo;

import java.util.List;

/**
 * 系统服务管理服务
 *
 * @author Qian-MoBai
 */
public interface SystemdService {
	/**
	 * 列出服务单元
	 *
	 * @param level 服务单元级别
	 * @return 服务单元列表
	 */
	List<ServiceUnitInfo> listServiceUnits(String level);

	/**
	 * 操作服务单元
	 */
	boolean operateServiceUnit(String level, String operation, String unitName);
}
