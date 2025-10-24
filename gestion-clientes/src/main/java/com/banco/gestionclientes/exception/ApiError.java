package com.banco.gestionclientes.exception;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

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
