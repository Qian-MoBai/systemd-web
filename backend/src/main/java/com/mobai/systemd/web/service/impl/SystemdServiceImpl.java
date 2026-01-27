package com.mobai.systemd.web.service.impl;

import com.mobai.systemd.web.entity.ServiceFile;
import com.mobai.systemd.web.entity.ServiceUnitInfo;
import com.mobai.systemd.web.entity.ServiceUnitOperation;
import com.mobai.systemd.web.enums.Operation;
import com.mobai.systemd.web.enums.ServiceFileContentBlacklist;
import com.mobai.systemd.web.enums.UnitBlacklist;
import com.mobai.systemd.web.enums.WantedByEnum;
import com.mobai.systemd.web.service.SystemdService;
import com.mobai.systemd.web.util.ExecUtil;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	/**
	 * 环境变量
	 */
	private final Environment env;

	public SystemdServiceImpl(Environment env) {
		this.env = env;
	}

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
		if (checkUnitName(serviceUnitOperation.unitName())) {
			throw new SecurityException("Invalid ServiceName: " + serviceUnitOperation.unitName());
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

	/**
	 * 检查服务名是否合法
	 *
	 * @param unitName 服务名
	 */
	private boolean checkUnitName(String unitName) {
		Pattern safeServiceName = Pattern.compile("^(?:[a-zA-Z0-9_.@-]|\\\\x[0-9a-fA-F]{2})+\\.service$");
		long serviceBlacklistCount = Arrays.stream(UnitBlacklist.values())
				.filter(unitBlacklist -> unitBlacklist.getUnitName().equals(unitName))
				.count();
		return serviceBlacklistCount > 0 || !safeServiceName.matcher(unitName).matches();
	}

	@Override
	public String getServiceTemplate(HttpSession session) {
		try {
			session.setAttribute("isGetTemplate", true);
			ClassPathResource resource = new ClassPathResource("templates/systemd/template.service");
			return new String(resource.getInputStream().readAllBytes(),
					StandardCharsets.UTF_8);
		} catch (IOException e) {
			LOG.error("Failed to get service template: {}", e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public boolean uploadService(HttpSession session, ServiceFile serviceFile) {
		if (session.getAttribute("isGetTemplate") == null) {
			LOG.error("{} the pre request is not accessed", session.getId());
			throw new RuntimeException(session.getId() + " the pre request is not accessed");
		}
		// 检查参数
		if (!StringUtils.hasText(serviceFile.unitName())
				|| !StringUtils.hasText(serviceFile.content())
				|| !StringUtils.hasText(serviceFile.level())) {
			LOG.error("Invalid parameters");
			throw new IllegalArgumentException("Invalid parameters");
		}
		// 检查服务名是否合法
		if (checkUnitName(serviceFile.unitName())) {
			throw new SecurityException("Invalid ServiceName: " + serviceFile.unitName());
		}
		// 检查最小条件
		if (!serviceFile.content().contains("[Unit]")
				|| !serviceFile.content().contains("[Service]")
				|| !serviceFile.content().contains("ExecStart=")
				|| !serviceFile.content().contains("[Install]")
				|| !serviceFile.content().contains("WantedBy=")) {
			LOG.error("Invalid service file: {}", serviceFile.unitName());
			throw new IllegalArgumentException("Invalid service file: " + serviceFile.unitName());
		}
		long serviceFileBlacklistCount = Arrays.stream(ServiceFileContentBlacklist.values())
				.filter(serviceFileBlacklist -> serviceFileBlacklist.find(serviceFile.content()))
				.count();
		// 检查 WantedBy
		String wantedBy = serviceFile.content().lines()
				.filter(line -> line.startsWith("WantedBy="))
				.toString();
		long count = Arrays.stream(WantedByEnum.values())
				.filter(wantedByEnum -> wantedBy.contains(wantedByEnum.getValue()))
				.count();
		if (serviceFileBlacklistCount > 0 || count == 0) {
			LOG.error("Invalid service file: {}", serviceFile.unitName());
			throw new IllegalArgumentException("Invalid service file: " + serviceFile.unitName());
		}
		// 路径
		Path baseDir = switch (serviceFile.level()) {
			case "system" -> Paths.get(Objects.requireNonNull(env.getProperty("systemd.service.system")));
			case "user" ->
					Paths.get(env.getProperty("systemd.service.user.home") + env.getProperty("systemd.service.user.path"));
			default -> throw new IllegalStateException("Unexpected level");
		};
		Path target = baseDir.resolve(serviceFile.unitName()).normalize();
		if (!target.startsWith(baseDir)) {
			throw new SecurityException("Path traversal detected");
		}
		// 写入
		try {
			File file = target.toFile();
			if (!file.exists()) {
				if (file.createNewFile()) {
					Files.writeString(target, serviceFile.content());
				}
			} else {
				throw new FileAlreadyExistsException(serviceFile.unitName() + "File already exists");
			}
		} catch (IOException e) {
			LOG.error("Failed to write service file: {}", serviceFile.unitName());
			throw new RuntimeException(e.getMessage());
		}
		// 重载 systemd
		return ExecUtil.isCommandSuccessful(buildSystemdCommand(serviceFile.level(), new String[]{"systemctl", "daemon-reload"}));
	}
}
