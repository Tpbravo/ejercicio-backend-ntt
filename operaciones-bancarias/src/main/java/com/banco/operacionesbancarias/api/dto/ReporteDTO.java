package com.banco.operacionesbancarias.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa un registro del reporte de movimientos, combinando datos
 * de la cuenta y el movimiento.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDTO {

	private String clienteNombre;
	private String numeroCuenta;
	private String tipoCuenta;

	private LocalDateTime fecha;
	private String tipoMovimiento;
	private BigDecimal saldoInicial;
	private BigDecimal valor;
	private BigDecimal saldoDisponible;
}
