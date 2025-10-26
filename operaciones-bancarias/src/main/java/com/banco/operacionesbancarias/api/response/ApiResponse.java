package com.banco.operacionesbancarias.api.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<T> {
	private boolean success;
	private String message;
	private T data;
	private LocalDateTime timestamp;

	public static <T> ApiResponse<T> ok(T data, String message) {
		return ApiResponse.<T>builder().success(true).message(message).data(data).timestamp(LocalDateTime.now())
				.build();
	}

	public static <T> ApiResponse<T> error(String message) {
		return ApiResponse.<T>builder().success(false).message(message).timestamp(LocalDateTime.now()).build();
	}
}
