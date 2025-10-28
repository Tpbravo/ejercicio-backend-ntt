package com.banco.gestionclientes.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banco.gestionclientes.api.dto.ClienteDTO;
import com.banco.gestionclientes.api.mapper.ClienteMapper;
import com.banco.gestionclientes.api.response.ApiResponse;
import com.banco.gestionclientes.api.validation.ValidationGroups.OnCreate;
import com.banco.gestionclientes.api.validation.ValidationGroups.OnUpdate;
import com.banco.gestionclientes.domain.model.Cliente;
import com.banco.gestionclientes.domain.service.ClienteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

	private final ClienteService clienteService;

	@PostMapping
	public ResponseEntity<ApiResponse<ClienteDTO>> guardar(@Validated(OnCreate.class) @RequestBody ClienteDTO dto) {
		Cliente cliente = ClienteMapper.toEntity(dto);
		Cliente saved = clienteService.guardar(cliente);
		ClienteDTO response = ClienteMapper.toDTO(saved);

		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response, "Cliente creado correctamente"));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<ClienteDTO>> actualizar(@PathVariable Long id,
			@Validated(OnUpdate.class) @RequestBody ClienteDTO dto) {
		Cliente actualizado = clienteService.actualizar(id, ClienteMapper.toEntity(dto));
		ClienteDTO response = ClienteMapper.toDTO(actualizado);

		return ResponseEntity.ok(ApiResponse.ok(response, "Cliente actualizado correctamente"));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<ClienteDTO>>> listar() {
		List<ClienteDTO> lista = clienteService.listar().stream().map(ClienteMapper::toDTO)
				.collect(Collectors.toList());
		return ResponseEntity.ok(ApiResponse.ok(lista, "Listado de clientes obtenido correctamente"));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<ClienteDTO>> obtenerPorId(@PathVariable Long id) {
		Cliente cliente = clienteService.buscarPorIdOrThrow(id);
		ClienteDTO dto = ClienteMapper.toDTO(cliente);
		return ResponseEntity.ok(ApiResponse.ok(dto, "Cliente obtenido correctamente"));
	}

	@GetMapping("/codigo/{clienteId}")
	public ResponseEntity<ApiResponse<ClienteDTO>> obtenerPorClienteId(@PathVariable String clienteId) {
		return clienteService.buscarPorClienteId(clienteId).map(cliente -> {
			ClienteDTO dto = ClienteMapper.toDTO(cliente);
			return ResponseEntity.ok(ApiResponse.ok(dto, "Cliente obtenido correctamente por clienteId"));
		}).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiResponse.error("Cliente no encontrado con clienteId: " + clienteId)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
		clienteService.eliminar(id);
		return ResponseEntity.ok(ApiResponse.ok(null, "Cliente eliminado correctamente"));
	}
}
