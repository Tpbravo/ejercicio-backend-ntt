package com.banco.operacionesbancarias.api.controller;

import com.banco.operacionesbancarias.api.dto.MovimientoResponseDTO;
import com.banco.operacionesbancarias.api.dto.ReporteDTO;
import com.banco.operacionesbancarias.domain.model.enums.TipoCuentaEnum;
import com.banco.operacionesbancarias.domain.model.enums.TipoMovimientoEnum;
import com.banco.operacionesbancarias.domain.service.ReporteService;
import com.banco.operacionesbancarias.infrastructure.config.MessageConfig;
import com.banco.operacionesbancarias.infrastructure.config.SecurityConfig;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReporteController.class)
@Import({ SecurityConfig.class, MessageConfig.class })
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("API - ReporteController")
class ReporteControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ReporteService reporteService;

	// Caso 1: Éxito - Reporte generado correctamente
	@Test
	@DisplayName("Debería retornar 200 OK y reporte generado correctamente")
	void generarReporte_exitoso() throws Exception {
		// Datos simulados
		MovimientoResponseDTO movimiento = new MovimientoResponseDTO(1L, LocalDateTime.of(2025, 10, 20, 10, 30),
				TipoMovimientoEnum.DEPOSITO, new BigDecimal("200.00"), new BigDecimal("1200.00"), "478758");

		ReporteDTO reporteDTO = ReporteDTO.builder().clienteNombre("Jose Lema").numeroCuenta("478758")
				.tipoCuenta(TipoCuentaEnum.AHORRO).saldoInicial(new BigDecimal("1000.00")).estado(true)
				.movimientos(List.of(movimiento)).build();

		List<ReporteDTO> reporteList = Collections.singletonList(reporteDTO);

		// Mock del servicio
		when(reporteService.generarReporte("123", LocalDate.of(2025, 10, 1), LocalDate.of(2025, 10, 27)))
				.thenReturn(reporteList);

		// Ejecución del request
		mockMvc.perform(get("/reportes").param("clienteId", "123").param("fechaInicio", "2025-10-01")
				.param("fechaFin", "2025-10-27").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Reporte generado correctamente"))
				.andExpect(jsonPath("$.data[0].clienteNombre").value("Jose Lema"))
				.andExpect(jsonPath("$.data[0].numeroCuenta").value("478758"))
				.andExpect(jsonPath("$.data[0].tipoCuenta").value("AHORRO"))
				.andExpect(jsonPath("$.data[0].movimientos[0].valor").value(200.00));
	}

	// Caso 2: Error - Fecha de inicio posterior a fecha fin
	@Test
	@DisplayName("Debería retornar 400 BAD_REQUEST cuando fechaInicio > fechaFin")
	void generarReporte_fechasInvalidas() throws Exception {
		mockMvc.perform(get("/reportes").param("clienteId", "123").param("fechaInicio", "2025-10-27")
				.param("fechaFin", "2025-10-01").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("La fecha de inicio no puede ser posterior a la fecha fin"));
	}
}
