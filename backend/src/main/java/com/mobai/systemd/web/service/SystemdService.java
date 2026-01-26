package com.mobai.systemd.web.service;

import com.mobai.systemd.web.entity.ServiceFile;
import com.mobai.systemd.web.entity.ServiceUnitInfo;
import com.mobai.systemd.web.entity.ServiceUnitOperation;
import jakarta.servlet.http.HttpSession;

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
	 *
	 * @param serviceUnitOperation 服务单元操作
	 * @return 是否操作成功
	 */
	boolean operateServiceUnit(ServiceUnitOperation serviceUnitOperation);

	/**
	 * 获取服务模板
	 *
	 * @param session 会话
	 * @return 服务模板
	 */
	String getServiceTemplate(HttpSession session);

	/**
	 * 上传服务
	 *
	 * @param session     会话
	 * @param serviceFile 服务文件
	 * @return 是否上传成功
	 */
	boolean uploadService(HttpSession session, ServiceFile serviceFile);
}
