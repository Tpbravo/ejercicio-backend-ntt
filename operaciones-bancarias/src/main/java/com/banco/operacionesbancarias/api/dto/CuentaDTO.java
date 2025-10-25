package com.banco.operacionesbancarias.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CuentaDTO {

	private Long id;

	@NotBlank(message = "{numeroCuenta.notblank}")
	@Size(max = 20, message = "{numeroCuenta.size}")
	private String numeroCuenta;

	@NotBlank(message = "{tipoCuenta.notblank}")
	@Size(max = 20, message = "{tipoCuenta.size}")
	private String tipoCuenta;

	@NotNull(message = "{saldoInicial.notnull}")
	@PositiveOrZero(message = "{saldoInicial.positiveOrZero}")
	private BigDecimal saldoInicial;

	@NotNull(message = "{estado.notnull}")
	private Boolean estado;

	@NotBlank(message = "{clienteId.notblank}")
	@Size(max = 20, message = "{clienteId.size}")
	private String clienteId;

	@NotBlank(message = "{clienteNombre.notblank}")
	@Size(max = 100, message = "{clienteNombre.size}")
	private String clienteNombre;

	private LocalDateTime fechaCreacion;
}
