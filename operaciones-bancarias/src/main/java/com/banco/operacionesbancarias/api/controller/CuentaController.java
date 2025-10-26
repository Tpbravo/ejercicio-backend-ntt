package com.banco.operacionesbancarias.api.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.banco.operacionesbancarias.api.dto.CuentaDTO;
import com.banco.operacionesbancarias.api.mapper.CuentaMapper;
import com.banco.operacionesbancarias.api.response.ApiResponse;
import com.banco.operacionesbancarias.api.validation.ValidationGroups.OnCreate;
import com.banco.operacionesbancarias.api.validation.ValidationGroups.OnUpdate;
import com.banco.operacionesbancarias.domain.model.Cuenta;
import com.banco.operacionesbancarias.domain.service.CuentaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class CuentaController {

	private final CuentaService cuentaService;

	@PostMapping
	public ResponseEntity<ApiResponse<CuentaDTO>> crear(@Validated(OnCreate.class) @RequestBody CuentaDTO dto) {
		Cuenta cuenta = CuentaMapper.toEntity(dto);
		Cuenta saved = cuentaService.crear(cuenta);
		CuentaDTO response = CuentaMapper.toDTO(saved);

		URI location = URI.create("/api/operaciones/cuentas/" + saved.getId());
		return ResponseEntity.created(location).body(ApiResponse.ok(response, "Cuenta creada correctamente"));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<CuentaDTO>>> listar() {
		List<CuentaDTO> cuentas = cuentaService.listar().stream().map(CuentaMapper::toDTO).collect(Collectors.toList());

		return ResponseEntity.ok(ApiResponse.ok(cuentas, "Listado de cuentas obtenido correctamente"));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<CuentaDTO>> buscarPorId(@PathVariable Long id) {
		return cuentaService.buscarPorId(id).map(cuenta -> {
			CuentaDTO dto = CuentaMapper.toDTO(cuenta);
			ApiResponse<CuentaDTO> response = ApiResponse.ok(dto, "Cuenta obtenida correctamente");
			return ResponseEntity.ok(response);
		}).orElseGet(() -> {
			ApiResponse<CuentaDTO> error = ApiResponse.error("Cuenta no encontrada");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		});
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<CuentaDTO>> actualizar(@PathVariable Long id,
			@Validated(OnUpdate.class) @RequestBody CuentaDTO dto) {

		if (dto.getId() != null && !dto.getId().equals(id)) {
			throw new IllegalArgumentException("El ID del cuerpo no coincide con el ID de la URL");
		}
		Cuenta cuenta = CuentaMapper.toEntity(dto);
		Cuenta actualizada = cuentaService.actualizar(id, cuenta);
		CuentaDTO response = CuentaMapper.toDTO(actualizada);

		return ResponseEntity.ok(ApiResponse.ok(response, "Cuenta actualizada correctamente"));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
		cuentaService.eliminar(id);
		return ResponseEntity.ok(ApiResponse.ok(null, "Cuenta eliminada correctamente"));
	}

}
