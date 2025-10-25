package com.banco.operacionesbancarias.domain.service;

import java.util.List;

import com.banco.operacionesbancarias.domain.model.Movimiento;

public interface MovimientoService {
	Movimiento registrarMovimiento(Movimiento movimiento);

	List<Movimiento> listarPorCuenta(Long cuentaId);
}
