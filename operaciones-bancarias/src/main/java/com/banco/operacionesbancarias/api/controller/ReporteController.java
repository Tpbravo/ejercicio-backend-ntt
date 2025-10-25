package com.banco.operacionesbancarias.api.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.banco.operacionesbancarias.api.dto.ReporteDTO;
import com.banco.operacionesbancarias.domain.service.ReporteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

	private final ReporteService reporteService;

	@GetMapping
	public ResponseEntity<List<ReporteDTO>> generarReporte(@RequestParam String clienteId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

		List<ReporteDTO> reporte = reporteService.generarReporte(clienteId, fechaInicio, fechaFin);
		return ResponseEntity.ok(reporte);
	}
}
