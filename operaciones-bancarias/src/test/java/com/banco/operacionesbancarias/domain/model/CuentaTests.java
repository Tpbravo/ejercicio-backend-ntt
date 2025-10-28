package com.banco.operacionesbancarias.domain.model;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.banco.operacionesbancarias.domain.model.enums.TipoCuentaEnum;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de dominio - Cuenta")
class CuentaTests {

	private Validator validator;

	@BeforeEach
	void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	private Cuenta crearCuentaValida() {
		return Cuenta.builder().numeroCuenta("1234567890").tipoCuenta(TipoCuentaEnum.AHORRO)
				.saldoInicial(new BigDecimal("1000.00")).estado(true).clienteId("C001").clienteNombre("Juan Pérez")
				.fechaCreacion(LocalDateTime.now()).build();
	}

	@Test
	@DisplayName("Debe crear una cuenta válida sin violaciones")
	void cuentaValida_NoDebeTenerViolaciones() {
		Cuenta cuenta = crearCuentaValida();
		Set<ConstraintViolation<Cuenta>> violaciones = validator.validate(cuenta);
		assertTrue(violaciones.isEmpty(), () -> "No debería haber violaciones, pero hay: " + violaciones);
	}

	@Test
	@DisplayName("Debe fallar si el número de cuenta está vacío")
	void numeroCuentaVacio_DebeGenerarViolacion() {
		Cuenta cuenta = crearCuentaValida();
		cuenta.setNumeroCuenta("");
		Set<ConstraintViolation<Cuenta>> violaciones = validator.validate(cuenta);
		assertFalse(violaciones.isEmpty());
	}

	@Test
	@DisplayName("Debe fallar si el saldo inicial es negativo")
	void saldoInicialNegativo_DebeGenerarViolacion() {
		Cuenta cuenta = crearCuentaValida();
		cuenta.setSaldoInicial(new BigDecimal("-10.00"));
		Set<ConstraintViolation<Cuenta>> violaciones = validator.validate(cuenta);
		assertFalse(violaciones.isEmpty());
	}

	@Test
	@DisplayName("Debe asignar fechaCreacion y valores por defecto en prePersist")
	void prePersistDebeAsignarValoresPorDefecto() {
		Cuenta cuenta = new Cuenta();
		cuenta.setNumeroCuenta("99999999");
		cuenta.setTipoCuenta(TipoCuentaEnum.CORRIENTE);
		cuenta.setClienteId("C009");
		cuenta.setClienteNombre("María López");
		cuenta.prePersist();

		assertNotNull(cuenta.getFechaCreacion());
		assertTrue(cuenta.getEstado(), "Debe tener estado TRUE por defecto");
		assertEquals(BigDecimal.ZERO, cuenta.getSaldoInicial(), "Debe tener saldo 0.00 por defecto");
	}

	@Test
	@DisplayName("No debe sobrescribir fechaCreacion si ya existe")
	void prePersistNoDebeSobrescribirFechaExistente() {
		Cuenta cuenta = crearCuentaValida();
		LocalDateTime fechaOriginal = LocalDateTime.of(2024, 1, 1, 10, 0);
		cuenta.setFechaCreacion(fechaOriginal);
		cuenta.prePersist();
		assertEquals(fechaOriginal, cuenta.getFechaCreacion(), "No debe cambiar la fecha existente");
	}
}