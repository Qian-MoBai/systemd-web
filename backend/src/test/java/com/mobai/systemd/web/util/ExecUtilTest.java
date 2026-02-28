package com.mobai.systemd.web.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class ExecUtilTest {
	@Test
	void executeCommand() {
		try {
			String result = ExecUtil.executeCommand(new String[]{"systemctl", "--user", "--no-pager", "--type=service", "list-units"});
			System.out.println(result);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}