package com.banco.operacionesbancarias.application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.banco.operacionesbancarias.api.dto.ReporteDTO;
import com.banco.operacionesbancarias.domain.model.Cuenta;
import com.banco.operacionesbancarias.domain.model.Movimiento;
import com.banco.operacionesbancarias.domain.service.ReporteService;
import com.banco.operacionesbancarias.infrastructure.persistence.CuentaRepository;
import com.banco.operacionesbancarias.infrastructure.persistence.MovimientoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

	private final CuentaRepository cuentaRepository;
	private final MovimientoRepository movimientoRepository;

	@Override
	public List<ReporteDTO> generarReporte(String clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
		List<Cuenta> cuentas = cuentaRepository.findAll().stream().filter(c -> c.getClienteId().equals(clienteId))
				.toList();

		List<ReporteDTO> resultado = new ArrayList<>();

		for (Cuenta cuenta : cuentas) {
			List<Movimiento> movimientos = movimientoRepository.findByCuentaOrderByFechaDesc(cuenta).stream()
					.filter(mov -> !mov.getFecha().toLocalDate().isBefore(fechaInicio)
							&& !mov.getFecha().toLocalDate().isAfter(fechaFin))
					.toList();

			for (Movimiento mov : movimientos) {
				ReporteDTO dto = ReporteDTO.builder().clienteNombre(cuenta.getClienteNombre())
						.numeroCuenta(cuenta.getNumeroCuenta()).tipoCuenta(cuenta.getTipoCuenta()).fecha(mov.getFecha())
						.tipoMovimiento(mov.getTipoMovimiento()).saldoInicial(cuenta.getSaldoInicial())
						.valor(mov.getValor()).saldoDisponible(mov.getSaldoDisponible()).build();

				resultado.add(dto);
			}
		}

		return resultado;
	}
}
