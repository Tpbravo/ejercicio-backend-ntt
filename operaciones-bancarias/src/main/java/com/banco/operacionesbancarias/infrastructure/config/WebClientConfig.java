package com.banco.operacionesbancarias.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Value("${services.gestion-clientes.url}")
	private String gestionClientesUrl;

	@Bean
	WebClient webClient(WebClient.Builder builder) {
		return builder.baseUrl(gestionClientesUrl).build();
	}
}