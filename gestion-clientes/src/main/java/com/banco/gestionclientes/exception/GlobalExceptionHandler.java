package com.banco.gestionclientes.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleAllExceptions(Exception ex, WebRequest request) {
		ApiError error = ApiError.builder().timestamp(LocalDateTime.now())
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()).message(ex.getMessage())
				.path(request.getDescription(false)).build();

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiError> handleRuntime(RuntimeException ex, WebRequest request) {
		ApiError error = ApiError.builder().timestamp(LocalDateTime.now()).status(HttpStatus.BAD_REQUEST.value())
				.error(HttpStatus.BAD_REQUEST.getReasonPhrase()).message(ex.getMessage())
				.path(request.getDescription(false)).build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
}
