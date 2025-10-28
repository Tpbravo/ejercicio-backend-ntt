package com.banco.operacionesbancarias.application;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.banco.operacionesbancarias.api.dto.MovimientoResponseDTO;
import com.banco.operacionesbancarias.api.dto.ReporteDTO;
import com.banco.operacionesbancarias.api.mapper.MovimientoMapper;
import com.banco.operacionesbancarias.domain.model.Cuenta;
import com.banco.operacionesbancarias.domain.model.Movimiento;
import com.banco.operacionesbancarias.domain.service.CuentaService;
import com.banco.operacionesbancarias.domain.service.ReporteService;
import com.banco.operacionesbancarias.infrastructure.persistence.CuentaRepository;
import com.banco.operacionesbancarias.infrastructure.persistence.MovimientoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

	private final CuentaRepository cuentaRepository;
	private final MovimientoRepository movimientoRepository;
    private final CuentaService cuentaService; 


	@Override
	public List<ReporteDTO> generarReporte(String clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
		List<Cuenta> cuentas = cuentaRepository.findByClienteId(clienteId);

		if (cuentas.isEmpty()) {
			throw new RuntimeException("No se encontraron cuentas para el cliente " + clienteId);
		}

		return cuentas.stream().map((Cuenta cuenta) -> {
            cuentaService.enriquecerConNombreCliente(cuenta);

			// Recuperamos movimientos dentro del rango de fechas
			List<Movimiento> movimientos = movimientoRepository.findByCuentaAndFechaBetweenOrderByFechaAsc(cuenta,
					fechaInicio.atStartOfDay(), fechaFin.atTime(23, 59, 59));

			// Mapeamos los movimientos usando MovimientoMapper
			List<MovimientoResponseDTO> movimientosDTO = movimientos.stream()
					.map((Movimiento mov) -> MovimientoMapper.toDTO(mov)).collect(Collectors.toList());

			// Construimos el reporte por cuenta
			return ReporteDTO.builder().clienteNombre(cuenta.getClienteNombre()).numeroCuenta(cuenta.getNumeroCuenta())
					.tipoCuenta(cuenta.getTipoCuenta()).saldoInicial(cuenta.getSaldoInicial())
					.estado(cuenta.getEstado()).movimientos(movimientosDTO).build();
		}).collect(Collectors.toList());
	}

}
