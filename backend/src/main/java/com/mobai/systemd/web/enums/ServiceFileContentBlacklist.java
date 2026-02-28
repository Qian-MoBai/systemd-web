package com.mobai.systemd.web.enums;

import java.util.regex.Pattern;

/**
 * 服务文件内容黑名单
 *
 * @author Qian-MoBai
 */
public enum ServiceFileContentBlacklist {
	/**
	 * 删除根文件系统
	 */
	RM_ROOT_FILE_SYSTEM("\\b(rm|unlink)\\s+-rf?\\s+(/\\s*|/\\*)"),
	/**
	 * 递归删除关键系统目录
	 */
	RECURSIVELY_DELETE_CRITICAL_SYSTEM_DIRECTORIES("\\b(rm|unlink)\\s+-rf?\\s+/(bin|sbin|lib|lib64|usr|etc|boot|var|opt)\\b"),
	/**
	 * 删除关键系统文件
	 */
	THE_DISK_WRITES_ZERO_RANDOM_NUMBERS_DIRECTLY("\\b(dd)\\s+if=/(dev/zero|dev/urandom)\\s+of=/dev/(sd[a-z]|vd[a-z]|nvme\\d+n\\d+)"),
	/**
	 * 格式化磁盘
	 */
	FORMAT_THE_DISK("\\b(mkfs(\\.\\w+)?|wipefs)\\s+.*?/dev/(sd[a-z]|vd[a-z]|nvme\\d+n\\d+)"),
	/**
	 * fork 炸弹
	 */
	FORK_BOMB(":\\s*\\(\\s*\\)\\s*\\{\\s*:\\s*\\|\\s*:\\s*;\\s*\\}\\s*;"),
	/**
	 * 杀掉 systemd / init / PID 1
	 */
	KILL_SYSTEMD_INIT_PID_1("\\b(kill|killall|pkill)\\s+(-9\\s+)?(systemd|init|1)\\b"),
	/**
	 * 批量杀进程
	 */
	KILL_ALL_PROCESSES_WITH_ONE_CLICK("\\b(killall|pkill)\\s+(-9\\s+)?(-u\\s+root|\\-1)\\b"),
	/**
	 * 挂载根文件系统为可写并破坏
	 */
	REMOUNT_THE_ROOT_AS_WRITABLE_AND_DESTROY("\\bmount\\s+-o\\s+remount,(rw|rw,.*)\\s+/\\b"),
	/**
	 * 关闭 SELinux
	 */
	TURN_OFF_SELINUX("\\b(setenforce\\s+0|getenforce\\s*\\|\\s*grep\\s+Permissive)\\b"),
	/**
	 * 重启、关机
	 */
	REBOOT_POWER_OFF("\\b(reboot|shutdown|poweroff|halt)\\b"),
	/**
	 * 自杀式 systemctl（尤其 ExecStart）
	 */
	SUICIDAL_SYSTEMCTL("\\bsystemctl\\s+(stop|restart)\\s+(systemd|multi-user\\.target|default\\.target)"),
	/**
	 * 停止网络（远程机=失联）
	 */
	STOP_NETWORK("\\bsystemctl\\s+stop\\s+(network|NetworkManager|networking)\\b"),
	/**
	 * 停止 SSH（远程机器死亡按钮）
	 */
	STOP_SSH("\\bsystemctl\\s+stop\\s+sshd\\b"),
	/**
	 * 修改 sudoers / 提权
	 */
	MODIFY_SUDOERS("\\b(echo|sed)\\s+.*(sudoers|/etc/sudoers\\.d)\\b"),
	/**
	 * 强行 chmod root 关键目录
	 */
	CHMOD_CRITICAL_DIRS("\\bchmod\\s+(777|666)\\s+/(etc|root|bin|sbin|usr)\\b"),
	/**
	 * 覆盖系统账号
	 */
	OVERWRITE_SYSTEM_ACCOUNTS("\\b(echo|sed|tee)\\s+.*(/etc/passwd|/etc/shadow)\\b"),
	/**
	 * ExecStart 使用 shell + 危险链式
	 */
	EXEC_START_DANGEROUS_CHAIN("Exec(Start|Stop|Reload)=.*/(sh|bash)\\s+-c\\s+.*(\\|\\||&&|;).*"),
	/**
	 * 后台化自身（破坏 systemd 状态机）
	 */
	DAEMONIZE_ITSELF("Exec(Start|Stop)=.*\\s+(&|nohup)\\b");

	private final Pattern pattern;

	ServiceFileContentBlacklist(String regex) {
		this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
	}

	/**
	 * 匹配字符串
	 *
	 * @param string 待匹配的字符串
	 * @return 匹配结果
	 */
	public boolean find(String string) {
		return pattern.matcher(string).find();
	}
}
