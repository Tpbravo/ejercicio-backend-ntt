package com.banco.gestionclientes.infrastructure.messaging;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClienteEventProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	public void publicarClienteDesactivado(String clienteId) {
		enviarEvento("CLIENTE_DESACTIVADO", clienteId);
	}

	public void publicarClienteActivado(String clienteId) {
		enviarEvento("CLIENTE_ACTIVADO", clienteId);
	}

	public void publicarClienteEliminado(String clienteId) {
		enviarEvento("CLIENTE_ELIMINADO", clienteId);
	}

	private void enviarEvento(String tipo, String clienteId) {
		Map<String, Object> evento = Map.of("evento", tipo, "clienteId", clienteId, "fecha", LocalDateTime.now());
		kafkaTemplate.send("clientes-eventos", clienteId, evento);
		kafkaTemplate.flush();
	}
}