package com.mobai.systemd.web.entity;

import org.springframework.http.HttpStatus;

/**
 * 响应结果
 *
 * @author Qian-MoBai
 */
public class ResponseResult<T> {
	/**
	 * 数据
	 */
	private final T data;
	/**
	 * 状态码
	 */
	private final int code;
	/**
	 * 提示信息
	 */
	private final String message;

	public ResponseResult(HttpStatus status, T data) {
		this.code = status.value();
		this.message = status.getReasonPhrase();
		this.data = data;
	}

	public ResponseResult(int code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public T getData() {
		return data;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
