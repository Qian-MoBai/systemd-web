package com.mobai.systemd.web.controller;

import com.mobai.systemd.web.entity.ServiceUnitInfo;
import com.mobai.systemd.web.service.SystemdService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/systemd")
class SystemdController {
	/**
	 * 系统服务管理服务
	 */
	private final SystemdService systemdService;

	public SystemdController(SystemdService systemdService) {
		this.systemdService = systemdService;
	}

	@GetMapping("/services")
	public List<ServiceUnitInfo> listServiceUnits(@RequestParam("level") String level) {
		return systemdService.listServiceUnits(level);
	}
}
