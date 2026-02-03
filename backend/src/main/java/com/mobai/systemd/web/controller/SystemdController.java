package com.mobai.systemd.web.controller;

import com.mobai.systemd.web.entity.ResponseResult;
import com.mobai.systemd.web.entity.ServiceFile;
import com.mobai.systemd.web.entity.ServiceUnitInfo;
import com.mobai.systemd.web.entity.ServiceUnitOperation;
import com.mobai.systemd.web.service.SystemdService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/systemd/service")
class SystemdController {
	private static final Logger LOG = LoggerFactory.getLogger(SystemdController.class);
	/**
	 * 系统服务管理服务
	 */
	private final SystemdService systemdService;

	public SystemdController(SystemdService systemdService) {
		this.systemdService = systemdService;
	}

	/**
	 * 列出服务单元
	 *
	 * @param level 系统级别
	 * @return 服务单元列表
	 */
	@GetMapping
	public ResponseResult<List<ServiceUnitInfo>> listServiceUnits(@RequestParam(value = "level", defaultValue = "system") String level) {
		LOG.info("Listing service units for level: {}", level);
		return new ResponseResult<>(HttpStatus.OK, systemdService.listServiceUnits(level));
	}

	/**
	 * 操作服务单元
	 *
	 * @param serviceUnitOperation 服务单元操作
	 * @return 操作结果
	 */
	@PostMapping("/operation")
	public ResponseResult<Boolean> operateServiceUnit(@RequestBody ServiceUnitOperation serviceUnitOperation) {
		LOG.info("Operating service unit: {}", serviceUnitOperation);
		return new ResponseResult<>(HttpStatus.OK, systemdService.operateServiceUnit(serviceUnitOperation));
	}

	/**
	 * 获取服务模板
	 *
	 * @param session session
	 * @return 服务模板
	 */
	@GetMapping("/template")
	public ResponseResult<String> getServiceTemplate(HttpSession session) {
		LOG.info("Getting service template");
		return new ResponseResult<>(HttpStatus.OK, systemdService.getServiceTemplate(session));
	}

	/**
	 * 上传服务
	 *
	 * @param session     session
	 * @param serviceFile 服务文件
	 * @return 上传结果
	 */
	@PostMapping("/upload")
	public ResponseResult<Boolean> uploadService(HttpSession session, @RequestBody ServiceFile serviceFile) {
		LOG.info("Uploading service: {}", serviceFile);
		return new ResponseResult<>(HttpStatus.OK, systemdService.uploadService(session, serviceFile));
	}
}
