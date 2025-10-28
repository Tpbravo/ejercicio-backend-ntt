package com.banco.operacionesbancarias.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClienteDTO {
	private Long id;
	private String nombre;
	private String identificacion;
	private Boolean estado;
	private String clienteId;


}
