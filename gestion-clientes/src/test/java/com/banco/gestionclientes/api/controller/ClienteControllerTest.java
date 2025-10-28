package com.banco.gestionclientes.api.controller;

import com.banco.gestionclientes.api.dto.ClienteDTO;
import com.banco.gestionclientes.domain.model.Cliente;
import com.banco.gestionclientes.domain.model.enums.Genero;
import com.banco.gestionclientes.domain.service.ClienteService;
import com.banco.gestionclientes.infrastructure.config.MessageConfig;
import com.banco.gestionclientes.infrastructure.config.SecurityConfig;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
@Import({ SecurityConfig.class, MessageConfig.class })
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("API - ClienteController")
class ClienteControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private ClienteService clienteService;

	// Caso 1: Crear cliente (POST /clientes)
	@Test
	@DisplayName("Debería crear un cliente correctamente y retornar 201 CREATED")
	void crearCliente_exitoso() throws Exception {
		ClienteDTO dto = new ClienteDTO();
		dto.setNombre("Jose Lema");
		dto.setGenero(Genero.M);
		dto.setEdad(30);
		dto.setIdentificacion("1234567890");
		dto.setDireccion("Otavalo sn y principal");
		dto.setTelefono("098254785");
		dto.setClienteId("CLI123");
		dto.setContrasena("Password1!");
		dto.setEstado(true);

		Cliente clienteGuardado = new Cliente();
		clienteGuardado.setId(1L);
		clienteGuardado.setNombre("Jose Lema");

		when(clienteService.guardar(org.mockito.ArgumentMatchers.any(Cliente.class))).thenReturn(clienteGuardado);

		mockMvc.perform(
				post("/clientes").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Cliente creado correctamente"))
				.andExpect(jsonPath("$.data.nombre").value("Jose Lema"));
	}

	// Caso 2: Listar clientes (GET /clientes)
	@Test
	@DisplayName("Debería listar clientes y retornar 200 OK")
	void listarClientes_exitoso() throws Exception {
		Cliente cliente = new Cliente();
		cliente.setId(1L);
		cliente.setNombre("Jose Lema");

		when(clienteService.listar()).thenReturn(List.of(cliente));

		mockMvc.perform(get("/clientes").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Listado de clientes obtenido correctamente"))
				.andExpect(jsonPath("$.data[0].nombre").value("Jose Lema"));
	}

	// Caso 3: Obtener cliente por código inexistente (GET
	// /clientes/codigo/{clienteId})
	@Test
	@DisplayName("Debería retornar 404 NOT_FOUND cuando cliente no existe")
	void obtenerClientePorCodigo_noEncontrado() throws Exception {
		when(clienteService.buscarPorClienteId("CLI999")).thenReturn(Optional.empty());

		mockMvc.perform(get("/clientes/codigo/CLI999").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}
}