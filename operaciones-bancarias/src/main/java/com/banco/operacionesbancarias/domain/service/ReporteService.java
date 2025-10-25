package com.banco.operacionesbancarias.domain.service;

import java.time.LocalDate;
import java.util.List;

import com.banco.operacionesbancarias.api.dto.ReporteDTO;

public interface ReporteService {

	/**
	 * Genera un reporte consolidado de movimientos para un cliente entre fechas
	 * dadas (inclusive).
	 */
	List<ReporteDTO> generarReporte(String clienteId, LocalDate fechaInicio, LocalDate fechaFin);
}
