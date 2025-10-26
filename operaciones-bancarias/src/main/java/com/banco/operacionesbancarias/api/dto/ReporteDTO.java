package com.banco.operacionesbancarias.api.dto;

import java.math.BigDecimal;
import java.util.List;

import com.banco.operacionesbancarias.domain.model.enums.TipoCuentaEnum;

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
	private TipoCuentaEnum tipoCuenta;
	private BigDecimal saldoInicial;
	private Boolean estado;
	private List<MovimientoResponseDTO> movimientos;
}
