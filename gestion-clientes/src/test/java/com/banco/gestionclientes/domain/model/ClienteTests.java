package com.banco.gestionclientes.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.banco.gestionclientes.domain.model.enums.Genero;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de dominio - Cliente")
class ClienteTests {

	private Validator validator;

	@BeforeEach
	void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	private Cliente crearClienteValido() {
		Cliente cliente = new Cliente();
		cliente.setNombre("María López");
		cliente.setGenero(Genero.F);
		cliente.setEdad(28);
		cliente.setIdentificacion("9999999999");
		cliente.setDireccion("Av. Siempre Viva 742");
		cliente.setTelefono("0987654321");
		cliente.setClienteId("C12345");
		cliente.setContrasena("secreto123");
		cliente.setEstado(true);
		return cliente;
	}

	@Test
	@DisplayName("Debe ser válido cuando todos los campos son correctos")
	void clienteValido() {
		Cliente cliente = crearClienteValido();
		Set<ConstraintViolation<Cliente>> violaciones = validator.validate(cliente);
		assertTrue(violaciones.isEmpty(), "El cliente debería ser válido");
	}

	@Test
	@DisplayName("Debe fallar cuando el clienteId está vacío o supera la longitud")
	void clienteIdInvalido() {
		Cliente cliente = crearClienteValido();
		cliente.setClienteId("");
		Set<ConstraintViolation<Cliente>> violaciones = validator.validate(cliente);
		assertFalse(violaciones.isEmpty());
	}

	@Test
	@DisplayName("Debe fallar cuando la contraseña tiene menos de 6 caracteres")
	void contrasenaMuyCorta() {
		Cliente cliente = crearClienteValido();
		cliente.setContrasena("123");
		Set<ConstraintViolation<Cliente>> violaciones = validator.validate(cliente);
		assertFalse(violaciones.isEmpty());
	}

	@Test
	@DisplayName("Debe fallar cuando el estado es nulo")
	void estadoNoDebeSerNulo() {
		Cliente cliente = crearClienteValido();
		cliente.setEstado(null);
		Set<ConstraintViolation<Cliente>> violaciones = validator.validate(cliente);
		assertFalse(violaciones.isEmpty());
	}

	@Test
	@DisplayName("Debe asignar fechaCreacion automáticamente al llamar prePersist()")
	void prePersistDebeAsignarFechaCreacion() {
		Cliente cliente = crearClienteValido();
		assertNull(cliente.getFechaCreacion(), "Antes de persistir no debe tener fecha");
		cliente.prePersist();
		assertNotNull(cliente.getFechaCreacion(), "Después de persistir debe tener fecha");
		assertTrue(cliente.getFechaCreacion().isBefore(LocalDateTime.now().plusSeconds(1)));
	}

	@Test
	@DisplayName("No debe sobrescribir fechaCreacion si ya existe")
	void prePersistNoDebeSobrescribirFechaExistente() {
		Cliente cliente = crearClienteValido();
		LocalDateTime fechaOriginal = LocalDateTime.of(2024, 1, 1, 10, 0);
		cliente.setFechaCreacion(fechaOriginal);
		cliente.prePersist();
		assertEquals(fechaOriginal, cliente.getFechaCreacion(), "No debe cambiar la fecha existente");
	}

	@Test
	@DisplayName("Debe detectar múltiples violaciones en un cliente inválido")
	void multiplesViolaciones() {
		Cliente cliente = new Cliente(); // vacío
		Set<ConstraintViolation<Cliente>> violaciones = validator.validate(cliente);
		assertTrue(violaciones.size() > 1, "Debe haber múltiples errores de validación");
	}
}