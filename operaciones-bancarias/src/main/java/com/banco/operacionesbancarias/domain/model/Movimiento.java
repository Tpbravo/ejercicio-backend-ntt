package com.banco.operacionesbancarias.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.banco.operacionesbancarias.domain.model.enums.TipoMovimientoEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "movimiento", schema = "core")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movimiento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "{fecha.notnull}")
	@Column(nullable = false)
	private LocalDateTime fecha;

	@NotNull(message = "{tipoMovimiento.notnull}")
	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_movimiento", nullable = false, length = 20)
	private TipoMovimientoEnum tipoMovimiento;

	@NotNull(message = "{valor.notnull}")
	@Digits(integer = 12, fraction = 2, message = "{valor.digits}")
	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal valor;

	@NotNull(message = "{saldoDisponible.notnull}")
	@Digits(integer = 12, fraction = 2, message = "{saldoDisponible.digits}")
	@Column(name = "saldo_disponible", nullable = false, precision = 15, scale = 2)
	private BigDecimal saldo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cuenta_id", nullable = false)
	private Cuenta cuenta;

	@PrePersist
	public void prePersist() {
		if (this.fecha == null) {
			this.fecha = LocalDateTime.now();
		}
	}
}
