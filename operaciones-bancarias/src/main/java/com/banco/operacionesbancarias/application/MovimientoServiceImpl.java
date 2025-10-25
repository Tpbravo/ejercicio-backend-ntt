package com.banco.operacionesbancarias.application;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banco.operacionesbancarias.domain.model.Cuenta;
import com.banco.operacionesbancarias.domain.model.Movimiento;
import com.banco.operacionesbancarias.domain.service.MovimientoService;
import com.banco.operacionesbancarias.infrastructure.persistence.CuentaRepository;
import com.banco.operacionesbancarias.infrastructure.persistence.MovimientoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MovimientoServiceImpl implements MovimientoService {

	private final MovimientoRepository movimientoRepository;
	private final CuentaRepository cuentaRepository;

	@Override
	public Movimiento registrarMovimiento(Movimiento movimiento) {
		Cuenta cuenta = cuentaRepository.findById(movimiento.getCuenta().getId())
				.orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

		BigDecimal saldoActual = cuenta.getSaldoInicial();
		BigDecimal nuevoSaldo = saldoActual.add(movimiento.getValor());

		// Validar saldo disponible
		if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
			throw new RuntimeException("Saldo no disponible");
		}

		// Actualizar saldo de la cuenta
		cuenta.setSaldoInicial(nuevoSaldo);
		movimiento.setSaldoDisponible(nuevoSaldo);
		movimiento.setCuenta(cuenta);

		// Guardar entidades
		cuentaRepository.save(cuenta);
		return movimientoRepository.save(movimiento);
	}

	@Override
	public List<Movimiento> listarPorCuenta(Long cuentaId) {
		Cuenta cuenta = cuentaRepository.findById(cuentaId)
				.orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

		return movimientoRepository.findByCuentaOrderByFechaDesc(cuenta);
	}
}
