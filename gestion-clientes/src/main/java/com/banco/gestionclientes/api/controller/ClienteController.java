package com.banco.gestionclientes.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banco.gestionclientes.api.dto.ClienteDTO;
import com.banco.gestionclientes.domain.model.Cliente;
import com.banco.gestionclientes.domain.service.ClienteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {
	private final ClienteService clienteService;

	@PostMapping
	public ResponseEntity<ClienteDTO> guardar(@RequestBody ClienteDTO dto) {
		Cliente cliente = toEntity(dto);
		Cliente saved = clienteService.guardar(cliente);
		return ResponseEntity.ok(toDTO(saved));
	}

	@GetMapping
	public ResponseEntity<List<ClienteDTO>> listar() {
		List<ClienteDTO> lista = clienteService.listar().stream().map(this::toDTO).toList();
		return ResponseEntity.ok(lista);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ClienteDTO> obtenerPorId(@PathVariable Long id) {
		return clienteService.buscarPorId(id).map(cliente -> ResponseEntity.ok(toDTO(cliente)))
				.orElse(ResponseEntity.notFound().build());
	}

	@PutMapping("/{id}")
	public ResponseEntity<ClienteDTO> actualizar(@PathVariable Long id, @RequestBody ClienteDTO dto) {
		Cliente actualizado = clienteService.actualizar(id, toEntity(dto));
		return ResponseEntity.ok(toDTO(actualizado));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		clienteService.eliminar(id);
		return ResponseEntity.noContent().build();
	}

	private Cliente toEntity(ClienteDTO dto) {
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

	private ClienteDTO toDTO(Cliente cliente) {
		ClienteDTO dto = new ClienteDTO();
		dto.setId(cliente.getId());
		dto.setNombre(cliente.getNombre());
		dto.setGenero(cliente.getGenero());
		dto.setEdad(cliente.getEdad());
		dto.setIdentificacion(cliente.getIdentificacion());
		dto.setDireccion(cliente.getDireccion());
		dto.setTelefono(cliente.getTelefono());
		dto.setClienteId(cliente.getClienteId());
		dto.setContrasena(cliente.getContrasena());
		dto.setEstado(cliente.getEstado());
		return dto;
	}
}
