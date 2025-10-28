package com.banco.operacionesbancarias.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.banco.operacionesbancarias.api.dto.MovimientoDTO;
import com.banco.operacionesbancarias.domain.model.Cuenta;
import com.banco.operacionesbancarias.domain.model.Movimiento;
import com.banco.operacionesbancarias.domain.model.enums.TipoMovimientoEnum;
import com.banco.operacionesbancarias.domain.service.MovimientoService;
import com.banco.operacionesbancarias.infrastructure.config.MessageConfig;
import com.banco.operacionesbancarias.infrastructure.config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(MovimientoController.class)
@Import({ SecurityConfig.class, MessageConfig.class })
@DisplayName("API - MovimientoController")
class MovimientoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private MovimientoService movimientoService;

	@Autowired
	private ObjectMapper objectMapper;

	private Movimiento buildMovimiento() {
		Cuenta cuenta = new Cuenta();
		cuenta.setNumeroCuenta("ACC-500");

		Movimiento movimiento = new Movimiento();
		movimiento.setId(1L);
		movimiento.setCuenta(cuenta);
		movimiento.setTipoMovimiento(TipoMovimientoEnum.DEPOSITO);
		movimiento.setValor(BigDecimal.valueOf(100));
		movimiento.setSaldo(BigDecimal.valueOf(1100));
		movimiento.setFecha(LocalDateTime.now());
		return movimiento;
	}

	@Test
	@DisplayName("POST /movimientos debe registrar un movimiento y retornar 201")
	void registrarMovimientoDebeRetornarCreated() throws Exception {
		MovimientoDTO dto = MovimientoDTO.builder().numeroCuenta("ACC-500").tipoMovimiento(TipoMovimientoEnum.DEPOSITO)
				.valor(BigDecimal.valueOf(100)).build();

		when(movimientoService.registrarMovimiento(any(MovimientoDTO.class))).thenReturn(buildMovimiento());

		mockMvc.perform(MockMvcRequestBuilders.post("/movimientos").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Movimiento registrado correctamente"));
	}

	@Test
	@DisplayName("GET /movimientos/cuenta/{id} debe retornar lista de movimientos")
	void listarPorCuentaDebeRetornarOk() throws Exception {
		when(movimientoService.listarPorCuenta(1L)).thenReturn(List.of(buildMovimiento()));

		mockMvc.perform(MockMvcRequestBuilders.get("/movimientos/cuenta/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Movimientos obtenidos correctamente"));
	}

	@Test
	@DisplayName("PUT /movimientos/{id} debe actualizar movimiento y retornar 200")
	void actualizarMovimientoDebeRetornarOk() throws Exception {
		MovimientoDTO dto = MovimientoDTO.builder().id(1L).numeroCuenta("ACC-500")
				.tipoMovimiento(TipoMovimientoEnum.RETIRO).valor(BigDecimal.valueOf(50)).build();

		when(movimientoService.actualizarMovimiento(eq(1L), any(MovimientoDTO.class))).thenReturn(buildMovimiento());

		mockMvc.perform(MockMvcRequestBuilders.put("/movimientos/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Movimiento actualizado correctamente"));
	}

	@Test
	@DisplayName("DELETE /movimientos/{id} debe eliminar y retornar 200")
	void eliminarMovimientoDebeRetornarOk() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/movimientos/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Movimiento eliminado correctamente"));
	}

	@Test
	@DisplayName("PUT /movimientos/{id} debe lanzar error 400 si el id del cuerpo no coincide")
	void actualizarMovimientoDebeLanzarErrorSiIdNoCoincide() throws Exception {
		MovimientoDTO dto = MovimientoDTO.builder().id(99L).numeroCuenta("ACC-500")
				.tipoMovimiento(TipoMovimientoEnum.RETIRO).valor(BigDecimal.valueOf(50)).build();

		mockMvc.perform(MockMvcRequestBuilders.put("/movimientos/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest());
	}
}