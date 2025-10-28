package com.banco.operacionesbancarias.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.banco.operacionesbancarias.domain.model.enums.TipoCuentaEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "cuenta", schema = "core", uniqueConstraints = { @UniqueConstraint(columnNames = "numero_cuenta") })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cuenta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "{numeroCuenta.notblank}")
	@Size(max = 20, message = "{numeroCuenta.size}")
	@Column(name = "numero_cuenta", nullable = false, unique = true, length = 20)
	private String numeroCuenta;

	@NotNull(message = "{tipoCuenta.notnull}")
	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_cuenta", nullable = false, length = 20)
	private TipoCuentaEnum tipoCuenta;

	@NotNull(message = "{saldoInicial.notnull}")
	@PositiveOrZero(message = "{saldoInicial.positiveOrZero}")
	@Digits(integer = 12, fraction = 2, message = "{saldoInicial.digits}")
	@Column(name = "saldo_inicial", nullable = false, precision = 15, scale = 2)
	private BigDecimal saldoInicial;

	@NotNull(message = "{estado.notnull}")
	@Column(nullable = false)
	private Boolean estado;

	@NotNull(message = "{clienteId.notnull}")
	@Column(name = "cliente_id", nullable = false)
	private String clienteId;

	@Column(name = "fecha_creacion", nullable = false, updatable = false)
	private LocalDateTime fechaCreacion;

	@Transient
	private String clienteNombre;

	@PrePersist
	public void prePersist() {
		if (this.fechaCreacion == null) {
			this.fechaCreacion = LocalDateTime.now();
		}
		if (this.estado == null) {
			this.estado = true;
		}
		if (this.saldoInicial == null) {
			this.saldoInicial = BigDecimal.ZERO;
		}
	}

}
