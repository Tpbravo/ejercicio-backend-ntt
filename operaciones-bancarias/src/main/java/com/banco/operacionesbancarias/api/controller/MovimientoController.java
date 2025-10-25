package com.banco.operacionesbancarias.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.banco.operacionesbancarias.api.dto.MovimientoDTO;
import com.banco.operacionesbancarias.domain.model.Cuenta;
import com.banco.operacionesbancarias.domain.model.Movimiento;
import com.banco.operacionesbancarias.domain.service.MovimientoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

	private final MovimientoService movimientoService;

	@PostMapping
	public ResponseEntity<Movimiento> registrar(@Valid @RequestBody MovimientoDTO dto) {
		Movimiento movimiento = Movimiento.builder().tipoMovimiento(dto.getTipoMovimiento()).valor(dto.getValor())
				.cuenta(Cuenta.builder().id(dto.getCuentaId()).build()).build();

		Movimiento registrado = movimientoService.registrarMovimiento(movimiento);
		return ResponseEntity.ok(registrado);
	}

	@GetMapping("/cuenta/{cuentaId}")
	public ResponseEntity<List<Movimiento>> listarPorCuenta(@PathVariable Long cuentaId) {
		return ResponseEntity.ok(movimientoService.listarPorCuenta(cuentaId));
	}
}
