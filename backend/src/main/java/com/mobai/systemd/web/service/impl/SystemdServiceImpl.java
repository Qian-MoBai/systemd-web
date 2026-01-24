package com.mobai.systemd.web.service.impl;

import com.mobai.systemd.web.entity.ServiceUnitInfo;
import com.mobai.systemd.web.service.SystemdService;
import com.mobai.systemd.web.util.ExecUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 系统服务管理服务实现类
 *
 * @author Qian-MoBai
 */
@Service
public class SystemdServiceImpl implements SystemdService {
	private static final Logger LOG = LoggerFactory.getLogger(SystemdServiceImpl.class);

	@Override
	public List<ServiceUnitInfo> listServiceUnits(String level) {
		List<ServiceUnitInfo> serviceUnits = null;
		try {
			String[] command = switch (level) {
				case "system" -> new String[]{
						"systemctl", "--no-pager", "--type=service", "list-units"
				};
				case "user" -> new String[]{
						"systemctl", "--user", "--no-pager", "--type=service", "list-units"
				};
				default -> throw new IllegalArgumentException("Invalid level: " + level);
			};
			String output = Objects.requireNonNull(ExecUtil.executeCommand(command));
			serviceUnits = output.lines()
					// 过滤掉非服务类型的行
					.filter(line -> line.contains("service"))
					.map(line -> {
						// 过滤多余的空格并创建 ServiceUnitInfo 对象
						String[] parts = line.trim().split("\\s+");
						return new ServiceUnitInfo(
								parts[0],
								parts[1],
								parts[2],
								parts[3],
								String.join(" ", Arrays.copyOfRange(parts, 4, parts.length))
						);
					})
					.toList();
		} catch (IOException e) {
			LOG.error("Failed to list service units: {}", e.getMessage());
		}
		return serviceUnits;
	}

	@Override
	public boolean operateServiceUnit(String level, String operation, String unitName) {
		//TODO: 待实现该功能
		return false;
	}
}
