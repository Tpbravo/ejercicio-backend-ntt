package com.banco.gestionclientes.integration.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;

import com.banco.gestionclientes.infrastructure.messaging.ClienteEventProducer;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = { "clientes-eventos" })
@DisplayName("Kafka Producer - Publicación de eventos de cliente")
class ClienteEventProducerTest {

	@Autowired
	private ClienteEventProducer clienteEventProducer;

	@Autowired
	private EmbeddedKafkaBroker embeddedKafkaBroker;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	@DisplayName("Debería publicar un evento CLIENTE_DESACTIVADO correctamente")
	void deberiaPublicarEventoClienteDesactivado() throws Exception {
		String clienteId = "CLI001";
		System.out.println("Broker en uso: " + embeddedKafkaBroker.getBrokersAsString());

		// ✅ Crear KafkaTemplate con serializadores correctos
		Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
		producerProps.put("key.serializer", StringSerializer.class);
		producerProps.put("value.serializer", JsonSerializer.class);
		producerProps.put("spring.json.trusted.packages", "*");

		KafkaTemplate<String, Object> kafkaTemplate = new KafkaTemplate<>(
				new DefaultKafkaProducerFactory<>(producerProps));
		kafkaTemplate.setDefaultTopic("clientes-eventos");

		// 🔧 Inyectar template al producer
		Field field = ClienteEventProducer.class.getDeclaredField("kafkaTemplate");
		field.setAccessible(true);
		field.set(clienteEventProducer, kafkaTemplate);

		// Crear consumer
		Map<String, Object> props = new HashMap<>(
				KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker));
		props.put("key.deserializer", StringDeserializer.class);
		props.put("value.deserializer", StringDeserializer.class);

		try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
			consumer.subscribe(Collections.singletonList("clientes-eventos"));
			Thread.sleep(1000);

			// 🧩 Publicar evento
			clienteEventProducer.publicarClienteDesactivado(clienteId);

			ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10));
			assertThat(records.count()).as("Debe haberse recibido al menos un mensaje").isGreaterThan(0);

			ConsumerRecord<String, String> record = records.iterator().next();
			System.out.println("Mensaje recibido: " + record.value());

			@SuppressWarnings("unchecked")
			Map<String, Object> evento = objectMapper.readValue(record.value(), Map.class);

			assertThat(record.key()).isEqualTo(clienteId);
			assertThat(evento.get("evento")).isEqualTo("CLIENTE_DESACTIVADO");
			assertThat(evento.get("clienteId")).isEqualTo(clienteId);
			assertThat(evento.get("fecha")).isNotNull();
		}
	}
}