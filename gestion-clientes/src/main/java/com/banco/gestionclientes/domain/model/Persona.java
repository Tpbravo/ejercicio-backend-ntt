package com.banco.gestionclientes.domain.model;

import com.banco.gestionclientes.domain.model.enums.Genero;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "persona", schema = "core")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Persona {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "{nombre.notblank}")
	@Size(max = 100, message = "{nombre.size}")
	@Column(nullable = false, length = 100)
	private String nombre;

	@NotNull(message = "{genero.notnull}")
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private Genero genero;

	@NotNull(message = "{edad.notnull}")
	@Min(value = 0, message = "{edad.min}")
	@Column(nullable = false)
	private Integer edad;

	@NotBlank(message = "{identificacion.notblank}")
	@Size(max = 20, message = "{identificacion.size}")
	@Column(nullable = false, unique = true, length = 20)
	private String identificacion;

	@Size(max = 150, message = "{direccion.size}")
	@Column(length = 150)
	private String direccion;

	@Size(max = 20, message = "{telefono.size}")
	@Column(length = 20)
	private String telefono;
}
