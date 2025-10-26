package com.banco.operacionesbancarias.api.mapper;

import com.banco.operacionesbancarias.api.dto.MovimientoResponseDTO;
import com.banco.operacionesbancarias.domain.model.Movimiento;

public class MovimientoMapper {

	public static MovimientoResponseDTO toDTO(Movimiento movimiento) {
		return new MovimientoResponseDTO(movimiento.getId(), movimiento.getFecha(), movimiento.getTipoMovimiento(),
				movimiento.getValor(), movimiento.getSaldo(), movimiento.getCuenta().getNumeroCuenta());
	}
}