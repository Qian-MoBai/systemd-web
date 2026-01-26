package com.mobai.systemd.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 *
 * @author Qian-MoBai
 */
@SpringBootApplication
public class SystemdWebApplication {

	static void main(String[] args) {
		// 获取用户目录
		String home = System.getenv("HOME");
		if (home == null) {
			home = System.getProperty("user.home");
		}
		// 设置用户服务目录
		System.setProperty("systemd.service.user.home", home);
		// 启动
		SpringApplication.run(SystemdWebApplication.class, args);
	}

}
