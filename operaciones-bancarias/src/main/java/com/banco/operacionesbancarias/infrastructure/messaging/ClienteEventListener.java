package com.banco.operacionesbancarias.infrastructure.messaging;

import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.banco.operacionesbancarias.domain.service.CuentaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ClienteEventListener {
	private final CuentaService cuentaService;

	@Transactional
	@KafkaListener(topics = "clientes-eventos", groupId = "operaciones-group", containerFactory = "kafkaListenerContainerFactory")
	public void consumirEvento(Map<String, Object> evento) {
		String tipo = (String) evento.get("evento");
		String clienteId = (String) evento.get("clienteId");

		log.info("Evento recibido: {} para cliente {}", tipo, clienteId);

		switch (tipo) {
		case "CLIENTE_DESACTIVADO" -> {
			log.info("Marcando cuentas del cliente {} como inactivas", clienteId);
			cuentaService.inactivarCuentasDeCliente(clienteId);
		}

		case "CLIENTE_ACTIVADO" -> {
			log.info("Marcando cuentas del cliente {} como activas", clienteId);
			cuentaService.activarCuentasDeCliente(clienteId);
		}

		case "CLIENTE_ELIMINADO" -> {
			log.info("Eliminando cuentas del cliente {}", clienteId);
			cuentaService.eliminarCuentasDeCliente(clienteId);
		}

		default -> log.warn("Evento desconocido recibido: {}", tipo);
		}
	}
}
