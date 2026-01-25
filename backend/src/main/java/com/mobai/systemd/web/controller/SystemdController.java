package com.mobai.systemd.web.controller;

import com.mobai.systemd.web.entity.ServiceUnitInfo;
import com.mobai.systemd.web.entity.ServiceUnitOperation;
import com.mobai.systemd.web.service.SystemdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/systemd")
class SystemdController {
	private static final Logger LOG = LoggerFactory.getLogger(SystemdController.class);
	/**
	 * 系统服务管理服务
	 */
	private final SystemdService systemdService;

	public SystemdController(SystemdService systemdService) {
		this.systemdService = systemdService;
	}

	@GetMapping("/services")
	public List<ServiceUnitInfo> listServiceUnits(@RequestParam(value = "level", defaultValue = "system") String level) {
		LOG.info("Listing service units for level: {}", level);
		return systemdService.listServiceUnits(level);
	}

	@PostMapping("/service/operation")
	public boolean operateServiceUnit(@RequestBody ServiceUnitOperation serviceUnitOperation) {
		LOG.info("Operating service unit: {}", serviceUnitOperation);
		return systemdService.operateServiceUnit(serviceUnitOperation);
	}
}
