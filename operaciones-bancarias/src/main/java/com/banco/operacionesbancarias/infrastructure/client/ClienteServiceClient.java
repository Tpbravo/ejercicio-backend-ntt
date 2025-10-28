package com.banco.operacionesbancarias.infrastructure.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.banco.operacionesbancarias.api.dto.ClienteDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteServiceClient {

	@Qualifier("webClient")
	private final WebClient webClient;

	@Value("${services.gestion-clientes.url}")
	private String baseUrl;

	public Mono<ClienteDTO> obtenerClientePorClienteId(String clienteId) {
		String uri = String.format("/codigo/%s", clienteId);
		String fullUrl = baseUrl + uri;

		log.info("Consultando cliente con URL: {}", fullUrl);

		return webClient.get().uri(uri).retrieve().onStatus(status -> status.value() == 404,
				response -> Mono.error(new RuntimeException("Cliente no encontrado con clienteId: " + clienteId)))
				.onStatus(status -> status.is5xxServerError(),
						response -> Mono.error(new RuntimeException("Error al consultar el servicio de clientes")))
				.bodyToMono(ClienteDTO.class).doOnSuccess(cliente -> log.info(" Cliente obtenido: {}", cliente))
				.doOnError(error -> log.error(" Error al consultar cliente {}: {}", clienteId, error.getMessage()));
	}

	public Mono<ClienteDTO> obtenerClientePorId(String id) {
		String uri = String.format("/%s", id);
		String fullUrl = baseUrl + uri;

		log.info(" Consultando cliente con URL: {}", fullUrl);

		return webClient.get().uri(uri).retrieve()
				.onStatus(status -> status.value() == 404,
						response -> Mono.error(new RuntimeException("Cliente no encontrado con id: " + id)))
				.onStatus(status -> status.is5xxServerError(),
						response -> Mono.error(new RuntimeException("Error al consultar el servicio de clientes")))
				.bodyToMono(ClienteDTO.class).doOnSuccess(cliente -> log.info(" Cliente obtenido por ID: {}", cliente))
				.doOnError(error -> log.error(" Error al consultar cliente {}: {}", id, error.getMessage()));
	}
}