package com.banco.operacionesbancarias.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MovimientoDTO {

	private Long id;

	@NotNull(message = "{cuentaId.notnull}")
	private Long cuentaId;

	private LocalDateTime fecha;

	@NotBlank(message = "{tipoMovimiento.notblank}")
	@Size(max = 20, message = "{tipoMovimiento.size}")
	private String tipoMovimiento; // "Depósito" o "Retiro"

	@NotNull(message = "{valor.notnull}")
	@Digits(integer = 12, fraction = 2, message = "{valor.digits}")
	private BigDecimal valor;

	private BigDecimal saldoDisponible;
}
