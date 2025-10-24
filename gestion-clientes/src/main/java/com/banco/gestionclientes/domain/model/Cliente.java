package com.banco.gestionclientes.domain.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cliente", schema = "core")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class Cliente extends Persona {
	@NotBlank(message = "{clienteId.notblank}")
	@Size(max = 20, message = "{clienteId.size}")
	@Column(name = "cliente_id", nullable = false, unique = true, length = 20)
	private String clienteId;

	@NotBlank(message = "{contrasena.notblank}")
	@Size(min = 6, message = "{contrasena.size}")
	@Column(nullable = false, length = 100)
	private String contrasena;

	@NotNull(message = "{estado.notnull}")
	@Column(nullable = false)
	private boolean estado;

	@Column(name = "fecha_creacion", nullable = false, updatable = false)
	private LocalDateTime fechaCreacion;

	@PrePersist
	public void prePersist() {
		this.fechaCreacion = LocalDateTime.now();
	}
}
