package com.banco.operacionesbancarias.api.mapper;

import com.banco.operacionesbancarias.api.dto.CuentaDTO;
import com.banco.operacionesbancarias.domain.model.Cuenta;

public class CuentaMapper {

	public static Cuenta toEntity(CuentaDTO dto) {
		if (dto == null)
			return null;

		Cuenta cuenta = new Cuenta();
		cuenta.setNumeroCuenta(dto.getNumeroCuenta());
		cuenta.setTipoCuenta(dto.getTipoCuenta());
		cuenta.setSaldoInicial(dto.getSaldoInicial());
		cuenta.setEstado(dto.getEstado());
		cuenta.setClienteId(dto.getClienteId());
		cuenta.setFechaCreacion(dto.getFechaCreacion());
		return cuenta;
	}

	public static CuentaDTO toDTO(Cuenta cuenta) {
		if (cuenta == null)
			return null;

		CuentaDTO dto = new CuentaDTO();
		dto.setId(cuenta.getId());
		dto.setNumeroCuenta(cuenta.getNumeroCuenta());
		dto.setTipoCuenta(cuenta.getTipoCuenta());
		dto.setSaldoInicial(cuenta.getSaldoInicial());
		dto.setEstado(cuenta.getEstado());
		dto.setClienteId(cuenta.getClienteId());
		dto.setClienteNombre(cuenta.getClienteNombre());
		dto.setFechaCreacion(cuenta.getFechaCreacion());
		return dto;
	}
}