package com.mobai.systemd.web.config;

import com.mobai.systemd.web.entity.ResponseResult;
import org.springframework.http.HttpStatus;
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
	public ResponseResult<String> handleException(Exception e) {
		return new ResponseResult<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
	}
}
