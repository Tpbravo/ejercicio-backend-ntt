package com.banco.operacionesbancarias.api.controller;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.banco.operacionesbancarias.api.dto.CuentaDTO;
import com.banco.operacionesbancarias.domain.model.Cuenta;
import com.banco.operacionesbancarias.domain.model.enums.TipoCuentaEnum;
import com.banco.operacionesbancarias.domain.service.CuentaService;
import com.banco.operacionesbancarias.infrastructure.config.MessageConfig;
import com.banco.operacionesbancarias.infrastructure.config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CuentaController.class)
@Import({ SecurityConfig.class, MessageConfig.class })

@DisplayName("API - CuentaController")
class CuentaControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CuentaService cuentaService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("POST /cuentas debe crear una cuenta y retornar 201 con datos válidos")
	void crearCuentaDebeRetornarCreated() throws Exception {
		// DTO enviado por el cliente
		CuentaDTO dto = new CuentaDTO();
		dto.setNumeroCuenta("ACC-999");
		dto.setTipoCuenta(TipoCuentaEnum.AHORRO);
		dto.setSaldoInicial(BigDecimal.valueOf(500));
		dto.setEstado(true);
		dto.setClienteId("CLI-01");

		// Entidad simulada que devuelve el servicio
		Cuenta cuenta = new Cuenta();
		cuenta.setId(1L);
		cuenta.setNumeroCuenta("ACC-999");
		cuenta.setTipoCuenta(TipoCuentaEnum.AHORRO);
		cuenta.setSaldoInicial(BigDecimal.valueOf(500));
		cuenta.setEstado(true);
		cuenta.setClienteId("CLI-01");

		when(cuentaService.crear(any(Cuenta.class))).thenReturn(cuenta);

		mockMvc.perform(MockMvcRequestBuilders.post("/cuentas").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.numeroCuenta").value("ACC-999"))
				.andExpect(jsonPath("$.message").value("Cuenta creada correctamente"));
	}

	@Test
	@DisplayName("GET /cuentas/{id} debe retornar 404 si la cuenta no existe")
	void buscarPorIdDebeRetornarNotFound() throws Exception {
		when(cuentaService.buscarPorId(1L)).thenReturn(Optional.empty());

		mockMvc.perform(MockMvcRequestBuilders.get("/cuentas/1")).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("Cuenta no encontrada"));
	}

	@Test
	@DisplayName("GET /cuentas debe retornar listado de cuentas con 200 OK")
	void listarCuentasDebeRetornarOk() throws Exception {
		Cuenta cuenta = new Cuenta();
		cuenta.setId(1L);
		cuenta.setNumeroCuenta("ACC-123");
		cuenta.setTipoCuenta(TipoCuentaEnum.CORRIENTE);
		cuenta.setSaldoInicial(BigDecimal.valueOf(1000));
		cuenta.setEstado(true);
		cuenta.setClienteId("CLI-01");

		when(cuentaService.listar()).thenReturn(List.of(cuenta));

		mockMvc.perform(MockMvcRequestBuilders.get("/cuentas")).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data[0].numeroCuenta").value("ACC-123"))
				.andExpect(jsonPath("$.message").value("Listado de cuentas obtenido correctamente"));
	}
}
