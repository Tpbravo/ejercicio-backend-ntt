package com.banco.operacionesbancarias.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.banco.operacionesbancarias.domain.model.enums.TipoMovimientoEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoResponseDTO {
	private Long id;
	private LocalDateTime fecha;
	private TipoMovimientoEnum tipoMovimiento;
	private BigDecimal valor;
	private BigDecimal saldoDisponible;
	private String numeroCuenta;
}
