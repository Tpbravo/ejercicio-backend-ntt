package com.banco.gestionclientes.api.mapper;

import com.banco.gestionclientes.api.dto.ClienteDTO;
import com.banco.gestionclientes.domain.model.Cliente;

public class ClienteMapper {

	public static Cliente toEntity(ClienteDTO dto) {
		if (dto == null)
			return null;

		Cliente cliente = new Cliente();
		cliente.setNombre(dto.getNombre());
		cliente.setGenero(dto.getGenero());
		cliente.setEdad(dto.getEdad());
		cliente.setIdentificacion(dto.getIdentificacion());
		cliente.setDireccion(dto.getDireccion());
		cliente.setTelefono(dto.getTelefono());
		cliente.setClienteId(dto.getClienteId());
		cliente.setContrasena(dto.getContrasena());
		cliente.setEstado(dto.getEstado());
		return cliente;
	}

	public static ClienteDTO toDTO(Cliente cliente) {
		if (cliente == null)
			return null;

		ClienteDTO dto = new ClienteDTO();
		dto.setId(cliente.getId());
		dto.setNombre(cliente.getNombre());
		dto.setGenero(cliente.getGenero());
		dto.setEdad(cliente.getEdad());
		dto.setIdentificacion(cliente.getIdentificacion());
		dto.setDireccion(cliente.getDireccion());
		dto.setTelefono(cliente.getTelefono());
		dto.setClienteId(cliente.getClienteId());
		dto.setEstado(cliente.getEstado());
		return dto;
	}
}
