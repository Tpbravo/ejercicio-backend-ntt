package com.banco.operacionesbancarias.infrastructure.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.banco.operacionesbancarias.api.dto.ClienteDTO;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ClienteServiceClient {

	private final WebClient webClient;

	public Mono<ClienteDTO> obtenerClientePorClienteId(String clienteId) {
		return webClient.get().uri("/codigo/{clienteId}", clienteId).retrieve()
				.onStatus(status -> status.value() == 404,
						response -> Mono
								.error(new RuntimeException("Cliente no encontrado con clienteId: " + clienteId)))
				.onStatus(status -> status.is5xxServerError(),
						response -> Mono.error(new RuntimeException("Error al consultar el servicio de clientes")))
				.bodyToMono(ClienteDTO.class);
	}

	public Mono<ClienteDTO> obtenerClientePorId(String id) {
		return webClient.get().uri("/{id}", id).retrieve()
				.onStatus(status -> status.value() == 404,
						response -> Mono.error(new RuntimeException("Cliente no encontrado con id: " + id)))
				.onStatus(status -> status.is5xxServerError(),
						response -> Mono.error(new RuntimeException("Error al consultar el servicio de clientes")))
				.bodyToMono(ClienteDTO.class);
	}
}