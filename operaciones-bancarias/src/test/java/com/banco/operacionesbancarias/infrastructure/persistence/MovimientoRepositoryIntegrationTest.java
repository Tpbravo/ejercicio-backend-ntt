package com.banco.operacionesbancarias.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.banco.operacionesbancarias.domain.model.Cuenta;
import com.banco.operacionesbancarias.domain.model.Movimiento;
import com.banco.operacionesbancarias.domain.model.enums.TipoCuentaEnum;
import com.banco.operacionesbancarias.domain.model.enums.TipoMovimientoEnum;

import jakarta.transaction.Transactional;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DisplayName(" Integración pura JPA - MovimientoRepository")
class MovimientoRepositoryIntegrationTest {

	@Autowired
	private MovimientoRepository movimientoRepository;

	@Autowired
	private TestEntityManager entityManager;

	private Cuenta crearCuenta() {
		Cuenta cuenta = new Cuenta();
		cuenta.setNumeroCuenta("ACC-500");
		cuenta.setClienteId("CLI-500");
		cuenta.setTipoCuenta(TipoCuentaEnum.AHORRO);
		cuenta.setSaldoInicial(BigDecimal.valueOf(1000));
		cuenta.setEstado(true);
		return entityManager.persistFlushFind(cuenta);
	}

	private Movimiento crearMovimiento(Cuenta cuenta, TipoMovimientoEnum tipo, double valor, double saldo,
			int minutosOffset) {
		Movimiento m = new Movimiento();
		m.setCuenta(cuenta);
		m.setFecha(LocalDateTime.now().plusMinutes(minutosOffset));
		m.setTipoMovimiento(tipo);
		m.setValor(BigDecimal.valueOf(valor));
		m.setSaldo(BigDecimal.valueOf(saldo));
		return entityManager.persistFlushFind(m);
	}

	@Test
	@DisplayName("Debe listar movimientos por cuenta ordenados por fecha descendente")
	void debeListarPorCuentaDesc() {
		Cuenta cuenta = crearCuenta();
		crearMovimiento(cuenta, TipoMovimientoEnum.DEPOSITO, 500, 1500, -10);
		crearMovimiento(cuenta, TipoMovimientoEnum.RETIRO, -200, 1300, 0);

		List<Movimiento> movimientos = movimientoRepository.findByCuentaOrderByFechaDesc(cuenta);

		assertEquals(2, movimientos.size());
		assertTrue(movimientos.get(0).getFecha().isAfter(movimientos.get(1).getFecha()));
	}

	@Test
	@DisplayName("Debe listar movimientos por cuenta entre fechas ordenados ascendentemente")
	void debeFiltrarPorRangoDeFechas() {
		Cuenta cuenta = crearCuenta();
		LocalDateTime ahora = LocalDateTime.now();

		crearMovimiento(cuenta, TipoMovimientoEnum.DEPOSITO, 100, 1100, -60);
		crearMovimiento(cuenta, TipoMovimientoEnum.RETIRO, -50, 1050, -30);
		crearMovimiento(cuenta, TipoMovimientoEnum.DEPOSITO, 200, 1250, 0);

		List<Movimiento> rango = movimientoRepository.findByCuentaAndFechaBetweenOrderByFechaAsc(cuenta,
				ahora.minusHours(2), ahora.plusMinutes(10));

		assertEquals(3, rango.size());
		assertTrue(rango.get(0).getFecha().isBefore(rango.get(1).getFecha()));
	}

	@Test
	@DisplayName("Debe obtener el último movimiento por cuentaId")
	void debeObtenerUltimoMovimiento() {
		Cuenta cuenta = crearCuenta();
		crearMovimiento(cuenta, TipoMovimientoEnum.DEPOSITO, 300, 1300, -5);
		Movimiento ultimo = crearMovimiento(cuenta, TipoMovimientoEnum.RETIRO, -100, 1200, 0);

		Optional<Movimiento> result = movimientoRepository.findTopByCuentaIdOrderByFechaDesc(cuenta.getId());

		assertTrue(result.isPresent());
		assertEquals(ultimo.getId(), result.get().getId());
	}

	@Test
	@DisplayName("Debe obtener el último movimiento excluyendo un id")
	void debeObtenerUltimoExcluyendoId() {
		Cuenta cuenta = crearCuenta();
		Movimiento m1 = crearMovimiento(cuenta, TipoMovimientoEnum.DEPOSITO, 300, 1300, -10);
		Movimiento m2 = crearMovimiento(cuenta, TipoMovimientoEnum.RETIRO, -100, 1200, 0);

		Optional<Movimiento> result = movimientoRepository.findTopByCuentaIdAndIdNotOrderByFechaDesc(cuenta.getId(),
				m2.getId());

		assertTrue(result.isPresent());
		assertEquals(m1.getId(), result.get().getId());
	}

	@Test
	@Transactional
	@DisplayName("Debe eliminar todos los movimientos por cuentaId y verificar existencia")
	void debeEliminarYVerificarExistencia() {
		Cuenta cuenta = crearCuenta();
		crearMovimiento(cuenta, TipoMovimientoEnum.DEPOSITO, 200, 1200, 0);

		assertTrue(movimientoRepository.existsByCuentaId(cuenta.getId()));

		movimientoRepository.deleteAllByCuentaId(cuenta.getId());
		entityManager.flush();

		assertFalse(movimientoRepository.existsByCuentaId(cuenta.getId()));
	}
}