package com.banco.gestionclientes.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClienteDTO {

    private Long id;

    @NotBlank(message = "{nombre.notblank}")
    @Size(max = 100, message = "{nombre.size}")
    private String nombre;

    @NotBlank(message = "{genero.notblank}")
    @Size(max = 50, message = "{genero.size}")
    private String genero;

    @NotNull(message = "{edad.notnull}")
    @Min(value = 0, message = "{edad.min}")
    private Integer edad;

    @NotBlank(message = "{identificacion.notblank}")
    @Size(max = 20, message = "{identificacion.size}")
    private String identificacion;

    @Size(max = 150, message = "{direccion.size}")
    private String direccion;

    @Size(max = 20, message = "{telefono.size}")
    private String telefono;

    @Size(max = 20, message = "{clienteId.size}")
    private String clienteId;

    @NotBlank(message = "{contrasena.notblank}")
    @Size(min = 6, message = "{contrasena.size}")
    private String contrasena;

    @NotNull(message = "{estado.notnull}")
    private Boolean estado;
}
