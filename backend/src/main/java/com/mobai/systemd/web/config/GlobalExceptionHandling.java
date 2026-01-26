package com.mobai.systemd.web.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

/**
 * 全局异常处理
 *
 * @author Qian-MoBai
 */
@RestController
@ControllerAdvice
public class GlobalExceptionHandling {
	@ExceptionHandler(exception = {RuntimeException.class, IllegalArgumentException.class, SecurityException.class})
	public ResponseEntity<String> handleException(Exception e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
