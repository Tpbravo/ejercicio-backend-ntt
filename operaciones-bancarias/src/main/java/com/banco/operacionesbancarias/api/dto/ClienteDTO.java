package com.banco.operacionesbancarias.api.dto;

import lombok.Data;

@Data
public class ClienteDTO {
	private Long id;
	private String nombre;
	private String identificacion;
	private Boolean estado;

}
