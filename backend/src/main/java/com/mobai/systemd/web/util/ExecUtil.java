package com.mobai.systemd.web.util;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 执行命令工具类
 *
 * @author Qian-MoBai
 */
public class ExecUtil {

	private static final Logger LOG = LoggerFactory.getLogger(ExecUtil.class);

	/**
	 * 执行命令并返回结果
	 *
	 * @param command 命令字符串数组
	 * @return 执行结果
	 * @throws IOException IO异常
	 */
	public static String executeCommand(String[] command) throws IOException {
		String commandStr = String.join(" ", command);
		CommandLine cmdLine = CommandLine.parse(commandStr);
		DefaultExecutor executor = DefaultExecutor.builder().get();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
		executor.setStreamHandler(streamHandler);
		try {
			int exitValue = executor.execute(cmdLine);
			String result = outputStream.toString(StandardCharsets.UTF_8);
			if (exitValue != 0) {
				LOG.error("命令执行失败，退出码: {}，命令: {}，错误信息: {}", exitValue, commandStr, result);
				throw new ExecuteException("命令执行失败，退出码: " + exitValue, exitValue);
			}
			LOG.info("命令执行成功: {}，输出: {}", commandStr, result);
			return result;
		} catch (IOException e) {
			LOG.error("执行命令时发生IO异常: {}", commandStr, e);
			throw new IOException();
		}
	}

	/**
	 * 执行命令并返回结果（忽略执行失败）
	 *
	 * @param command 命令字符串数组
	 * @return 执行结果
	 */
	public static String executeCommandIgnoreError(String... command) {
		String commandStr = String.join(" ", command);
		try {
			return executeCommand(command);
		} catch (IOException e) {
			LOG.warn("执行命令时发生异常，已忽略: {}，异常信息: {}", commandStr, e.getMessage());
			return null;
		}
	}

	/**
	 * 检查命令是否执行成功
	 *
	 * @param command 命令字符串数组
	 * @return 是否执行成功
	 */
	public static boolean isCommandSuccessful(String... command) {
		try {
			executeCommand(command);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}

