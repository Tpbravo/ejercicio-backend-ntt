package com.banco.gestionclientes.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de dominio - Persona")
class PersonaTests {

	private Validator validator;

	@BeforeEach
	void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	private Persona crearPersonaValida() {
		Persona persona = new Persona();
		persona.setNombre("Juan Pérez");
		persona.setGenero(com.banco.gestionclientes.domain.model.enums.Genero.M);
		persona.setEdad(30);
		persona.setIdentificacion("1234567890");
		persona.setDireccion("Calle Falsa 123");
		persona.setTelefono("0991234567");
		return persona;
	}

	@Test
	@DisplayName("Debe ser válida cuando todos los campos son correctos")
	void personaValida() {
		Persona persona = crearPersonaValida();
		Set<ConstraintViolation<Persona>> violaciones = validator.validate(persona);
		assertTrue(violaciones.isEmpty(), "La persona debería ser válida");
	}

	@Test
	@DisplayName("Debe fallar cuando el nombre está vacío")
	void nombreNoDebeEstarVacio() {
		Persona persona = crearPersonaValida();
		persona.setNombre("");
		Set<ConstraintViolation<Persona>> violaciones = validator.validate(persona);
		assertFalse(violaciones.isEmpty());
	}

	@Test
	@DisplayName("Debe fallar cuando el género es nulo")
	void generoNoDebeSerNulo() {
		Persona persona = crearPersonaValida();
		persona.setGenero(null);
		Set<ConstraintViolation<Persona>> violaciones = validator.validate(persona);
		assertFalse(violaciones.isEmpty());
	}

	@Test
	@DisplayName("Debe fallar cuando la edad es negativa")
	void edadNoDebeSerNegativa() {
		Persona persona = crearPersonaValida();
		persona.setEdad(-1);
		Set<ConstraintViolation<Persona>> violaciones = validator.validate(persona);
		assertFalse(violaciones.isEmpty());
	}

	@Test
	@DisplayName("Debe fallar cuando la identificación está vacía")
	void identificacionNoDebeEstarVacia() {
		Persona persona = crearPersonaValida();
		persona.setIdentificacion("");
		Set<ConstraintViolation<Persona>> violaciones = validator.validate(persona);
		assertFalse(violaciones.isEmpty());
	}

	@Test
	@DisplayName("Debe fallar si la dirección o teléfono exceden la longitud permitida")
	void direccionYTelefonoNoDebenExcederLongitud() {
		Persona persona = crearPersonaValida();
		persona.setDireccion("a".repeat(151));
		persona.setTelefono("1".repeat(25));
		Set<ConstraintViolation<Persona>> violaciones = validator.validate(persona);
		assertFalse(violaciones.isEmpty());
	}

}