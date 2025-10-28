package com.banco.gestionclientes.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.banco.gestionclientes.domain.model.Cliente;
import com.banco.gestionclientes.domain.model.enums.Genero;
import com.banco.gestionclientes.infrastructure.messaging.ClienteEventProducer;
import com.banco.gestionclientes.infrastructure.persistence.ClienteRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de servicio - ClienteServiceImpl")
class ClienteServiceTests {

	@Mock
	private ClienteRepository repository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private ClienteEventProducer eventProducer;

	@InjectMocks
	private ClienteServiceImpl service;

	private Cliente clienteBase;

	@BeforeEach
	void init() {
		clienteBase = new Cliente();
		clienteBase.setId(1L);
		clienteBase.setClienteId("CLI-1234");
		clienteBase.setNombre("Juan Pérez");
		clienteBase.setGenero(Genero.M);
		clienteBase.setEdad(30);
		clienteBase.setIdentificacion("1234567890");
		clienteBase.setDireccion("Av. Principal 123");
		clienteBase.setTelefono("0999999999");
		clienteBase.setContrasena("12345");
		clienteBase.setEstado(true);
	}

	// GUARDAR
	@Test
	@DisplayName("guardar(): debe codificar contraseña y generar clienteId si no existe")
	void guardar_DebeGenerarClienteIdYCodificarPassword() {
		// Given
		Cliente nuevo = new Cliente();
		nuevo.setNombre("Ana Torres");
		nuevo.setContrasena("abc123");
		nuevo.setGenero(Genero.F);
		nuevo.setEdad(28);
		nuevo.setIdentificacion("ABC12345");
		nuevo.setEstado(true);

		when(passwordEncoder.encode("abc123")).thenReturn("ENCODED");
		when(repository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

		// When
		Cliente resultado = service.guardar(nuevo);

		// Then
		assertNotNull(resultado.getClienteId(), "Debe generar un clienteId automáticamente");
		assertTrue(resultado.getClienteId().startsWith("CLI-"));
		assertEquals("ENCODED", resultado.getContrasena());
		verify(repository).save(any(Cliente.class));
	}

	// ACTUALIZAR
	@Test
	@DisplayName("actualizar(): debe actualizar campos y publicar evento de activación/desactivación")
	void actualizar_DebeActualizarYPublicarEvento() {
		Cliente existente = new Cliente();
		existente.setId(1L);
		existente.setEstado(false);
		existente.setClienteId("CLI-9999");
		existente.setContrasena("OLD_PASS");
		existente.setContrasenaAnterior("PREV_PASS");

		Cliente actualizacion = new Cliente();
		actualizacion.setEstado(true);
		actualizacion.setContrasena("nueva123");

		when(repository.findById(1L)).thenReturn(Optional.of(existente));
		when(passwordEncoder.matches("nueva123", "OLD_PASS")).thenReturn(false);
		when(passwordEncoder.matches("nueva123", "PREV_PASS")).thenReturn(false);
		when(passwordEncoder.encode("nueva123")).thenReturn("ENCODED_PASS");
		when(repository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

		Cliente actualizado = service.actualizar(1L, actualizacion);

		assertTrue(actualizado.getEstado());
		assertEquals("ENCODED_PASS", actualizado.getContrasena());
		verify(eventProducer).publicarClienteActivado(anyString());
	}

	@Test
	@DisplayName("actualizar(): debe lanzar excepción si nueva contraseña es igual a la actual")
	void actualizar_ContraseñaRepetidaDebeFallar() {
		Cliente existente = new Cliente();
		existente.setId(1L);
		existente.setContrasena("ENCODED_PASS");
		existente.setEstado(true);

		Cliente actualizacion = new Cliente();
		actualizacion.setContrasena("igual");

		when(repository.findById(1L)).thenReturn(Optional.of(existente));
		when(passwordEncoder.matches("igual", "ENCODED_PASS")).thenReturn(true);

		assertThrows(IllegalArgumentException.class, () -> service.actualizar(1L, actualizacion));

		verify(repository, never()).save(any());
	}

	@Test
	@DisplayName("actualizar(): debe lanzar excepción si cliente no existe")
	void actualizar_ClienteNoExisteDebeFallar() {
		when(repository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> service.actualizar(999L, clienteBase));

		verify(repository, never()).save(any());
	}

	// ELIMINAR
	@Test
	@DisplayName("eliminar(): debe borrar cliente y publicar evento de eliminación")
	void eliminar_DebeEliminarYPublicarEvento() {
		when(repository.findById(1L)).thenReturn(Optional.of(clienteBase));

		service.eliminar(1L);

		verify(eventProducer).publicarClienteEliminado("CLI-1234");
		verify(repository).delete(clienteBase);
	}

	@Test
	@DisplayName("eliminar(): debe lanzar ResourceNotFoundException si cliente no existe")
	void eliminar_ClienteNoExisteDebeLanzarExcepcion() {
		when(repository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> service.eliminar(1L));

		verify(repository, never()).delete(any());
	}

	// BUSQUEDA
	@Test
	@DisplayName("buscarPorIdOrThrow(): debe devolver cliente si existe o lanzar excepción")
	void buscarPorIdOrThrow_DebeFuncionarCorrectamente() {
		when(repository.findById(1L)).thenReturn(Optional.of(clienteBase));
		when(repository.findById(999L)).thenReturn(Optional.empty());

		Cliente encontrado = service.buscarPorIdOrThrow(1L);
		assertEquals("CLI-1234", encontrado.getClienteId());

		assertThrows(ResourceNotFoundException.class, () -> service.buscarPorIdOrThrow(999L));
	}
}