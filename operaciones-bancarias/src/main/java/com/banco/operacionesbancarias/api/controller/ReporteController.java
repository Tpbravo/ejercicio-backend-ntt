package com.banco.operacionesbancarias.api.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.banco.operacionesbancarias.api.dto.ReporteDTO;
import com.banco.operacionesbancarias.api.response.ApiResponse;
import com.banco.operacionesbancarias.domain.service.ReporteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
@Slf4j
public class ReporteController {

	private final ReporteService reporteService;

	@GetMapping
	public ResponseEntity<ApiResponse<List<ReporteDTO>>> generarReporte(@RequestParam String clienteId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

		log.info("Generando reporte para cliente={} desde {} hasta {}", clienteId, fechaInicio, fechaFin);

		if (fechaInicio.isAfter(fechaFin)) {
			log.warn("Rango de fechas inválido: fechaInicio={} > fechaFin={}", fechaInicio, fechaFin);
			ApiResponse<List<ReporteDTO>> error = ApiResponse
					.error("La fecha de inicio no puede ser posterior a la fecha fin");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		}

		List<ReporteDTO> reporte = reporteService.generarReporte(clienteId, fechaInicio, fechaFin);
		ApiResponse<List<ReporteDTO>> response = ApiResponse.ok(reporte, "Reporte generado correctamente");

		return ResponseEntity.ok(response);
	}
}
