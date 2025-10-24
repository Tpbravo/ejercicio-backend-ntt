package com.banco.gestionclientes.infrastructure.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		long start = System.currentTimeMillis();

		// Ejecutar la petición
		filterChain.doFilter(request, response);

		long duration = System.currentTimeMillis() - start;

		log.info("[{}] {} -> status={} ({} ms) from IP={}", request.getMethod(), request.getRequestURI(),
				response.getStatus(), duration, request.getRemoteAddr());
	}
}