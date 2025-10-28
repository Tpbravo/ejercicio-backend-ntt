package com.banco.operacionesbancarias.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.banco.operacionesbancarias.domain.model.enums.TipoMovimientoEnum;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MovimientoDTO {

	private Long id;

	private LocalDateTime fecha;

	@NotBlank(message = "El número de cuenta es obligatorio")
	private String numeroCuenta;

	@NotNull(message = "El tipo de movimiento es obligatorio")
	private TipoMovimientoEnum tipoMovimiento;

	@NotNull(message = "El valor es obligatorio")
	@Digits(integer = 12, fraction = 2, message = "Formato de valor inválido")
	private BigDecimal valor;
}
