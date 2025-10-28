package com.banco.operacionesbancarias.domain.model;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.banco.operacionesbancarias.domain.model.enums.TipoCuentaEnum;
import com.banco.operacionesbancarias.domain.model.enums.TipoMovimientoEnum;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Tests de dominio - Movimiento")
class MovimientoTests {

	private Validator validator;
	private Cuenta cuentaValida;

	@BeforeEach
	void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();

		cuentaValida = Cuenta.builder().numeroCuenta("1234567890").tipoCuenta(TipoCuentaEnum.AHORRO)
				.saldoInicial(new BigDecimal("1000.00")).estado(true).clienteId("C001").clienteNombre("Juan Pérez")
				.fechaCreacion(LocalDateTime.now()).build();
	}

	private Movimiento crearMovimientoValido() {
		return Movimiento.builder().cuenta(cuentaValida).tipoMovimiento(TipoMovimientoEnum.DEPOSITO)
				.valor(new BigDecimal("200.00")).saldo(new BigDecimal("1200.00")).fecha(LocalDateTime.now()).build();
	}

	@Test
	@DisplayName("Debe crear un movimiento válido sin violaciones")
	void movimientoValido_NoDebeTenerViolaciones() {
		Movimiento movimiento = crearMovimientoValido();
		Set<ConstraintViolation<Movimiento>> violaciones = validator.validate(movimiento);
		assertTrue(violaciones.isEmpty());
	}

	@Test
	@DisplayName("Debe fallar si el valor es nulo")
	void valorNulo_DebeGenerarViolacion() {
		Movimiento movimiento = crearMovimientoValido();
		movimiento.setValor(null);
		Set<ConstraintViolation<Movimiento>> violaciones = validator.validate(movimiento);
		assertFalse(violaciones.isEmpty());
	}

	@Test
	@DisplayName("Debe asignar fecha si es nula en prePersist")
	void prePersistDebeAsignarFecha() {
		Movimiento movimiento = crearMovimientoValido();
		movimiento.setFecha(null);
		movimiento.prePersist();
		assertNotNull(movimiento.getFecha(), "Debe asignar fecha automáticamente");
	}

	@Test
	@DisplayName("No debe sobrescribir fecha existente en prePersist")
	void prePersistNoDebeSobrescribirFechaExistente() {
		Movimiento movimiento = crearMovimientoValido();
		LocalDateTime fechaOriginal = LocalDateTime.of(2024, 1, 1, 12, 0);
		movimiento.setFecha(fechaOriginal);
		movimiento.prePersist();
		assertEquals(fechaOriginal, movimiento.getFecha(), "No debe cambiar la fecha existente");
	}
}