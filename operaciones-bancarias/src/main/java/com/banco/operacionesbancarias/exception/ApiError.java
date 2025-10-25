package com.banco.operacionesbancarias.exception;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

/**
 * Estructura estándar de error API en formato JSON. Compatible con los
 * controladores REST y el GlobalExceptionHandler.
 */
@Data
@Builder
public class ApiError {
	private LocalDateTime timestamp;
	private int status;
	private String error;
	private String message;
	private Map<String, String> details;
	private String path;
}
