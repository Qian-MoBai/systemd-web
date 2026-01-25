package com.mobai.systemd.web.service.impl;

import com.mobai.systemd.web.entity.ServiceUnitInfo;
import com.mobai.systemd.web.entity.ServiceUnitOperation;
import com.mobai.systemd.web.enums.Operation;
import com.mobai.systemd.web.enums.ServiceBlacklist;
import com.mobai.systemd.web.service.SystemdService;
import com.mobai.systemd.web.util.ExecUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

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
			String[] command = buildSystemdCommand(level, new String[]{"systemctl", "--no-pager", "--type=service", "list-units"});
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
	public boolean operateServiceUnit(ServiceUnitOperation serviceUnitOperation) {
		// 检查服务名是否合法
		Pattern safeServiceName = Pattern.compile("^(?:[a-zA-Z0-9_.@-]|\\\\x[0-9a-fA-F]{2})+\\.service$");
		// 检查服务黑名单
		long serviceBlacklistCount = Arrays.stream(ServiceBlacklist.values())
				.filter(serviceBlacklist -> serviceBlacklist.getServiceName().equals(serviceUnitOperation.unitName()))
				.count();
		if (serviceBlacklistCount > 0 || !safeServiceName.matcher(serviceUnitOperation.unitName()).matches()) {
			throw new IllegalArgumentException("Invalid ServiceName: " + serviceUnitOperation.unitName());
		}
		// 检查操作
		long operateCount = Arrays.stream(Operation.values())
				.filter(operation -> operation.getOperation().equals(serviceUnitOperation.operation()))
				.count();
		if (operateCount == 0) {
			throw new IllegalArgumentException("Invalid operation: " + serviceUnitOperation.operation());
		}
		String[] command = buildSystemdCommand(serviceUnitOperation.level(), new String[]{"systemctl", serviceUnitOperation.operation(), serviceUnitOperation.unitName()});
		return ExecUtil.isCommandSuccessful(command);
	}

	/**
	 * 构建 systemd 命令
	 *
	 * @param level   系统级别
	 * @param command 命令
	 */
	private String[] buildSystemdCommand(String level, String[] command) {
		List<String> newCommand;
		switch (level) {
			case "system" -> {
				List<String> temp = new ArrayList<>(Arrays.asList(command));
				temp.addFirst("sudo");
				newCommand = temp;
			}
			case "user" -> {
				List<String> temp = new ArrayList<>(Arrays.asList(command));
				temp.add(1, "--user");
				newCommand = temp;
			}
			default -> throw new IllegalArgumentException("Invalid level: " + level);
		}
		return newCommand.toArray(String[]::new);
	}
}
