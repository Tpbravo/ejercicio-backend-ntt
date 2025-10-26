package com.banco.operacionesbancarias.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.banco.operacionesbancarias.api.validation.ValidationGroups.OnCreate;
import com.banco.operacionesbancarias.api.validation.ValidationGroups.OnUpdate;
import com.banco.operacionesbancarias.domain.model.enums.TipoCuentaEnum;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CuentaDTO {

	private Long id;

	@NotBlank(message = "{numeroCuenta.notblank}", groups = OnCreate.class)
	@Size(max = 20, message = "{numeroCuenta.size}")
	private String numeroCuenta;

	@NotNull(message = "{tipoCuenta.notnull}", groups = { OnCreate.class, OnUpdate.class })
	private TipoCuentaEnum tipoCuenta;

	@NotNull(message = "{saldoInicial.notnull}", groups = { OnCreate.class })
	@PositiveOrZero(message = "{saldoInicial.positiveOrZero}", groups = { OnCreate.class, OnUpdate.class })
	private BigDecimal saldoInicial;

	@NotNull(message = "{estado.notnull}", groups = { OnCreate.class, OnUpdate.class })
	private Boolean estado;

	@NotNull(message = "{clienteId.notnull}", groups = OnCreate.class)
	private String clienteId;

	@Size(max = 100, message = "{clienteNombre.size}")
	private String clienteNombre;

	private LocalDateTime fechaCreacion;
}
