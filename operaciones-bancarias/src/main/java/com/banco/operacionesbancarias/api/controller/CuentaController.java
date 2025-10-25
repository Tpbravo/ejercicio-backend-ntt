package com.banco.operacionesbancarias.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.banco.operacionesbancarias.api.dto.CuentaDTO;
import com.banco.operacionesbancarias.domain.model.Cuenta;
import com.banco.operacionesbancarias.domain.service.CuentaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor
public class CuentaController {

	private final CuentaService cuentaService;

	@PostMapping
	public ResponseEntity<Cuenta> crear(@Valid @RequestBody CuentaDTO dto) {
		Cuenta cuenta = new Cuenta();
		cuenta.setNumeroCuenta(dto.getNumeroCuenta());
		cuenta.setTipoCuenta(dto.getTipoCuenta());
		cuenta.setSaldoInicial(dto.getSaldoInicial());
		cuenta.setEstado(dto.getEstado());
		cuenta.setClienteId(dto.getClienteId());
		cuenta.setClienteNombre(dto.getClienteNombre());

		return ResponseEntity.ok(cuentaService.crear(cuenta));
	}

	@GetMapping
	public ResponseEntity<List<Cuenta>> listar() {
		return ResponseEntity.ok(cuentaService.listar());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Cuenta> buscarPorId(@PathVariable Long id) {
		return cuentaService.buscarPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PutMapping("/{id}")
	public ResponseEntity<Cuenta> actualizar(@PathVariable Long id, @Valid @RequestBody CuentaDTO dto) {
		Cuenta cuenta = new Cuenta();
		cuenta.setTipoCuenta(dto.getTipoCuenta());
		cuenta.setEstado(dto.getEstado());
		return ResponseEntity.ok(cuentaService.actualizar(id, cuenta));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		cuentaService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
