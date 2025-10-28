package com.banco.operacionesbancarias.infrastructure.messaging;

import static org.mockito.Mockito.*;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.banco.operacionesbancarias.domain.service.CuentaService;

@SpringJUnitConfig
@ContextConfiguration(classes = { ClienteEventListener.class, ClienteEventListenerTest.KafkaTestConfig.class })
@EmbeddedKafka(partitions = 1, topics = { "clientes-eventos" })
@DisplayName("Kafka Listener - Consumo de eventos de cliente")
class ClienteEventListenerTest {

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	@MockBean
	private CuentaService cuentaService;

	@Test
	@DisplayName("Debe consumir evento CLIENTE_DESACTIVADO y llamar a inactivarCuentasDeCliente")
	void deberiaConsumirEventoClienteDesactivado() {
		Map<String, Object> evento = Map.of("evento", "CLIENTE_DESACTIVADO", "clienteId", "CLI001");

		kafkaTemplate.send("clientes-eventos", "CLI001", evento);

		await().atMost(Duration.ofSeconds(10))
				.untilAsserted(() -> verify(cuentaService).inactivarCuentasDeCliente("CLI001"));
	}

	@Test
	@DisplayName("Debe consumir evento CLIENTE_ACTIVADO y llamar a activarCuentasDeCliente")
	void deberiaConsumirEventoClienteActivado() {
		Map<String, Object> evento = Map.of("evento", "CLIENTE_ACTIVADO", "clienteId", "CLI002");

		kafkaTemplate.send("clientes-eventos", "CLI002", evento);

		await().atMost(Duration.ofSeconds(10))
				.untilAsserted(() -> verify(cuentaService).activarCuentasDeCliente("CLI002"));
	}

	@Test
	@DisplayName("Debe consumir evento CLIENTE_ELIMINADO y llamar a eliminarCuentasDeCliente")
	void deberiaConsumirEventoClienteEliminado() {
		Map<String, Object> evento = Map.of("evento", "CLIENTE_ELIMINADO", "clienteId", "CLI003");

		kafkaTemplate.send("clientes-eventos", "CLI003", evento);

		await().atMost(Duration.ofSeconds(10))
				.untilAsserted(() -> verify(cuentaService).eliminarCuentasDeCliente("CLI003"));
	}

	// -------------------------------
	// Configuración Kafka embebido
	// -------------------------------
	@Configuration
	@EnableKafka
	@Import(ClienteEventListener.class)
	static class KafkaTestConfig {

		@Bean
		ProducerFactory<String, Object> producerFactory(EmbeddedKafkaBroker broker) {
			Map<String, Object> props = new HashMap<>();
			props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, broker.getBrokersAsString());
			props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
			props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
			props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
			return new DefaultKafkaProducerFactory<>(props);
		}

		@Bean
		KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
			return new KafkaTemplate<>(producerFactory);
		}

		@Bean
		ConsumerFactory<String, Map<String, Object>> consumerFactory(EmbeddedKafkaBroker broker) {
			Map<String, Object> props = new HashMap<>(
					KafkaTestUtils.consumerProps("operaciones-group", "true", broker));
			props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
			props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
			props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
			props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "java.util.HashMap");
			return new DefaultKafkaConsumerFactory<>(props);
		}

		@Bean(name = "kafkaListenerContainerFactory")
		ConcurrentKafkaListenerContainerFactory<String, Map<String, Object>> kafkaListenerContainerFactory(
				ConsumerFactory<String, Map<String, Object>> consumerFactory) {
			ConcurrentKafkaListenerContainerFactory<String, Map<String, Object>> factory = new ConcurrentKafkaListenerContainerFactory<>();
			factory.setConsumerFactory(consumerFactory);
			return factory;
		}

		@Bean
		NewTopic clientesEventosTopic() {
			return new NewTopic("clientes-eventos", 1, (short) 1);
		}
	}
}