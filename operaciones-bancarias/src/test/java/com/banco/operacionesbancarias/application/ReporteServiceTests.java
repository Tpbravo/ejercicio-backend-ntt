package com.banco.operacionesbancarias.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.banco.operacionesbancarias.api.dto.*;
import com.banco.operacionesbancarias.domain.model.*;
import com.banco.operacionesbancarias.domain.model.enums.*;
import com.banco.operacionesbancarias.domain.service.CuentaService;
import com.banco.operacionesbancarias.infrastructure.persistence.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de servicio - ReporteServiceImpl")
class ReporteServiceTests {

	@Mock
	private CuentaRepository cuentaRepository;
	@Mock
	private MovimientoRepository movimientoRepository;
	@Mock
	private CuentaService cuentaService;
	@InjectMocks
	private ReporteServiceImpl service;

	private Cuenta cuenta;

	@BeforeEach
	void setup() {
		cuenta = new Cuenta();
		cuenta.setId(1L);
		cuenta.setNumeroCuenta("123");
		cuenta.setClienteId("CLI-1");
		cuenta.setClienteNombre("Juan Pérez");
		cuenta.setSaldoInicial(BigDecimal.valueOf(1000));
		cuenta.setTipoCuenta(TipoCuentaEnum.AHORRO);
		cuenta.setEstado(true);
	}

	@Test
	@DisplayName("generarReporte(): debe generar reporte con movimientos dentro del rango")
	void generarReporteDebeRetornarListaDeReportes() {
		Movimiento mov = Movimiento.builder().id(1L).fecha(LocalDateTime.now()).valor(BigDecimal.valueOf(100))
				.saldo(BigDecimal.valueOf(1100)).tipoMovimiento(TipoMovimientoEnum.DEPOSITO).cuenta(cuenta).build();

		when(cuentaRepository.findByClienteId("CLI-1")).thenReturn(List.of(cuenta));
		when(movimientoRepository.findByCuentaAndFechaBetweenOrderByFechaAsc(any(), any(), any()))
				.thenReturn(List.of(mov));

		List<ReporteDTO> reportes = service.generarReporte("CLI-1", LocalDate.now().minusDays(1), LocalDate.now());

		assertEquals(1, reportes.size());
		ReporteDTO reporte = reportes.get(0);
		assertEquals("Juan Pérez", reporte.getClienteNombre());
		assertEquals("123", reporte.getNumeroCuenta());
		assertEquals(1, reporte.getMovimientos().size());
	}

	@Test
	@DisplayName("generarReporte(): debe lanzar excepción si no hay cuentas")
	void generarReporteSinCuentasDebeFallar() {
		when(cuentaRepository.findByClienteId("CLI-1")).thenReturn(Collections.emptyList());

		assertThrows(RuntimeException.class,
				() -> service.generarReporte("CLI-1", LocalDate.now().minusDays(1), LocalDate.now()));
	}
}