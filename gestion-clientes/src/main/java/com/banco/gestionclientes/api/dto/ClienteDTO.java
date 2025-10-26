package com.banco.gestionclientes.api.dto;

import com.banco.gestionclientes.api.validation.ValidationGroups.OnCreate;
import com.banco.gestionclientes.domain.model.enums.Genero;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClienteDTO {

	private Long id;

	@NotBlank(message = "{nombre.notblank}", groups = OnCreate.class)
	@Size(max = 100, message = "{nombre.size}")
	@Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ' -]+$", message = "{nombre.pattern}")
	private String nombre;

	@NotNull(message = "{genero.notnull}", groups = OnCreate.class)
	private Genero genero;

	@NotNull(message = "{edad.notnull}", groups = OnCreate.class)
	@Min(value = 0, message = "{edad.min}")
	private Integer edad;

	@NotBlank(message = "{identificacion.notblank}", groups = OnCreate.class)
	@Size(max = 20, message = "{identificacion.size}")
	private String identificacion;

	@Size(max = 150, message = "{direccion.size}")
	private String direccion;

	@Size(max = 20, message = "{telefono.size}")
	@Pattern(regexp = "^[0-9]+$", message = "{telefono.pattern}")
	private String telefono;

	@Size(max = 20, message = "{clienteId.size}")
	private String clienteId;

	@NotBlank(message = "{contrasena.notblank}", groups = OnCreate.class)
	@Size(min = 8, message = "{contrasena.size}", groups = OnCreate.class)
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&#._-])[A-Za-z\\d@$!%*?&#._-]{8,}$", message = "{contrasena.pattern}", groups = OnCreate.class)
	private String contrasena;

	@NotNull(message = "{estado.notnull}", groups = OnCreate.class)
	private Boolean estado;
}
