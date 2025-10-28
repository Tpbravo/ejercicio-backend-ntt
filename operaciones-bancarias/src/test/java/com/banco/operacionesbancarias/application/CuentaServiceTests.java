package com.banco.operacionesbancarias.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.banco.operacionesbancarias.api.dto.ClienteDTO;
import com.banco.operacionesbancarias.domain.model.Cuenta;
import com.banco.operacionesbancarias.domain.model.enums.TipoCuentaEnum;
import com.banco.operacionesbancarias.infrastructure.client.ClienteServiceClient;
import com.banco.operacionesbancarias.infrastructure.persistence.CuentaRepository;
import com.banco.operacionesbancarias.infrastructure.persistence.MovimientoRepository;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de servicio - CuentaServiceImpl")
class CuentaServiceTests {

	@Mock
	private CuentaRepository cuentaRepository;

	@Mock
	private MovimientoRepository movimientoRepository;

	@Mock
	private ClienteServiceClient clienteServiceClient;

	@InjectMocks
	private CuentaServiceImpl service;

	private Cuenta cuentaBase;

	@BeforeEach
	void setup() {
		cuentaBase = new Cuenta();
		cuentaBase.setId(1L);
		cuentaBase.setNumeroCuenta("123-ABC");
		cuentaBase.setClienteId("CLI-001");
		cuentaBase.setClienteNombre("Juan Pérez");
		cuentaBase.setSaldoInicial(BigDecimal.valueOf(500));
		cuentaBase.setTipoCuenta(TipoCuentaEnum.AHORRO);
		cuentaBase.setEstado(true);
	}

	// CREAR
	// -------------------------------------------------------------
	@Test
	@DisplayName("crear(): debe crear una cuenta si no existe y el cliente es válido")
	void crearCuentaDebeGuardarSiClienteExiste() {
		when(cuentaRepository.existsByNumeroCuenta("123-ABC")).thenReturn(false);
		when(clienteServiceClient.obtenerClientePorClienteId("CLI-001"))
				.thenReturn(Mono.just(ClienteDTO.builder().clienteId("CLI-001").nombre("Juan Pérez").build()));
		when(cuentaRepository.save(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));

		Cuenta creada = service.crear(cuentaBase);

		assertEquals("Juan Pérez", creada.getClienteNombre());
		assertEquals(BigDecimal.valueOf(500), creada.getSaldoInicial());
		verify(cuentaRepository).save(any(Cuenta.class));
	}

	@Test
	@DisplayName("crear(): debe lanzar excepción si número de cuenta ya existe")
	void crearCuentaDuplicadaDebeFallar() {
		when(cuentaRepository.existsByNumeroCuenta("123-ABC")).thenReturn(true);

		assertThrows(RuntimeException.class, () -> service.crear(cuentaBase));
		verify(cuentaRepository, never()).save(any());
	}

	@Test
	@DisplayName("crear(): debe lanzar excepción si cliente no existe en gestión-clientes")
	void crearCuentaClienteNoExisteDebeFallar() {
		when(cuentaRepository.existsByNumeroCuenta("123-ABC")).thenReturn(false);
		when(clienteServiceClient.obtenerClientePorClienteId("CLI-001")).thenReturn(Mono.empty());

		assertThrows(RuntimeException.class, () -> service.crear(cuentaBase));
	}

	// -------------------------------------------------------------
	// ACTUALIZAR
	// -------------------------------------------------------------
	@Test
	@DisplayName("actualizar(): debe actualizar campos mutables correctamente")
	void actualizarCuentaDebeActualizarCampos() {
		Cuenta actualizada = new Cuenta();
		actualizada.setNumeroCuenta("123-ABC");
		actualizada.setClienteId("CLI-001");
		actualizada.setTipoCuenta(TipoCuentaEnum.CORRIENTE);
		actualizada.setSaldoInicial(BigDecimal.valueOf(800));
		actualizada.setEstado(false);

		when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaBase));
		when(movimientoRepository.existsByCuentaId(1L)).thenReturn(false);
		when(cuentaRepository.save(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));

		Cuenta resultado = service.actualizar(1L, actualizada);

		assertEquals(TipoCuentaEnum.CORRIENTE, resultado.getTipoCuenta());
		assertEquals(BigDecimal.valueOf(800), resultado.getSaldoInicial());
		assertFalse(resultado.getEstado());
		verify(cuentaRepository).save(any(Cuenta.class));
	}

	@Test
	@DisplayName("actualizar(): debe lanzar excepción si intenta cambiar cliente o número de cuenta")
	void actualizarCamposInmutablesDebeFallar() {
		Cuenta actualizada = new Cuenta();
		actualizada.setNumeroCuenta("999-XYZ");
		actualizada.setClienteId("CLI-002");

		when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaBase));

		assertThrows(RuntimeException.class, () -> service.actualizar(1L, actualizada));
	}

	@Test
	@DisplayName("actualizar(): no debe permitir modificar saldo si hay movimientos")
	void actualizarSaldoConMovimientosDebeFallar() {
		Cuenta actualizada = new Cuenta();
		actualizada.setNumeroCuenta("123-ABC");
		actualizada.setClienteId("CLI-001");
		actualizada.setSaldoInicial(BigDecimal.valueOf(1000));

		when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaBase));
		when(movimientoRepository.existsByCuentaId(1L)).thenReturn(true);

		assertThrows(RuntimeException.class, () -> service.actualizar(1L, actualizada));
	}

	// -------------------------------------------------------------
	// ELIMINAR
	// -------------------------------------------------------------
	@Test
	@DisplayName("eliminar(): debe eliminar cuenta y movimientos asociados")
	void eliminarCuentaDebeBorrarMovimientos() {
		when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaBase));

		service.eliminar(1L);

		verify(movimientoRepository).deleteAllByCuentaId(1L);
		verify(cuentaRepository).delete(cuentaBase);
	}

	@Test
	@DisplayName("eliminar(): debe lanzar excepción si la cuenta no existe")
	void eliminarCuentaNoExisteDebeFallar() {
		when(cuentaRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> service.eliminar(99L));
	}
}