package com.banco.operacionesbancarias.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.banco.operacionesbancarias.api.dto.MovimientoDTO;
import com.banco.operacionesbancarias.api.dto.MovimientoResponseDTO;
import com.banco.operacionesbancarias.api.mapper.MovimientoMapper;
import com.banco.operacionesbancarias.api.response.ApiResponse;
import com.banco.operacionesbancarias.domain.model.Movimiento;
import com.banco.operacionesbancarias.domain.service.MovimientoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

	private final MovimientoService movimientoService;

	@PostMapping
	public ResponseEntity<ApiResponse<MovimientoResponseDTO>> registrar(@Validated @RequestBody MovimientoDTO dto) {

		Movimiento movimiento = movimientoService.registrarMovimiento(dto);
		MovimientoResponseDTO response = MovimientoMapper.toDTO(movimiento);

		ApiResponse<MovimientoResponseDTO> apiResponse = ApiResponse.ok(response,
				"Movimiento registrado correctamente");

		return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
	}

	@GetMapping("/cuenta/{cuentaId}")
	public ResponseEntity<ApiResponse<List<MovimientoResponseDTO>>> listarPorCuenta(@PathVariable Long cuentaId) {

		List<MovimientoResponseDTO> movimientos = movimientoService.listarPorCuenta(cuentaId).stream()
				.map(MovimientoMapper::toDTO).collect(Collectors.toList());

		ApiResponse<List<MovimientoResponseDTO>> apiResponse = ApiResponse.ok(movimientos,
				"Movimientos obtenidos correctamente");

		return ResponseEntity.ok(apiResponse);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<MovimientoResponseDTO>> actualizarMovimiento(@PathVariable Long id,
			@Validated @RequestBody MovimientoDTO dto) {

		Movimiento actualizado = movimientoService.actualizarMovimiento(id, dto);
		MovimientoResponseDTO response = MovimientoMapper.toDTO(actualizado);

		ApiResponse<MovimientoResponseDTO> apiResponse = ApiResponse.ok(response,
				"Movimiento actualizado correctamente");

		return ResponseEntity.ok(apiResponse);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> eliminarMovimiento(@PathVariable Long id) {
		movimientoService.eliminarMovimiento(id);

		ApiResponse<Void> apiResponse = ApiResponse.ok(null, "Movimiento eliminado correctamente");
		return ResponseEntity.ok(apiResponse);
	}
}