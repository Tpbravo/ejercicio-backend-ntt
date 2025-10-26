package com.banco.operacionesbancarias.domain.service;

import java.util.List;

import com.banco.operacionesbancarias.api.dto.MovimientoDTO;
import com.banco.operacionesbancarias.domain.model.Movimiento;

public interface MovimientoService {
	Movimiento registrarMovimiento(MovimientoDTO dto);

	List<Movimiento> listarPorCuenta(Long cuentaId);

	Movimiento actualizarMovimiento(Long id, MovimientoDTO dto);

	void eliminarMovimiento(Long id);

}
