package com.banco.operacionesbancarias.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.banco.operacionesbancarias.api.dto.MovimientoDTO;
import com.banco.operacionesbancarias.domain.model.*;
import com.banco.operacionesbancarias.domain.model.enums.TipoMovimientoEnum;
import com.banco.operacionesbancarias.infrastructure.persistence.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de servicio - MovimientoServiceImpl")
class MovimientoServiceTests {

	@Mock
	private MovimientoRepository movimientoRepository;

	@Mock
	private CuentaRepository cuentaRepository;

	@InjectMocks
	private MovimientoServiceImpl service;

	private Cuenta cuenta;

	@BeforeEach
	void init() {
		cuenta = new Cuenta();
		cuenta.setId(1L);
		cuenta.setNumeroCuenta("123");
		cuenta.setSaldoInicial(BigDecimal.valueOf(500));
		cuenta.setEstado(true);
	}

	@Test
	@DisplayName("registrarMovimiento(): debe registrar un depósito correctamente")
	void registrarDepositoDebeIncrementarSaldo() {
		MovimientoDTO dto = MovimientoDTO.builder().numeroCuenta("123").valor(BigDecimal.valueOf(200))
				.tipoMovimiento(TipoMovimientoEnum.DEPOSITO).build();
		when(cuentaRepository.findByNumeroCuenta("123")).thenReturn(Optional.of(cuenta));
		when(movimientoRepository.findTopByCuentaIdOrderByFechaDesc(1L)).thenReturn(Optional.empty());
		when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(inv -> inv.getArgument(0));

		Movimiento mov = service.registrarMovimiento(dto);

		assertEquals(BigDecimal.valueOf(700), mov.getSaldo());
		assertTrue(mov.getValor().compareTo(BigDecimal.ZERO) > 0);
	}

	@Test
	@DisplayName("registrarMovimiento(): debe lanzar error si cuenta inactiva")
	void registrarMovimientoCuentaInactivaDebeFallar() {
		cuenta.setEstado(false);
		MovimientoDTO dto = MovimientoDTO.builder().numeroCuenta("123").valor(BigDecimal.valueOf(100))
				.tipoMovimiento(TipoMovimientoEnum.DEPOSITO).build();
		when(cuentaRepository.findByNumeroCuenta("123")).thenReturn(Optional.of(cuenta));

		assertThrows(RuntimeException.class, () -> service.registrarMovimiento(dto));
	}

	@Test
	@DisplayName("registrarMovimiento(): debe lanzar error si saldo insuficiente")
	void registrarRetiroSinSaldoDebeFallar() {
		MovimientoDTO dto = MovimientoDTO.builder().numeroCuenta("123").valor(BigDecimal.valueOf(1000))
				.tipoMovimiento(TipoMovimientoEnum.RETIRO).build();
		when(cuentaRepository.findByNumeroCuenta("123")).thenReturn(Optional.of(cuenta));
		when(movimientoRepository.findTopByCuentaIdOrderByFechaDesc(1L)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> service.registrarMovimiento(dto));
	}

	@Test
	@DisplayName("actualizarMovimiento(): solo debe permitir actualizar el último")
	void actualizarMovimientoSoloUltimoDebeFuncionar() {
		Movimiento mov = Movimiento.builder().id(10L).cuenta(cuenta).tipoMovimiento(TipoMovimientoEnum.DEPOSITO)
				.valor(BigDecimal.valueOf(100)).saldo(BigDecimal.valueOf(600)).fecha(LocalDateTime.now()).build();

		when(movimientoRepository.findById(10L)).thenReturn(Optional.of(mov));
		when(movimientoRepository.findByCuentaIdOrderByFechaDesc(1L)).thenReturn(List.of(mov));
		when(movimientoRepository.findTopByCuentaIdAndIdNotOrderByFechaDesc(1L, 10L)).thenReturn(Optional.empty());
		when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(inv -> inv.getArgument(0));

		MovimientoDTO dto = MovimientoDTO.builder().numeroCuenta("123").valor(BigDecimal.valueOf(50))
				.tipoMovimiento(TipoMovimientoEnum.RETIRO).build();
		Movimiento actualizado = service.actualizarMovimiento(10L, dto);

		assertEquals(BigDecimal.valueOf(450), actualizado.getSaldo());
		assertEquals(TipoMovimientoEnum.RETIRO, actualizado.getTipoMovimiento());
	}
}