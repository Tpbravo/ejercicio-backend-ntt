package com.banco.gestionclientes.exception;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@Autowired
	private MessageSource messageSource;

	// Errores de validación provenientes de DTOs (@Valid)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {

		Locale locale = LocaleContextHolder.getLocale();

		Map<String, String> details = ex.getBindingResult().getFieldErrors().stream()
				.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> b));

		String errorTitle = messageSource.getMessage("validation.error.title", null, locale);
		String errorMessage = messageSource.getMessage("validation.error.message", null, locale);

		ApiError error = ApiError.builder().timestamp(LocalDateTime.now()).status(HttpStatus.BAD_REQUEST.value())
				.error(errorTitle).message(errorMessage).details(details).path(request.getDescription(false)).build();

		return ResponseEntity.badRequest().body(error);
	}

	// Errores de validación en entidades JPA (persistencia)
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiError> handleConstraintViolations(ConstraintViolationException ex, WebRequest request) {
		Locale locale = LocaleContextHolder.getLocale();

		Map<String, String> details = ex.getConstraintViolations().stream().collect(
				Collectors.toMap(v -> v.getPropertyPath().toString(), ConstraintViolation::getMessage, (a, b) -> b));

		String errorTitle = messageSource.getMessage("validation.error.title", null, locale);
		String errorMessage = messageSource.getMessage("validation.error.message", null, locale);

		ApiError error = ApiError.builder().timestamp(LocalDateTime.now()).status(HttpStatus.BAD_REQUEST.value())
				.error(errorTitle).message(errorMessage).details(details).path(request.getDescription(false)).build();

		return ResponseEntity.badRequest().body(error);
	}

	// Excepciones de negocio (RuntimeException)
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiError> handleRuntime(RuntimeException ex, WebRequest request) {
		Locale locale = LocaleContextHolder.getLocale();

		String errorTitle = messageSource.getMessage("business.error.title", null, locale);

		ApiError error = ApiError.builder().timestamp(LocalDateTime.now()).status(HttpStatus.BAD_REQUEST.value())
				.error(errorTitle).message(ex.getMessage()).path(request.getDescription(false)).build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	// Excepciones generales (errores no controlados)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleAllExceptions(Exception ex, WebRequest request) {
		Locale locale = LocaleContextHolder.getLocale();

		String errorTitle = messageSource.getMessage("server.error.title", null, locale);

		ApiError error = ApiError.builder().timestamp(LocalDateTime.now())
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).error(errorTitle).message(ex.getMessage())
				.path(request.getDescription(false)).build();

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, WebRequest request) {
		Locale locale = LocaleContextHolder.getLocale();
		String title = messageSource.getMessage("data.error.title", null, locale);
		String message;

		if (ex.getMostSpecificCause().getMessage().contains("duplicate key value")) {
			message = messageSource.getMessage("data.error.duplicate", null, locale);
		} else {
			message = messageSource.getMessage("data.error.generic", null, locale);
		}

		ApiError error = ApiError.builder().timestamp(LocalDateTime.now()).status(HttpStatus.BAD_REQUEST.value())
				.error(title).message(message).path(request.getDescription(false)).build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
		Locale locale = LocaleContextHolder.getLocale();

		String errorTitle = messageSource.getMessage("resource.notfound.title", null, "Recurso no encontrado", locale);

		ApiError error = ApiError.builder().timestamp(LocalDateTime.now()).status(HttpStatus.NOT_FOUND.value())
				.error(errorTitle).message(ex.getMessage()).path(request.getDescription(false)).build();

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

}