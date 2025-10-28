package com.banco.operacionesbancarias.integration;

import com.banco.operacionesbancarias.api.dto.ClienteDTO;
import com.banco.operacionesbancarias.infrastructure.client.ClienteServiceClient;
import com.banco.operacionesbancarias.infrastructure.config.MessageConfig;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

@DisplayName("Integración REST - ClienteServiceClient (Operaciones ↔ Gestión de Clientes)")
class ClienteRestIntegrationTest {

	private static WireMockServer wireMockServer;
	private static ClienteServiceClient clienteServiceClient;

	@BeforeAll
	static void iniciarWireMock() {
		// Arrancamos WireMock en un puerto fijo
		wireMockServer = new WireMockServer(9561);
		wireMockServer.start();
		configureFor("localhost", 9561);

		// Creamos el WebClient apuntando al servidor simulado
		WebClient webClient = WebClient.builder().baseUrl("http://localhost:9561/clientes").build();

		// Instanciamos el cliente REST manualmente
		clienteServiceClient = new ClienteServiceClient(webClient);
	}

	@AfterAll
	static void detenerWireMock() {
		if (wireMockServer != null) {
			wireMockServer.stop();
		}
	}

	@BeforeEach
	void limpiarMocks() {
		wireMockServer.resetAll();
	}

	// -------------------------------------------------------------
	// Caso 1: Cliente encontrado (HTTP 200)
	// -------------------------------------------------------------
	@Test
	@DisplayName("Debería retornar ClienteDTO cuando el servicio remoto responde 200 OK")
	void obtenerClientePorClienteId_exitoso() {
		String clienteId = "CLI123";

		stubFor(get(urlEqualTo("/clientes/codigo/" + clienteId))
				.willReturn(aResponse().withHeader("Content-Type", "application/json").withStatus(200).withBody("""
						    {
						      "id": 1,
						      "nombre": "Jose Lema",
						      "genero": "Masculino",
						      "edad": 30,
						      "identificacion": "1234567890",
						      "direccion": "Otavalo sn y principal",
						      "telefono": "098254785",
						      "clienteId": "CLI123",
						      "contrasena": "Password1!",
						      "estado": true
						    }
						""")));

		ClienteDTO cliente = clienteServiceClient.obtenerClientePorClienteId(clienteId).block();

		assertThat(cliente).isNotNull();
		assertThat(cliente.getNombre()).isEqualTo("Jose Lema");
		assertThat(cliente.getClienteId()).isEqualTo("CLI123");
		assertThat(cliente.getEstado()).isTrue();
	}

	// -------------------------------------------------------------
	// Caso 2: Cliente no encontrado (HTTP 404)
	// -------------------------------------------------------------
	@Test
	@DisplayName("Debería lanzar excepción cuando el cliente no existe (404)")
	void obtenerClientePorClienteId_noEncontrado() {
		String clienteId = "CLI999";

		stubFor(get(urlEqualTo("/clientes/codigo/" + clienteId)).willReturn(aResponse().withStatus(404)));

		assertThatThrownBy(() -> clienteServiceClient.obtenerClientePorClienteId(clienteId).block())
				.isInstanceOf(RuntimeException.class).hasMessageContaining("Cliente no encontrado");
	}

	// -------------------------------------------------------------
	// Caso 3: Error del servidor remoto (HTTP 500)
	// -------------------------------------------------------------
	@Test
	@DisplayName("Debería lanzar excepción cuando el servicio remoto responde 500")
	void obtenerClientePorClienteId_errorServidor() {
		String clienteId = "CLI500";

		stubFor(get(urlEqualTo("/clientes/codigo/" + clienteId)).willReturn(aResponse().withStatus(500)));

		assertThatThrownBy(() -> clienteServiceClient.obtenerClientePorClienteId(clienteId).block())
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("Error al consultar el servicio de clientes");
	}
}